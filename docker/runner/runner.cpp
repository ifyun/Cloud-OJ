#include <iostream>
#include <fstream>
#include <sstream>
#include <ios>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/ptrace.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/wait.h>

const unsigned EXIT_0 = 0;
const unsigned TLE = 1;
const unsigned MLE = 2;
const unsigned RE = 3;

struct result
{
    int status;
    int timeUsed;
    int memUsed;

    std::string toJson()
    {
        std::stringstream ss;
        ss << "{"
           << "\"status\": " << status << ","
           << "\"timeUsed\": " << timeUsed << ","
           << "\"memUsed\": " << memUsed
           << "}";
        return ss.str();
    }
};

/**
 * 设置资源限制
 */
void setProcessLimit(int timeLimit, int maxMemLimit)
{
    struct rlimit rl;
    /* set the time_limit (Second)*/
    rl.rlim_cur = (timeLimit / 1000) + 1;
    rl.rlim_max = rl.rlim_cur + 1;

    setrlimit(RLIMIT_CPU, &rl);

    /* set the maxMemLimit (Byte)*/
    rl.rlim_cur = maxMemLimit * 1024;
    rl.rlim_max = rl.rlim_cur + 1024;

    setrlimit(RLIMIT_DATA, &rl);
}

/**
 * 执行命令
 */
void run_cmd(char *args[], long timeLimit, int maxMemLimit, std::string in, std::string out)
{
    int newstdin = open(in.c_str(), O_RDONLY, 0644);
    int newstdout = open(out.c_str(), O_RDWR | O_CREAT, 0644);

    setProcessLimit(timeLimit, maxMemLimit);

    if (newstdin != -1 && newstdout != -1)
    {
        dup2(newstdin, fileno(stdin));
        dup2(newstdout, fileno(stdout));

        if (execvp(args[0], args) == -1)
        {
            std::cerr << "Failed to start the process!\n";
        }

        close(newstdin);
        close(newstdout);
    }
    else
    {
        std::cerr << "Failed to open file!\n";
    }
}

/**
 * 检查结果
 */
void watch_result(pid_t pid, int timeLimit, int memLimit, struct result *rest)
{
    int status;
    struct rusage ru;

    if (wait4(pid, &status, 0, &ru) == -1)
    {
        std::cerr << "wait4 failure!\n";
    }

    rest->timeUsed = ru.ru_utime.tv_sec * 1000 + ru.ru_utime.tv_usec / 1000 + ru.ru_stime.tv_sec * 1000 + ru.ru_stime.tv_usec / 1000;
    rest->memUsed = ru.ru_maxrss;

    if (WIFSIGNALED(status))
    {
        switch (WTERMSIG(status))
        {
        case SIGSEGV:
            if (rest->memUsed > memLimit)
                rest->status = MLE;
            else
                rest->status = RE;
            break;
        case SIGALRM:
        case SIGXCPU:
            rest->status = TLE;
            break;
        default:
            rest->status = RE;
            break;
        }
    }
    else
    {
        if (rest->timeUsed > timeLimit)
            rest->status = TLE;
        else if (rest->memUsed > memLimit)
            rest->status = MLE;
        else
            rest->status = EXIT_0;
    }
}

/**
 * 创建子进程,执行命令
 */
void run(char *args[], int timeLimit, int memLimit, int maxMemLimit, const char *in, const char *out, struct result *res)
{
    pid_t pid = fork();

    if (pid < 0)
    {
        std::cerr << "fork error!\n";
    }
    else if (pid == 0)
    {
        run_cmd(args, timeLimit, maxMemLimit, in, out);
    }
    else
    {
        watch_result(pid, timeLimit, memLimit, res);
    }
}

void split(char **arr, char *str, const char *del)
{
    char *s = NULL;
    s = strtok(str, del);
    while (s != NULL)
    {
        *arr++ = s;
        s = strtok(NULL, del);
    }
    *arr++ = NULL;
}

/**
 * 结果写入文件
 */
void write_result(std::string res)
{
    std::ofstream out_file;
    out_file.open("./result.out", std::ios_base::out | std::ios_base::trunc);
    out_file << res;
    out_file.close();
}

int main(int argc, char *argv[])
{
    char *cmd[32];
    split(cmd, argv[1], "@");

    struct result res;
    run(cmd, atoi(argv[2]), atoi(argv[3]), atoi(argv[4]), argv[5], "./output.out", &res);

    write_result(res.toJson());

    return 0;
}