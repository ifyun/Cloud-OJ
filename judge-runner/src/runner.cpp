#include <fcntl.h>
#include <unistd.h>
#include <iostream>
#include <sys/resource.h>
#include <sys/wait.h>
#include "runner.h"
#include "utils.h"

std::string Result::to_json() const {
    std::stringstream ss;
    ss << "{"
       << "\"status\": " << status << ", "
       << "\"timeUsed\": " << timeUsed << ", "
       << "\"memUsed\": " << memUsed
       << "}";
    return ss.str();
}

/**
 * @brief 设置当前进程的资源限制
 */
void Runner::set_limit(const Config &config) {
    struct rlimit rl{};

    rl.rlim_cur = (config.timeout / 1000) + 1;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = config.max_memory << 10;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_DATA, &rl);

    rl.rlim_cur = config.output_size << 10;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_FSIZE, &rl);
}

/**
 * @brief 使用 execvp 执行命令判题
 * @param args 命令 + 参数
 * @param config 资源限制
 * @return 0 -> 正常返回，1 -> 非正常返回
 */
int Runner::run_cmd(char **args, const Config &config) {
    auto new_stdin = open(config.in.c_str(), O_RDONLY, 0644);
    auto new_stdout = open(config.out.c_str(), O_RDWR | O_CREAT, 0644);

    set_limit(config);

    if (new_stdin != -1 && new_stdout != -1) {
        dup2(new_stdin, fileno(stdin));
        dup2(new_stdout, fileno(stdout));

        if (execvp(args[0], args) == -1) {
            std::cerr << "[ERROR] Failed to start process\n";
            return 1;
        }

        close(new_stdin);
        close(new_stdout);
    } else if (new_stdin == -1) {
        std::cerr << "[ERROR] Failed to open " << config.in << std::endl;
        return 1;
    } else {
        std::cerr << "[ERROR] Failed to open " << config.out << std::endl;
        return 1;
    }

    return 0;
}

/**
 * 计算结果
 */
Result Runner::watch_result(pid_t pid, const Config &config) {
    int status;
    struct rusage ru{};
    struct Result res{};

    if (wait4(pid, &status, 0, &ru) == -1) {
        exit(EXIT_FAILURE);
    }

    res.timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 +
                   ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
    res.memUsed = ru.ru_maxrss;

    if (WIFSIGNALED(status)) {
        auto signal = WTERMSIG(status);
        switch (signal) {
            case SIGSEGV:
                if (res.memUsed > config.memory) {
                    res.status = MLE;
                } else {
                    std::cerr << "[ERROR] Invalid access\n";
                    exit(EXIT_FAILURE);
                }
                break;
            case SIGALRM:
            case SIGXCPU:
                res.status = TLE;
                break;
            case SIGXFSZ:
                res.status = OLE;
                break;
            case SIGKILL:
                std::cerr << "[ERROR] Killed\n";
                exit(EXIT_FAILURE);
            default:
                exit(EXIT_FAILURE);
        }
    } else {
        if (res.timeUsed > config.timeout) {
            res.status = TLE;
        } else if (res.memUsed > config.memory) {
            res.status = MLE;
        } else if (WEXITSTATUS(status) != 0) {
            std::cerr << "[ERROR] Non-zero exit\n";
            exit(EXIT_FAILURE);
        } else {
            res.status = utils::diff(config.out, config.expect) ? WA : AC;
        }
    }

    return res;
}

/**
 * @brief 创建子进程判题
 * @param args 命令 + 参数
 * @param config 资源限制
 * @return 判题结果
 */
Result Runner::run(char **args, const Config &config) {
    pid_t pid = fork();

    if (pid < 0) {
        std::cerr << "[ERROR] Failed to create process\n";
        exit(EXIT_FAILURE);
    } else if (pid == 0) {
        auto status = run_cmd(args, config);
        exit(status);
    } else {
        return watch_result(pid, config);
    }
}
