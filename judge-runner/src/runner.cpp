#include "runner.h"
#include "utils.h"
#include <fcntl.h>
#include <unistd.h>
#include <iostream>
#include <sys/resource.h>
#include <sys/wait.h>

std::string result::to_json() const {
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
void runner::set_limit(const resource_limit &limit) {
    struct rlimit rl{};

    rl.rlim_cur = (limit.timeout / 1000) + 1;
    rl.rlim_max = rl.rlim_cur + 1;

    setrlimit(RLIMIT_CPU, &rl);

    rl.rlim_cur = limit.max_memory * 1024;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_DATA, &rl);

    rl.rlim_cur = limit.output_size * 1024;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_FSIZE, &rl);
}

/**
 * @brief 使用 execvp 执行命令判题
 * @param args 命令 + 参数
 * @param limit 资源限制
 * @param in 输入数据
 * @param out 实际输出
 * @return 0 -> 正常返回，1 -> 非正常返回
 */
int runner::run_cmd(char **args, const resource_limit &limit, const std::string &in, const std::string &out) {
    auto new_stdin = open(in.c_str(), O_RDONLY, 0644);
    auto new_stdout = open(out.c_str(), O_RDWR | O_CREAT, 0644);

    set_limit(limit);

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
        std::cerr << "[ERROR] Failed to open " << in << std::endl;
        return 1;
    } else {
        std::cerr << "[ERROR] Failed to open " << out << std::endl;
        return 1;
    }

    return 0;
}

/**
 * 计算结果
 */
result runner::watch_result(pid_t pid, const resource_limit &limit, const std::string &out, const std::string &expect) {
    int status;
    struct rusage ru{};
    struct result res{};

    if (wait4(pid, &status, 0, &ru) == -1) {
        // Runtime error
        exit(EXIT_FAILURE);
    }

    res.timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 +
                   ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
    res.memUsed = ru.ru_maxrss;

    if (WIFSIGNALED(status)) {
        auto signal = WTERMSIG(status);
        switch (signal) {
            case SIGSEGV:
                if (res.memUsed > limit.memory) {
                    res.status = MLE;
                } else {
                    std::cerr << "[ERROR] Invalid access to storage";
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
            default:
                // Runtime error
                exit(EXIT_FAILURE);
        }
    } else {
        if (res.timeUsed > limit.timeout) {
            res.status = TLE;
        } else if (res.memUsed > limit.memory) {
            res.status = MLE;
        } else if (WEXITSTATUS(status) != 0) {
            std::cerr << "[ERROR] Non-zero exit\n";
            exit(EXIT_FAILURE);
        } else {
            res.status = utils::diff(out, expect) ? WA : AC;
        }
    }

    return res;
}

/**
 * @brief 创建子进程判题
 * @param args 命令 + 参数
 * @param limit 资源限制
 * @param in 输入数据文件的路径，若没有则使用 "/dev/null"
 * @param out 保存实际输出的文件路径
 * @param expect 输出数据文件的路径
 * @return 判题结果
 */
result runner::run(char **args, const resource_limit &limit, const std::string &in, const std::string &out,
                   const std::string &expect) {
    pid_t pid = fork();

    if (pid < 0) {
        std::cerr << "[ERROR] Failed to create process\n";
        exit(EXIT_FAILURE);
    } else if (pid == 0) {
        auto status = run_cmd(args, limit, in, out);
        exit(status);
    } else {
        return watch_result(pid, limit, out, expect);
    }
}
