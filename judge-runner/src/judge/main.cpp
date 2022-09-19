/// 判题程序入口
/// @author Cloud
#include <iostream>
#include <csignal>
#include "runner.h"
#include "options.h"

int main(int argc, char *argv[]) {
    int lang;
    char cmd[64];
    char work_dir[64];
    char data_dir[64];
    Config config;

    int i = get_args(argc, argv, cmd, lang, work_dir, data_dir, config);

    if (i != 0) {
        return i;
    }

    if (getuid() != 0) {
        std::cerr << R"({"code": -1, "error": "permission denied"})" << std::endl;
        return -1;
    }

    Runner runner(cmd, work_dir, data_dir, lang, config);
    RTN rtn = runner.judge();
    std::cout << rtn.result << std::endl;

    return rtn.code;
}
