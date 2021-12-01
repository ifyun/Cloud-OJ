#include <fcntl.h>
#include <unistd.h>
#include <iostream>
#include <sys/resource.h>
#include <sys/wait.h>
#include <dirent.h>
#include <sys/ptrace.h>
#include "runner.h"
#include "utils.h"
#include "env_setup.h"

pid_t child_pid;
int root_fd;

void alarm_child() {
    kill(child_pid, SIGALRM);
}

Runner::Runner(char **cmd, char *work_dir, char *data_dir, Config &config) {
    this->cmd = cmd;
    this->work_dir = work_dir;
    this->data_dir = data_dir;
    this->config = config;
}

/**
 * 设置当前进程的 CPU 核心
 */
inline void Runner::set_cpu() const {
    cpu_set_t mask;

    CPU_ZERO(&mask);
    CPU_SET(config.cpu, &mask);

    if (sched_setaffinity(0, sizeof(cpu_set_t), &mask) == -1) {
        perror("set affinity failed: ");
        exit(JUDGE_ERROR);
    }
}

/**
 * @brief 退出 chroot 并清理环境
 */
inline void Runner::clean_up() {
    fchdir(root_fd);
    chroot(".");
    end_env(work_dir, tmp_data_dir.c_str());
}

/**
 * @brief 设置当前进程的资源限制
 */
void Runner::set_limit() const {
    struct rlimit rl{};

    rl.rlim_cur = (config.timeout / 1000) + 1;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = config.output_size << 10;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_FSIZE, &rl);

    if (config.proc_count > 0) {
        rl.rlim_cur = config.proc_count;
        rl.rlim_max = rl.rlim_cur;

        setrlimit(RLIMIT_NPROC, &rl);
    }
}

/**
 * @brief 使用 execvp 执行命令判题
 * @return 0 -> 正常返回，1 -> 非正常返回
 */
int Runner::run_cmd() {
    auto new_stdin = open(config.in.c_str(), O_RDONLY, 0644);
    auto new_stdout = open(config.out.c_str(), O_WRONLY | O_CREAT | O_TRUNC, 0644);

    set_limit();

    if (new_stdin != -1 && new_stdout != -1) {
        dup2(new_stdin, fileno(stdin));
        dup2(new_stdout, fileno(stdout));

        if (setuid(99) == -1) {
            std::cerr << "setuid failed.\n";
            return 1;
        }

        ptrace(PTRACE_TRACEME, 0, 0, 0);

        if (execvp(cmd[0], cmd) == -1) {
            perror("无法创建进程: ");
            return 1;
        }

        close(new_stdin);
        close(new_stdout);
    } else if (new_stdin == -1) {
        std::string s = "无法打开 " + config.in + ": ";
        perror(s.c_str());
        return 1;
    } else {
        std::string s = "无法打开 " + config.out + ": ";
        perror(s.c_str());
        return 1;
    }

    return 0;
}

/**
 * @brief 等待结果
 */
