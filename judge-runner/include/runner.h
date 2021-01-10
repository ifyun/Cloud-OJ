#ifndef _RUNNER_H
#define _RUNNER_H

#endif //_RUNNER_H

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
struct resource_limit {
    long timeout;       // 运行时间(ms)
    long memory;        // 内存限制，用于判断是否超出限制(KB)
    long max_memory;    // 实际内存限制，超过此限制程序会中断
    long output_size;   // 输出限制(KB)
};

struct result {
    int status;
    long timeUsed;
    long memUsed;

    std::string to_json() const;
};

class runner {
private:
    static void set_limit(const resource_limit &limit);

    static int run_cmd(char **args, const resource_limit &limit, const std::string &in, const std::string &out);

    static result watch_result(pid_t pid, const resource_limit &limit, const std::string &out,
                               const std::string &expect);

public:
    static result run(char *args[], const resource_limit &limit, const std::string &in, const std::string &out,
                      const std::string &expect);
};
