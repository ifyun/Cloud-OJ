#include <iostream>
#include <sstream>
#include <cstdlib>
#include "runner.h"

/**
 * @param argv[1]: 命令 + 参数，空格用 '@' 表示
 * @param argv[2]: 时间限制(ms)
 * @param argv[3]: 内存限制(MB)
 * @param argv[4]: 实际内存限制(MB)
 * @param argv[5]: 输出限制(MB)
 * @param argv[7]: 工作目录
 * @param argv[6]: 测试数据目录
 */
int main(int argc, char *argv[]) {
    if (argc < 8) {
        std::cerr << "Missing parameters.\n";
        exit(JUDGE_ERROR);
    }

    RTN rtn = exec(argv);

    if (rtn.code == 0) {
        std::cout << rtn.result;
    }

    return rtn.code;
}
