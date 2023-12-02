#include <unistd.h>
#include <iostream>
#include <sstream>
#include "runner.h"
#include "common.h"

const char *RESULT_STR[] = {"", "AC", "TLE", "MLE", "PA",
                            "WA", "CE", "RE", "IE", "OLE"};

int main(int argc, char *argv[]) {
    int lang;
    char cmd[128];
    char work_dir[128];
    char data_dir[128];
    Config config;
    std::stringstream ss;

    if (getuid() != 0) {
        ss << "{\n"
           << R"( "result": )" << IE << ",\n"
           << R"( "error": "RUN AS ROOT")" << "\n"
           << "}\n";
        std::cout << ss.str();
        return 0;
    }

    if (get_args(argc, argv, cmd, lang, work_dir, data_dir, config) != 0) {
        ss << "{\n"
           << R"( "result": )" << IE << ",\n"
           << R"( "error": "INVALID ARGS")" << "\n"
           << "}\n";
        std::cout << ss.str();
        return 0;
    }

    Runner runner(cmd, work_dir, data_dir, lang, config);
    RTN rtn = runner.judge();

    if (rtn.result == RE || rtn.result == IE) {
        ss << "{\n"
           << R"( "result": )" << rtn.result << ",\n"
           << R"( "error": ")" << rtn.err << "\"\n"
           << "}\n";
    } else {
        ss << "{\n"
           << R"( "result": )" << rtn.result << ",\n"
           << R"( "desc": ")" << RESULT_STR[rtn.result] << R"(",)" << "\n"
           << R"( "total": )" << rtn.total << ",\n"
           << R"( "passed": )" << rtn.passed << ",\n"
           << R"( "passRate": )" << rtn.passRate << ",\n"
           << R"( "time": )" << rtn.time << ",\n"
           << R"( "memory": )" << rtn.memory << "\n"
           << "}\n";
    }

    std::cout << ss.str();
    return 0;
}
