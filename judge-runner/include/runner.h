#ifndef _RUNNER_H
#define _RUNNER_H 1

#include <sstream>
#include <string>

const int AC = 0;
const int WA = 1;
const int TLE = 2;
const int MLE = 3;
const int OLE = 4;

/**
 * @brief 资源限制
 */
struct Config {
    long timeout;       // 运行时间(ms)
    long memory;        // 内存限制，用于判断是否超出限制(KB)
    long max_memory;    // 实际内存限制，超过此限制程序会中断(KB)
    long output_size;   // 输出限制(KB)

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

    std::string to_json() const;
};

class Runner {
private:
    static void set_limit(const Config &config);

    static int run_cmd(char **args, const Config &config);

    static Result watch_result(pid_t pid, const Config &config);

public:
    static Result run(char *args[], const Config &config);
};

#endif //_RUNNER_H