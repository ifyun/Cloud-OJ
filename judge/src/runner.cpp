#include <sys/resource.h>
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <fcntl.h>
#include <dirent.h>
#include <unistd.h>
#include <iostream>
#include <cstring>
#include "runner.h"
#include "utils.h"
#include "env_setup.h"
#include "common.h"

Runner::Runner(char *cmd, char *work_dir, char *data_dir, int language, Config &config) {
    split(this->argv, cmd, "@");
    this->work_dir = work_dir;
    this->data_dir = data_dir;
    this->syscall_rule = new SyscallRule(language);
    this->config = config;
    root_fd = open("/", O_RDONLY);
}

Runner::~Runner() {
    delete this->syscall_rule;
    close(root_fd);
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

inline void Runner::run_cmd() {
    struct rlimit rl{};

    rl.rlim_cur = (config.timeout / 1000000) + 1;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = config.output_size << 10;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_FSIZE, &rl);

    if (set_cpu() != 0) {
        goto exit;
    }

#ifdef DEBUG
    cpu_set_t mask;
    sched_getaffinity(0, sizeof(cpu_set_t), &mask);
    printf("PID: %d, CPU_COUNT: %d\n", getpid(), CPU_COUNT(&mask));
#endif

    dup2(config.in, STDIN_FILENO);
    dup2(config.out, STDOUT_FILENO);
    dup2(config.out, STDERR_FILENO);

    setuid(65534);
    alarm(ALARM_SECONDS);
    ptrace(PTRACE_TRACEME, 0, 0, 0);
    // * exec 成功不会返回
    if (execvp(argv[0], argv) != 0) {
        goto exit;
    }

    exit:
    _exit(IE);
}

/**
 * @brief 等待结果
 */
Result Runner::watch_result(pid_t pid) {
    int syscall_number = 0;
    int status;
    struct rusage ru{};
    struct Result res{.mem = 0};

    while (wait4(pid, &status, 0, &ru) > 0) {
        int stop_sig;
        struct user_regs_struct regs{};

        if (syscall_number == 0) {
            // ? 检查系统调用
            ptrace(PTRACE_GETREGS, pid, 0, &regs);
            if ((syscall_number = syscall_rule->check(&regs)) != 0) {
                kill(pid, SIGSYS);
            }
        }

        // * Code + Data + Stack
        res.mem = ru.ru_minflt * getpagesize() / 1024;
        // ? 检查内存占用
        if (res.mem > config.memory) {
            kill(pid, SIGUSR1);
        }

        res.time = (ru.ru_utime.tv_sec + ru.ru_stime.tv_sec) * 1000000
                   + (ru.ru_utime.tv_usec + ru.ru_stime.tv_usec);

        if (WIFSTOPPED(status)
            && (stop_sig = WSTOPSIG(status)) != SIGTRAP
            && stop_sig != SIGURG
            && stop_sig != SIGCHLD) {
            // ? 子进程停止
            // * 忽略 ptrace 产生的 SIGTRAP 信号
            // * 忽略 SIGURG 信号 (Golang Urgent I/O condition)
            // ! 没有 return 会进入死循环
            switch (stop_sig) {
                case SIGUSR1:
                    res.status = MLE;
                    return res;
                case SIGALRM:
                    sprintf(res.err, "SIGALRM(CPU: %.2fms)", (int) res.time / 1000.0);
                    res.status = RE;
                    return res;
                case SIGSEGV:
                    sprintf(res.err, "段错误(SIGSEGV)");
                    res.status = RE;
                    return res;
                case SIGSYS:
                    sprintf(res.err, "非法调用(SYSCALL: %d)", syscall_number);
                    res.status = RE;
                    return res;
                default:
                    sprintf(res.err, "程序停止(%d: %s)", stop_sig, strsignal(stop_sig));
                    res.status = RE;
                    return res;
            }
        }

        if (WIFSIGNALED(status)) {
            // ? 子进程收到信号终止
            int sig = WTERMSIG(status);
            switch (sig) {
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
                    res.status = RE;
                    return res;
                case SIGSEGV:
                    sprintf(res.err, "段错误(SIGSEGV)");
                    res.status = RE;
                    return res;
                case SIGSYS:
                    sprintf(res.err, "非法调用(SYSCALL: %d)", syscall_number);
                    res.status = RE;
                    return res;
                default:
                    sprintf(res.err, "程序终止(%d: %s)", sig, strsignal(sig));
                    res.status = RE;
                    return res;
            }
        }

        if (WIFEXITED(status)) {
            // ? 子进程退出
            auto exit_code = WEXITSTATUS(status);

            if (res.time > config.timeout) {
                res.status = TLE;
            } else if (exit_code != 0) {
                sprintf(res.err, "非零退出(%d)", exit_code);
                res.status = RE;
                return res;
            } else {
                res.status = Utils::compare(config.user_out, config.expect_out) ? AC : WA;
            }
        }

        ptrace(PTRACE_SYSCALL, pid, 0, 0);
    }

    return res;
}

