#ifndef _RUNNER_H
#define _RUNNER_H 1

#include <sstream>
#include <string>

const int AC = 0;
const int WA = 1;
const int TLE = 2;
const int MLE = 3;
const int OLE = 4;
const int PA = 5;

const int RUNTIME_ERROR = 1;
const int JUDGE_ERROR = -1;

const int MAX_WAIT_SECONDS = 15;    // ALARM 触发时间

/**
 * @brief 运行配置(资源限制和文件路径)
 */
struct Config {
    long timeout;       // 运行时间(ms)
    long memory;        // 内存限制，用于判断是否超出限制(MB)
    long output_size;   // 输出限制(MB)
    int cpu = 0;        // CPU 核心 ID，将进程绑定到指定核心减少切换
    int proc_count = 0; // 进程数限制

    std::string in;     // 输入文件路径
    std::string out;    // 输出文件路径(实际输出)
    std::string expect; // 输出文件路径(正确输出)
};

/**
 * @brief 运行结果
 */
struct Result {
    int status;
    long timeUsed;
    long memUsed;
};

struct RTN {
    int code = 0;
    std::string result;
};

class Runner {
private:
    char **cmd;
    char *work_dir;             // 工作目录(用户程序所在目录)
    char *data_dir;             // 测试数据目录
    std::string tmp_data_dir;   // 沙盒环境中的测试数据目录
    Config config;
private:
    void set_cpu() const;

    void set_limit() const;

    void clean_up();

    int run_cmd();

    Result watch_result(pid_t pid);

    Result run();

public:
    Runner(char *cmd[], char *work_dir, char *data_dir, Config &config);

    RTN exec();
};

#endif //_RUNNER_H