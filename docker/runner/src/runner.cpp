#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <ios>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include "utils.h"

const unsigned AC = 0;
const unsigned WA = 1;
const unsigned TLE = 2;
const unsigned MLE = 3;
const unsigned RE = 4;

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
 * Set resource limit.
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
 * Execute user code.
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
            std::cerr << "[ERROR] Failed to start the process!\n";
            exit(-1);
        }

        close(newstdin);
        close(newstdout);
    }
    else
    {
        if (newstdin == -1)
        {
            std::cerr << "[ERROR] Failed to open " << in << std::endl;
            exit(-1);
        }
        if (newstdout == -1)
        {
            std::cerr << "[ERROR] Failed to open " << out << std::endl;
            exit(-1);
        }
    }
}

/**
 * Check result.
 */
void watch_result(pid_t pid, int timeLimit, int memLimit, std::string out, std::string expect, struct result *rest)
{
    int status;
    struct rusage ru;

    if (wait4(pid, &status, 0, &ru) == -1)
    {
        rest->status = RE;
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
            rest->status = diff(out, expect) ? WA : AC;
    }
}

/**
 * Create process, execute command.
 */
void run(char *args[], int timeLimit, int memLimit, int maxMemLimit, const char *in, std::string out, std::string expect, struct result *res)
{
    pid_t pid = fork();

    if (pid < 0)
    {
        res->status = RE;
    }
    else if (pid == 0)
    {
        run_cmd(args, timeLimit, maxMemLimit, in, out);
    }
    else
    {
        watch_result(pid, timeLimit, memLimit, out, expect, res);
    }
}

/**
 * Split command to execute.
 */
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
 * argv:
 *   argv[1]: Command to execute, use '@' replace space.
 *   argv[2]: Time limit(ms).
 *   argv[3]: Memory limit(KB).
 *   argv[4]: Max memory limit(KB), set for jvm.
 *   argv[5]: Directory of input and output files.
 */
int main(int argc, char *argv[])
{
    char *cmd[32];
    split(cmd, argv[1], "@");

    int time_limit = atoi(argv[2]);
    int mem_limit = atoi(argv[3]);
    int max_mem_limit = atoi(argv[4]);

    std::vector<std::string> results;

    // get input files
    std::vector<std::string> input_files = get_files(argv[5], "in");
    std::vector<std::string> output_files = get_files(argv[5], "out");

    if (input_files.size() > 0)
    {
        // if input_files.size() != output_files.size(), just exit.
        if (input_files.size() != output_files.size())
        {
            std::cerr << "[ERROR] The number of input and output files is not equal.";
            exit(-1);
        }
        // run user code with input files
        for (auto i = 0; i < input_files.size(); i++)
        {
            struct result res;
            std::string out = std::to_string(i + 1) + std::string(".out");
            run(cmd, time_limit, mem_limit, max_mem_limit, input_files[i].c_str(), out, output_files[i], &res);
            results.push_back(res.toJson());
        }
    }
    else
    {
        struct result res;
        std::string expect = get_files(argv[5], ".out")[0]; // no input files, only one out
        run(cmd, time_limit, mem_limit, max_mem_limit, "/dev/null", "./1.out", expect, &res);
        results.push_back(res.toJson());
    }

    write_result(results);

    return 0;
}