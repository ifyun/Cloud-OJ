#include <sys/resource.h>
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <sys/prctl.h>
#include <fcntl.h>
#include <dirent.h>
#include <unistd.h>
#include <iostream>
#include <cstring>
#include <dlfcn.h>
#include "runner.h"
#include "utils.h"
#include "env_setup.h"
#include "common.h"

Runner::Runner(char *cmd, char *work_dir, char *data_dir, int language, Config &config) {
    split(this->argv, cmd, "@");
    root_fd = open("/", O_RDONLY);

    this->work_dir = work_dir;
    this->data_dir = data_dir;
    this->syscall_rule = new SyscallRule(language);
    this->config = config;
    this->config.in = new std::ifstream;
    this->config.out = new std::ifstream;
    this->config.ans = new std::ifstream;

    char spj_path[192];
    sprintf(spj_path, "%s/%s", data_dir, "spj.so");
    dl_handler = dlopen(spj_path, RTLD_NOW);

    if (dl_handler != nullptr) {
        spj = (spj_func) dlsym(dl_handler, "spj");
    }
}

Runner::~Runner() {
    delete this->syscall_rule;
    close(root_fd);

    if (dl_handler != nullptr) {
        dlclose(dl_handler);
    }

    delete config.in;
    delete config.out;
    delete config.ans;
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

    dup2(config.std_in, STDIN_FILENO);
    dup2(config.std_out, STDOUT_FILENO);
    dup2(config.std_out, STDERR_FILENO);

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
void Runner::watch_result(pid_t pid, Result *res) {
    int syscall_number = 0;
    int status;
    struct rusage ru{};
    res->mem = 0;

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
        res->mem = ru.ru_minflt * getpagesize() / 1024;
        // ? 检查内存占用
        if (res->mem > config.memory) {
            kill(pid, SIGUSR1);
        }

        res->time = (ru.ru_utime.tv_sec + ru.ru_stime.tv_sec) * 1000000
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
                    res->status = MLE;
                    return;
                case SIGALRM:
                    sprintf(res->err, "SIGALRM(CPU: %.2fms)", (double) res->time / 1000.0);
                    res->status = RE;
                    return;
                case SIGSEGV:
                    sprintf(res->err, "段错误(SIGSEGV)");
                    res->status = RE;
                    return;
                case SIGSYS:
                    sprintf(res->err, "非法调用(SYSCALL: %d)", syscall_number);
                    res->status = RE;
                    return;
                default:
                    sprintf(res->err, "程序停止(%d: %s)", stop_sig, strsignal(stop_sig));
                    res->status = RE;
                    return;
            }
        }

        if (WIFSIGNALED(status)) {
            // ? 子进程收到信号终止
            int sig = WTERMSIG(status);
            switch (sig) {
                case SIGUSR1:
                    res->status = MLE;
                    break;
                case SIGXCPU:
                    res->status = TLE;
                    break;
                case SIGXFSZ:
                    res->status = OLE;
                    break;
                case SIGKILL:
                    sprintf(res->err, "程序终止(SIGKILL)");
                    res->status = RE;
                    return;
                case SIGSEGV:
                    sprintf(res->err, "段错误(SIGSEGV)");
                    res->status = RE;
                    return;
                case SIGSYS:
                    sprintf(res->err, "非法调用(SYSCALL: %d)", syscall_number);
                    res->status = RE;
                    return;
                default:
                    sprintf(res->err, "程序终止(%d: %s)", sig, strsignal(sig));
                    res->status = RE;
                    return;
            }
        }

        if (WIFEXITED(status)) {
            // ? 子进程退出
            auto exit_code = WEXITSTATUS(status);

            if (res->time > config.timeout) {
                res->status = TLE;
            } else if (exit_code != 0) {
                sprintf(res->err, "非零退出(%d)", exit_code);
                res->status = RE;
                return;
            } else {
                res->status = Utils::compare(config, spj) ? AC : WA;
            }
        }

        ptrace(PTRACE_SYSCALL, pid, 0, 0);
    }
}

void Runner::run(Result *res) {
    if (chdir(work_dir) != 0) {
        res->status = IE;
        sprintf(res->err, "chdir: %s", strerror(errno));
        fprintf(stderr, "%s", res->err);
    }

    chroot(work_dir);
    pid_t pid = vfork();

    if (pid < 0) {
        res->status = IE;
        sprintf(res->err, "fork: %s", strerror(errno));
        fprintf(stderr, "%s", res->err);
    } else if (pid == 0) {
        // child process
        prctl(PR_SET_PDEATHSIG, SIGKILL);
        run_cmd();
    } else {
        watch_result(pid, res);

        close(config.std_in);
        close(config.std_out);
        close(config.in_fd);
        close(config.out_fd);
        close(config.ans_fd);
        config.in->close();
        config.out->close();
        config.ans->close();
        // exit sandbox env
        fchdir(root_fd);
        chroot(".");
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

        // * N 组数据(N >= 1)
        for (auto i = 0; i < input_files.size(); i++) {
            sprintf(out_path, "%s/%d.out", work_dir, i + 1);
            config.in_fd = open(input_files[i].c_str(), O_RDONLY, 0644);
            config.std_in = open(input_files[i].c_str(), O_RDONLY, 0644);
            config.std_out = open(out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);
            config.out_fd = open(out_path, O_RDONLY, 0644);
            config.ans_fd = open(output_files[i].c_str(), O_RDONLY, 0644);
            config.in->open(input_files[i], std::ios_base::in);
            config.out->open(out_path, std::ios_base::in);
            config.ans->open(output_files[i], std::ios_base::in);

            Result res{};
            run(&res);
            results.push_back(res);

            if (res.status == RE || res.status == IE) {
                rtn.result = res.status;
                strcpy(rtn.err, res.err);
                goto exit;
            }
        }
    } else if (!output_files.empty()) {
        // * 没有输入数据，读取第一个 .out 文件
        sprintf(out_path, "%s/%s", work_dir, "1.out");
        config.in_fd = open("/dev/null", O_RDONLY, 0644);
        config.std_in = open("/dev/null", O_RDONLY, 0644);
        config.std_out = open(out_path, O_WRONLY | O_CREAT | O_TRUNC, 0644);
        config.out_fd = open(out_path, O_RDONLY, 0644);
        config.ans_fd = open(output_files[0].c_str(), O_RDONLY, 0644);
        config.in->open("/dev/null", std::ios_base::in);
        config.out->open(out_path, std::ios_base::in);
        config.ans->open(output_files[0], std::ios_base::in);

        Result res{};
        run(&res);
        results.push_back(res);

        if (res.status == RE || res.status == IE) {
            rtn.result = res.status;
            strcpy(rtn.err, res.err);
            goto exit;
        }
    } else {
        rtn = {.result = IE, .err = "无测试数据"};
    }

    exit:
    Utils::calc_results(rtn, results, output_files);
    end_env(work_dir);
    return rtn;
}
