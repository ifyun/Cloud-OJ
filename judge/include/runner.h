#ifndef RUNNER_H
#define RUNNER_H 1

#include <string>
#include "syscall_rule.h"

#define AC 1
#define TLE 2
#define MLE 3
#define PA 4
#define WA 5
#define RE 7
#define IE 8
#define OLE 9

#define ALARM_SECONDS 20

typedef bool (*spj_func)(int, int, int);

/**
 * 运行配置(资源限制和文件路径)
 */
struct Config {
    long timeout{};            // 运行时间(μs)
    long memory{};             // 内存限制(MiB)，用于判断是否超出限制
    long output_size{};        // 输出限制(MiB)
    int cpu = 0;               // CPU 核心，将进程绑定到指定核心减少切换
    int input{};               // 输入文件 fd
    int std_in{};              // 输入文件 fd(用于重定向)
    int std_out{};             // 输出文件 fd(用户输出，用于重定向)
    int user_out{};            // 输出文件 fd(用户输出，用于对比)
    int expect_out{};          // 输出文件 fd(正确输出，用于对比)
};

/**
 * 每个测试点的运行结果
 */
struct Result {
    int status;
    long time;
    long mem;
    char err[128];
};

/**
 * 最终结果
 */
struct RTN {
    int result;
    int total;
    int passed;
    double passRate;
    long long time;     // μs
    long long memory;   // KiB
    char err[128];      // 错误信息
};

class Runner {
private:
    int root_fd;                // 根目录 fd
    char *argv[16]{};
    char *work_dir;             // 工作目录(用户程序所在目录)
    char *data_dir;             // 测试数据目录
    Config config;
    SyscallRule *syscall_rule;
    void *dl_handler = nullptr;
    spj_func spj = nullptr;
private:
    [[nodiscard]] int set_cpu() const;

    void run_cmd();

    Result watch_result(pid_t pid);

    Result run();

public:
    Runner(char *cmd, char *work_dir, char *data_dir, int language, Config &config);

    ~Runner();

    RTN judge();
};

#endif // RUNNER_H
