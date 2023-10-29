#include <iostream>
#include <unistd.h>
#include "runner.h"
#include "common.h"

int main(int argc, char *argv[]) {
    int lang;
    char cmd[128];
    char work_dir[128];
    char data_dir[128];
    Config config;

    if (get_args(argc, argv, cmd, lang, work_dir, data_dir, config) != 0) {
        char res[64];
        sprintf(res, "{ \"code\": %d, \"error\": \"wrong arg(s)\" }\n", INTERNAL_ERROR);
        std::cout << res;
        return INTERNAL_ERROR;
    }

    if (getuid() != 0) {
        std::cerr << "permission denied\n";
        return INTERNAL_ERROR;
    }

    Runner runner(cmd, work_dir, data_dir, lang, config);
    RTN rtn = runner.judge();
    std::cout << rtn.result << std::endl;

    return 0;
}