Result Runner::run() {
    if (chdir(work_dir) != 0) {
        Result res = {.status=IE};
        sprintf(res.err, "chdir: %s", strerror(errno));
        fprintf(stderr, "%s", res.err);
        return res;
    }

    chroot(work_dir);
    pid_t pid = vfork();

    if (pid < 0) {
        Result res = {.status=IE};

        sprintf(res.err, "fork: %s", strerror(errno));
        fprintf(stderr, "%s", res.err);

        return res;
    } else if (pid == 0) {
        // child process
        prctl(PR_SET_PDEATHSIG, SIGKILL);
        run_cmd();
    } else {
        Result result = watch_result(pid);

        close(config.in);
        close(config.out);
        close(config.user_out);
        close(config.expect_out);
        // exit sandbox env
        fchdir(root_fd);
        chroot(".");

        return result;
    }
}

RTN Runner::judge() {
    RTN rtn{};
    std::vector<std::string> input_files;
    std::vector<std::string> output_files;
    std::vector<Result> results;
    DIR *dp = opendir(work_dir);

    if (dp == nullptr) {
        rtn = {.result = IE, .err = "工作目录不存在"};
        goto exit;
    }

    closedir(dp);
    setup_env(work_dir);

    try {
        // * 获取测试数据
        input_files = Utils::get_files(data_dir, ".in");
        output_files = Utils::get_files(data_dir, ".out");
    } catch (const std::invalid_argument &error) {
        rtn.result = IE;
        strcpy(rtn.err, error.what());
        goto exit;
    }

    char out_path[128];     // 用户输出文件路径

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            rtn = {.result = IE, .err = "测试数据文件数量不一致"};
            goto exit;
        }

        // * 多组数据
        for (auto i = 0; i < input_files.size(); i++) {
            sprintf(out_path, "%s/%d.out", work_dir, i + 1);
            config.in = open(input_files[i].c_str(), O_RDONLY, 0644);
            config.out = open(out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);
            config.user_out = open(out_path, O_RDONLY, 0644);
            config.expect_out = open(output_files[i].c_str(), O_RDONLY, 0644);

            Result res = run();

            if (res.status == RE || res.status == IE) {
                rtn.result = res.status;
                strcpy(rtn.err, res.err);
                goto exit;
            }

            results.push_back(res);
        }
    } else if (!output_files.empty()) {
        // * 没有输入数据，读取第一个 .out 文件
        sprintf(out_path, "%s/%s", work_dir, "1.out");
        config.in = open("/dev/null", O_RDONLY, 0644);
        config.out = open(out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);
        config.user_out = open(out_path, O_RDONLY, 0644);
        config.expect_out = open(output_files[0].c_str(), O_RDONLY, 0644);

        Result res = run();

        if (res.status == RE || res.status == IE) {
            rtn.result = res.status;
            strcpy(rtn.err, res.err);
            goto exit;
        }

        results.push_back(res);
    } else {
        rtn = {.result = IE, .err = "无测试数据"};
    }

    exit:
    Utils::calc_results(rtn, results);
    end_env(work_dir);
    return rtn;
}
