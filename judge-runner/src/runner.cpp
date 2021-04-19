#include <fcntl.h>
#include <unistd.h>
#include <iostream>
#include <sys/resource.h>
#include <sys/wait.h>
#include <cstring>
#include <dirent.h>
#include "runner.h"
#include "utils.h"
#include "env_setup.h"

/**
 * @brief 退出 chroot 并清理环境
 * @param fd 文件描述符，切换到此目录
 */
void clean_up(const int &fd, const std::string &work_dir, const std::string &random_dir) {
    fchdir(fd);
    chroot(".");
    end_env(work_dir.c_str(), random_dir.c_str());
}

void split(char **arr, char *str, const char *del) {
    char *s;
    s = strtok(str, del);

    while (s != nullptr) {
        *arr++ = s;
        s = strtok(nullptr, del);
    }

    *arr = nullptr;
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

    rl.rlim_cur = 1;
    rl.rlim_max = rl.rlim_cur;

    setrlimit(RLIMIT_NPROC, &rl);
}

/**
 * @brief 使用 execvp 执行命令判题
 * @param args 命令 + 参数
 * @param config 资源限制
 * @return 0 -> 正常返回，1 -> 非正常返回
 */
int Runner::run_cmd(char **args, const Config &config) {
    auto new_stdin = open(config.in.c_str(), O_RDONLY, 0644);
    auto new_stdout = open(config.out.c_str(), O_WRONLY | O_CREAT | O_TRUNC, 0644);

    set_limit(config);

    if (new_stdin != -1 && new_stdout != -1) {
        dup2(new_stdin, fileno(stdin));
        dup2(new_stdout, fileno(stdout));

        // 降权
        if (setuid(65534) == -1) {
            std::cerr << "Permission Denied.\n";
            return 1;
        }

        if (execvp(args[0], args) == -1) {
            std::cerr << "Failed to start process\n";
            return 1;
        }

        close(new_stdin);
        close(new_stdout);
    } else if (new_stdin == -1) {
        std::cerr << "Failed to open " << config.in << std::endl;
        return 1;
    } else {
        std::cerr << "Failed to open " << config.out << std::endl;
        return 1;
    }

    return 0;
}

/**
 * @brief 等待结果，遇到异常直接退出
 */
Result Runner::watch_result(pid_t pid, const Config &config, int root_fd,
                            const std::string &work_dir, const std::string &random_dir) {
    int status;
    struct rusage ru{};
    struct Result res{};

    if (wait4(pid, &status, 0, &ru) == -1) {
        std::cerr << "Wait process failed.\n";
        clean_up(root_fd, work_dir, random_dir);
        exit(RUNTIME_ERROR);
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
                    std::cerr << "Invalid access\n";
                    clean_up(root_fd, work_dir, random_dir);
                    exit(RUNTIME_ERROR);
                }
                break;
            case SIGALRM:
                std::cerr << "SIGALRM.\n";
                exit(RUNTIME_ERROR);
            case SIGXCPU:
                res.status = TLE;
                break;
            case SIGXFSZ:
                res.status = OLE;
                break;
            case SIGKILL:
                std::cerr << "Killed\n";
                clean_up(root_fd, work_dir, random_dir);
                exit(RUNTIME_ERROR);
            default:
                clean_up(root_fd, work_dir, random_dir);
                exit(RUNTIME_ERROR);
        }
    } else {
        if (res.timeUsed > config.timeout) {
            res.status = TLE;
        } else if (res.memUsed > config.memory) {
            res.status = MLE;
        } else if (WEXITSTATUS(status) != 0) {
            std::cerr << "Non-zero exit\n";
            clean_up(root_fd, work_dir, random_dir);
            exit(RUNTIME_ERROR);
        } else {
            res.status = utils::diff(config.out, config.expect) ? WA : AC;
        }
    }

    return res;
}

/**
 * @brief 创建子进程判题
 * @param args 命令 + 参数
 */
Result Runner::run(char **args, const Config &config, int root_fd,
                   const std::string &work_dir, const std::string &random_dir) {
    pid_t pid = vfork();

    if (pid < 0) {
        std::cerr << "Failed to create process\n";
        exit(JUDGE_ERROR);
    } else if (pid == 0) {
        // 最长等待 6s，避免 sleep()
        alarm(6);
        auto status = run_cmd(args, config);
        exit(status);
    } else {
        Result result = watch_result(pid, config, root_fd, work_dir, random_dir);
        return result;
    }
}

/**
 * @brief 入口
 */
RTN exec(char *argv[]) {
    RTN rtn;

    if (getuid() != 0) {
        std::cerr << "Required root permission.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    char *cmd[32];
    split(cmd, argv[1], "@");

    Config config = {
            .timeout=strtol(argv[2], nullptr, 10),
            .memory=strtol(argv[3], nullptr, 10) << 10,
            .max_memory=strtol(argv[4], nullptr, 10) << 10,
            .output_size=strtol(argv[5], nullptr, 10) << 10
    };

    std::string work_dir = argv[6];
    std::string data_dir = argv[7];

    if (opendir(work_dir.c_str()) == nullptr) {
        std::cerr << "Work dir does not exist.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    // 获取根目录的 FD
    auto root_fd = open("/", O_RDONLY);

    if (root_fd == -1) {
        std::cerr << "Open root failed.\n";
        rtn.code = JUDGE_ERROR;
        return rtn;
    }

    // 创建运行环境，random_dir 为测试数据挂载点，根目录(/)变为 work_dir
    std::string random_dir = setup_env(work_dir.c_str(), data_dir.c_str());

    if (chroot(work_dir.c_str()) != 0) {
        std::cerr << "Chroot failed.\n";
        rtn.code = JUDGE_ERROR;
        clean_up(root_fd, work_dir, random_dir);
        return rtn;
    }

    if (chdir("/") != 0) {
        std::cerr << "Chroot failed.\n";
        rtn.code = JUDGE_ERROR;
        clean_up(root_fd, work_dir, random_dir);
        return rtn;
    }

    std::vector<std::string> input_files;
    std::vector<std::string> output_files;

    try {
        input_files = utils::get_files(random_dir, "in");       // 获取输入数据
        output_files = utils::get_files(random_dir, "out");     // 获取输出数据
    } catch (const std::invalid_argument &error) {
        std::cerr << error.what();
        rtn.code = JUDGE_ERROR;
        clean_up(root_fd, work_dir, random_dir);
        return rtn;
    }

    std::vector<Result> results;

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            std::cerr << "The number of input and output files is not equal\n";
            rtn.code = JUDGE_ERROR;
            clean_up(root_fd, work_dir, random_dir);
            return rtn;
        }

        for (auto i = 0; i < input_files.size(); i++) {
            std::string real_output_file = std::to_string(i + 1) + ".out";  // 实际输出文件

            config.in = input_files[i];
            config.out = real_output_file;
            config.expect = output_files[i];

            Result res = Runner::run(cmd, config, root_fd, work_dir, random_dir);

            results.push_back(res);
        }
    } else if (!output_files.empty()) {
        // 没有输入数据，读取第一个 .out 文件
        std::string expect = utils::get_files(random_dir, ".out")[0];

        config.in = "/dev/null";
        config.out = "1.out";
        config.expect = expect;

        Result res = Runner::run(cmd, config, root_fd, work_dir, random_dir);

        results.push_back(res);
    } else {
        std::cerr << "Test data required\n";
        rtn.code = JUDGE_ERROR;
    }

    if (!results.empty()) {
        rtn.result = utils::write_result(results);
    }

    clean_up(root_fd, work_dir, random_dir);
    return rtn;
}
