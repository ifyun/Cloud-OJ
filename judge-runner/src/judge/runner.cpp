#include <sys/resource.h>
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <iostream>
#include <cstring>
#include "runner.h"
#include "utils.h"
#include "env_setup.h"
#include "common.h"

int root_fd;
pid_t child_pid;

Runner::Runner(char *cmd, char *work_dir, char *data_dir, int language, Config &config) {
    split(this->argv, cmd, "@");
    this->work_dir = work_dir;
    this->data_dir = data_dir;
    this->syscall_checker = new SyscallChecker(language);
    this->config = config;
}

Runner::~Runner() {
    delete this->syscall_checker;
    close(config.in);
    close(config.out);
}

void alarm_child(int sig) {
    kill(child_pid, sig);
}

/**
 * @brief 设置当前进程的 CPU 核心
 */
int Runner::set_cpu() const {
    cpu_set_t mask;

    CPU_ZERO(&mask);
    CPU_SET(config.cpu, &mask);

    return sched_setaffinity(0, sizeof(cpu_set_t), &mask);
}

inline void Runner::set_limit() const {
    struct rlimit rl{};

    rl.rlim_cur = (config.timeout / 1000) + 1;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = config.output_size << 10;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_FSIZE, &rl);
}

inline int Runner::run_cmd() {
    set_limit();
    dup2(config.in, fileno(stdin));
    dup2(config.out, fileno(stdout));
    setuid(99);
    ptrace(PTRACE_TRACEME, 0, 0, 0);

    if (execvp(argv[0], argv) == -1) {
        perror("无法创建进程");
        return JUDGE_ERROR;
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
        // 检查系统调用
        int syscall_number;
        struct user_regs_struct regs{};
        ptrace(PTRACE_GETREGS, pid, 0, &regs);

        if ((syscall_number = syscall_checker->check(&regs)) != 0) {
            kill(pid, SIGSYS);
        }

        res.memUsed = ru.ru_minflt * getpagesize() / 1024;

        if (res.memUsed > config.memory) {
            kill(pid, SIGUSR1);
        }

        res.timeUsed = (ru.ru_utime.tv_sec + ru.ru_stime.tv_sec) * 1000 +
                       (ru.ru_utime.tv_usec + ru.ru_stime.tv_usec) / 1000;

        int stop_signal;
        if (WIFSTOPPED(status) && (stop_signal = WSTOPSIG(status)) != SIGTRAP) {
            // 子进程暂停(忽略 ptrace 产生的 SIGTRAP 信号)
            // 这个分支必须有 return 或 exit，否则会进入死循环
            switch (stop_signal) {
                case SIGUSR1:
                    res.status = MLE;
                    return res;
                case SIGALRM:
                    sprintf(res.err, "SIGALRM(CPU Time: %dms)", (int) res.timeUsed);
                    res.code = RUNTIME_ERROR;
                    return res;
                case SIGSEGV:
                    sprintf(res.err, "段错误.\n");
                    res.code = RUNTIME_ERROR;
                    return res;
                case SIGSYS:
                    sprintf(res.err, "非法调用(SYSCALL: %d)", syscall_number);
                    res.code = RUNTIME_ERROR;
                    return res;
                default:
                    sprintf(res.err, "程序停止(SIGNAL: %d)", stop_signal);
                    res.code = RUNTIME_ERROR;
                    return res;
            }
        } else if (WIFSIGNALED(status)) {
            // 子进程终止
            auto signal = WTERMSIG(status);
            switch (signal) {
                case SIGUSR1:
                    res.status = MLE;
                    break;
                case SIGXCPU:
                    res.status = TLE;
                    break;
                case SIGXFSZ:
                    res.status = OLE;
                    break;
                case SIGKILL:
                    sprintf(res.err, "程序终止(SIGKILL)");
                    res.code = RUNTIME_ERROR;
                    return res;
                case SIGSEGV:
                    sprintf(res.err, "段错误(SIGSEGV)");
                    res.code = RUNTIME_ERROR;
                    return res;
                default:
                    sprintf(res.err, "程序终止(SIG%s)", strsignal(signal));
                    res.code = RUNTIME_ERROR;
                    return res;
            }
        } else if (WIFEXITED(status)) {
            // 子进程退出
            if (res.timeUsed > config.timeout) {
                res.status = TLE;
            } else if (WEXITSTATUS(status) != 0) {
                sprintf(res.err, "非零退出");
                res.code = WEXITSTATUS(status);
                return res;
            } else {
                res.status = Utils::diff(config.out_path, config.expect_path) ? WA : AC;
            }
        } else {
            // 跟踪系统调用
            ptrace(PTRACE_SYSCALL, pid, 0, 0);
        }
    }

    return res;
}

Result Runner::run() {
    // create sandbox
    if (chdir(work_dir) != 0) {
        Result res = {.status=JUDGE_ERROR};
        sprintf(res.err, "chdir: %s", strerror(errno));
        fprintf(stderr, "%s", res.err);
        return res;
    }

    chroot(work_dir);
    pid_t pid = fork();

    if (pid < 0) {
        Result res = {.status=JUDGE_ERROR};
        sprintf(res.err, "fork: %s", strerror(errno));
        fprintf(stderr, "%s", res.err);
        return res;
    } else if (pid == 0) {
        _exit(run_cmd());
    } else {
        child_pid = pid;
        fchdir(root_fd);
        chroot(".");
        signal(SIGALRM, alarm_child);
        alarm(ALARM_SECONDS);
        Result result = watch_result(pid);
        alarm(0);
        return result;
    }
}

RTN Runner::judge() {
    RTN rtn;
    std::vector<std::string> input_files;
    std::vector<std::string> output_files;
    std::vector<Result> results;
    DIR *dp = opendir(work_dir);

    if (dp == nullptr) {
        rtn = {JUDGE_ERROR, "工作目录不存在"};
        goto exit;
    }

    closedir(dp);

    root_fd = open("/", O_RDONLY);

    if (set_cpu() == -1) {
        rtn = {JUDGE_ERROR, strerror(errno)};
        goto exit;
    }

    setup_env(work_dir);

    try {
        input_files = Utils::get_files(data_dir, "in");       // 获取输入数据
        output_files = Utils::get_files(data_dir, "out");     // 获取输出数据
    } catch (const std::invalid_argument &error) {
        rtn = {JUDGE_ERROR, error.what()};
        goto exit;
    }

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            rtn = {JUDGE_ERROR, "测试数据文件数量不一致"};
            goto exit;
        }

        for (auto i = 0; i < input_files.size(); i++) {
            strcpy(config.expect_path, output_files[i].c_str());
            sprintf(config.out_path, "%s/%d.out", work_dir, i + 1);
            config.in = open(input_files[i].c_str(), O_RDONLY, 0644);
            config.out = open(config.out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);

            Result res = run();

            if (res.code != 0) {
                rtn = {res.code, res.err};
                goto exit;
            }

            results.push_back(res);
        }
    } else if (!output_files.empty()) {
        // 没有输入数据，读取第一个 .out 文件
        strcpy(config.expect_path, output_files[0].c_str());
        sprintf(config.out_path, "%s/%s", work_dir, "1.out");
        config.in = open("/dev/null", O_RDONLY, 0644);
        config.out = open(config.out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);

        Result res = run();

        if (res.code != 0) {
            rtn = {res.code, res.err};
            goto exit;
        }

        results.push_back(res);
    } else {
        rtn = {JUDGE_ERROR, "无测试数据"};
    }

    exit:
    Utils::write_result(rtn, results, work_dir);
    end_env(work_dir);
    return rtn;
}