Result Runner::watch_result(pid_t pid) {
    int status;
    struct rusage ru{};
    struct Result res{.memUsed = 0};

    while (wait4(pid, &status, 0, &ru) > 0) {
        int stop_sig;
        if (WIFSTOPPED(status) && (stop_sig = WSTOPSIG(status)) != SIGTRAP) {
            // 子进程收到信号停止(ptrace 会产生 SIGTRAP 信号，忽略它)
            // 这个分支必须有 return 或 exit，否则会进入死循环
            switch (stop_sig) {
                case SIGALRM:
                    fprintf(stderr, "超出最大时间限制: %ds.\n", MAX_WAIT_SECONDS);
                    clean_up();
                    exit(RUNTIME_ERROR);
                case SIGSEGV:
                    res.status = MLE;
                    res.memUsed = ru.ru_minflt * getpagesize() / 1024;
                    return res;
                default:
                    fprintf(stderr, "进程已退出(SIG: %d).\n", stop_sig);
                    clean_up();
                    exit(RUNTIME_ERROR);
            }
        } else if (WIFSIGNALED(status)) {
            // 子进程收到信号终止
            res.timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 +
                           ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
            auto signal = WTERMSIG(status);
            switch (signal) {
                case SIGSEGV:
                    res.status = MLE;
                    break;
                case SIGXCPU:
                    res.status = TLE;
                    break;
                case SIGXFSZ:
                    res.status = OLE;
                    break;
                case SIGKILL:
                default:
                    fprintf(stderr, "进程终止(SIG: %d).\n", signal);
                    clean_up();
                    exit(RUNTIME_ERROR);
            }
        } else if (WIFEXITED(status)) {
            // 子进程退出
            res.timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 +
                           ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
            if (res.timeUsed > config.timeout) {
                res.status = TLE;
            } else if (WEXITSTATUS(status) != 0) {
                std::cerr << "非零退出.\n";
                clean_up();
                exit(RUNTIME_ERROR);
            } else {
                res.status = Utils::diff(config.out, config.expect) ? WA : AC;
            }
        } else {
            res.memUsed = ru.ru_minflt * getpagesize() / 1024;

            if (res.memUsed > config.memory) {
                // 超出内存限制，发送 SIGSEGV 停止子进程
                kill(pid, SIGSEGV);
            }

            ptrace(PTRACE_SYSCALL, pid, 0, 0);
        }
    }

    return res;
}

/**
 * @brief 创建子进程判题
 */
Result Runner::run() {
    pid_t pid = fork();
    child_pid = pid;

    if (pid < 0) {
        perror("创建进程失败: ");
        clean_up();
        exit(JUDGE_ERROR);
    } else if (pid == 0) {
        auto status = run_cmd();
        exit(status);
    } else {
        signal(SIGALRM, (void (*)(int)) alarm_child);
        alarm(MAX_WAIT_SECONDS);
        Result result = watch_result(pid);
        alarm(0);
        return result;
    }
}

/**
 * @brief 入口
 */
RTN Runner::exec() {
    RTN rtn;
    set_cpu();

    if (getuid() != 0) {
        std::cerr << "Permission Denied.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    if (opendir(work_dir) == nullptr) {
        std::cerr << "工作目录不存在.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    // 获取根目录的文件描述符，用于退出 chroot
    if ((root_fd = open("/", O_RDONLY)) == -1) {
        std::cerr << "open root dir failed.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    // region 创建沙盒环境，根目录(/)变为 work_dir
    tmp_data_dir = setup_env(work_dir, data_dir);

    if (chroot(work_dir) != 0) {
        std::cerr << "chroot failed.\n";
        rtn.code = JUDGE_ERROR;
        clean_up();
        return rtn;
    }

    if (chdir("/") != 0) {
        std::cerr << "chroot failed.\n";
        rtn.code = JUDGE_ERROR;
        clean_up();
        return rtn;
    }
    // endregion

    std::vector<std::string> input_files;
    std::vector<std::string> output_files;

    try {
        input_files = Utils::get_files(tmp_data_dir, "in");       // 获取输入数据
        output_files = Utils::get_files(tmp_data_dir, "out");     // 获取输出数据
    } catch (const std::invalid_argument &error) {
        std::cerr << error.what();
        rtn.code = JUDGE_ERROR;
        clean_up();
        return rtn;
    }

    std::vector<Result> results;

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            std::cerr << "测试数据文件数量不一致.\n";
            rtn.code = JUDGE_ERROR;
            clean_up();
            return rtn;
        }

        // 多组数据，逐个运行
        for (auto i = 0; i < input_files.size(); i++) {
            config.in = input_files[i];
            config.out = std::to_string(i + 1) + ".out";
            config.expect = output_files[i];

            Result res = run();
            results.push_back(res);
        }
    } else if (!output_files.empty()) {
        // 没有输入数据，读取第一个 .out 文件
        std::string expect = Utils::get_files(tmp_data_dir, ".out")[0];

        config.in = "/dev/null";
        config.out = "1.out";
        config.expect = expect;

        Result res = run();
        results.push_back(res);
    } else {
        std::cerr << "无测试数据.\n";
        rtn.code = JUDGE_ERROR;
    }

    if (!results.empty()) {
        rtn.result = Utils::write_result(results);
    }

    clean_up();
    return rtn;
}
