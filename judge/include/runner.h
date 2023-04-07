#ifndef RUNNER_H
#define RUNNER_H 1

#include <string>
#include "syscall_rule.h"

#define AC 0
#define WA 1
#define TLE 2
#define MLE 3
#define OLE 4
#define PA 5

#define ALARM_SECONDS 7    // ALARM 触发时间

extern pid_t child_pid;

void alarm_child(int sig);

/**
 * 运行配置(资源限制和文件路径)
 */
struct Config {
    long timeout;           // 运行时间(us)
    long memory;            // 内存限制，用于判断是否超出限制(MB)
    long output_size;       // 输出限制(MB)
    int cpu = 0;            // CPU 核心，将进程绑定到指定核心减少切换
    int in;                 // 输入文件 fd
    int out;                // 输出文件 fd(实际输出)
    char out_path[64];      // 输出文件路径(实际输出)
    char expect_path[64];   // 输出文件路径(正确输出)
};

/**
 * 每个测试点的运行结果
 */
struct Result {
    int code;
    int status;
    long timeUsed;
    long memUsed;
    char err[32];
};

/**
 * 最终结果
 */
struct RTN {
    int code = 0;           // 0 -> 无错误
    std::string err;        // 错误信息
    std::string result;     // 判题结果(JSON String)
};

class Runner {
private:
    char *argv[16]{};
    char *work_dir;             // 工作目录(用户程序所在目录)
    char *data_dir;             // 测试数据目录
    std::string tmp_data_dir;   // 沙盒环境中的测试数据目录
    Config config;
    SyscallRule *syscall_rule;
private:
    int set_cpu() const;

    /**
    * 设置当前进程的资源限制
    */
    void set_limit() const;

    /**
    * 使用 execvp 执行 Solution
    */
    int run_cmd();

    Result watch_result(pid_t pid);

    /**
    * 创建子进程判题
    */
    Result run();

public:
    Runner(char *cmd, char *work_dir, char *data_dir, int language, Config &config);

    ~Runner();

    RTN judge();
};

#endif // RUNNER_H
