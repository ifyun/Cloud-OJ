#include <unistd.h>
#include <iostream>
#include <sstream>
#include <cstring>
#include "runner.h"
#include "common.h"

const char *RESULT_STR[] = {"", "AC", "TLE", "MLE", "PA",
                            "WA", "CE", "RE", "IE", "OLE"};

int main(int argc, char *argv[]) {
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

    if (get_args(argc, argv, cmd, work_dir, data_dir, config) != 0) {
        ss << "{\n"
           << R"( "result": )" << IE << ",\n"
           << R"( "error": "INVALID ARGS")" << "\n"
           << "}\n";
        std::cout << ss.str();
        return 0;
    }

    Runner runner(cmd, work_dir, data_dir, config);
    RTN rtn = runner.judge();

    if (rtn.result == IE) {
        ss << "{\n"
           << R"( "result": )" << rtn.result << ",\n"
           << R"( "error": ")" << rtn.err << "\"\n"
           << "}\n";
    } else {
        ss << "{\n"
           << R"( "result": )" << rtn.result << ",\n"
           << R"( "desc": ")" << RESULT_STR[rtn.result] << "\",\n";

        if (strlen(rtn.err) > 0) {
            ss << R"( "error": ")" << rtn.err << "\",\n";
        }

        ss << R"( "total": )" << rtn.total << ",\n"
           << R"( "passed": )" << rtn.passed << ",\n"
           << R"( "passRate": )" << rtn.passRate << ",\n"
           << R"( "time": )" << rtn.time << ",\n"
           << R"( "memory": )" << rtn.memory << ",\n";

        if (!rtn.detail.empty()) {
            ss << R"( "detail": )" << "[ ";
            auto iter = rtn.detail.begin();

            while (iter != rtn.detail.end()) {
                auto index = iter->find_last_of('/');
                auto name = iter->substr(index + 1, iter->length() - index - 1);
                ss << "\"" << name << "\"";
                iter++;

                if (iter != rtn.detail.end()) {
                    ss << ", ";
                }
            }

            ss << " ]\n";
        } else {
            ss << R"( "detail": [])" << "\n";
        }

        ss << "}\n";
    }

    std::cout << ss.str();
    return 0;
}
