#ifndef RUNNER_H
#define RUNNER_H 1

#include <fstream>
#include <vector>

#define AC 1
#define TLE 2
#define MLE 3
#define PA 4
#define WA 5
#define RE 7
#define IE 8
#define OLE 9

#define ALARM_SECONDS 20

// Special Judge Define
typedef bool (*spj_func)(std::ifstream *, std::ifstream *, std::ifstream *);

/**
 * 运行配置(资源限制和文件路径)
 */
struct Config {
    long timeout{};            // 运行时间(μs)
    long memory{};             // 内存限制(KiB)，用于判断是否超出限制
    long output_size{};        // 输出限制(KiB)
    int cpu = 0;               // CPU 核心，将进程绑定到指定核心减少切换
    int std_in{};              // 输入文件 fd(用于重定向 stdin)
    int std_out{};             // 用户输出 fd(用于重定向 stdout)
    int in_fd{};               // 输入文件 fd
    int out_fd{};              // 用户输出 fd(用于对比)
    int ans_fd{};              // 正确输出 fd(用于对比)
    std::ifstream *in{};
    std::ifstream *out{};
    std::ifstream *ans{};
};

/**
 * 每个测试点的运行结果
 */
struct Result {
    int status;
    long time;       // μs
    long mem;        // KiB
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
    long long time;                         // μs
    long long memory;                       // KiB
    char err[128];                          // 错误信息
    std::vector<std::string> detail;        // 存储通过的测试点文件名
};

class Runner {
private:
    int root_fd;                // 根目录 fd
    char *argv[16]{};           // exec 参数
    char *work_dir;             // 工作目录(用户程序所在目录)
    char *data_dir;             // 测试数据目录
    Config config;
    void *dl_handler = nullptr; // SPJ 动态链接库
    spj_func spj = nullptr;     // SPJ 函数
private:
    [[nodiscard]] int set_cpu() const;

    void run_cmd();

    void watch_result(pid_t pid, Result *res);

    void run(Result *res);

public:
    Runner(char *cmd, char *work_dir, char *data_dir, Config &config);

    ~Runner();

    RTN judge();
};

#endif // RUNNER_H
