#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include "utils.h"

const int JUDGE_ERROR = -1;

void split(char **arr, char *str, const char *del) {
    char *s;
    s = strtok(str, del);

    while (s != nullptr) {
        *arr++ = s;
        s = strtok(nullptr, del);
    }

    *arr = nullptr;
}

/**
 * @note argv[1]: 命令 + 参数，空格用 '@' 表示
 * @note argv[2]: 时间限制(ms)
 * @note argv[3]: 内存限制(MB)
 * @note argv[4]: 实际内存限制(MB)
 * @note argv[5]: 输出限制(MB)
 * @note argv[6]: 测试数据目录
 */
int main(int argc, char *argv[]) {
    if (argc < 7) {
        std::cerr << "Args required\n";
        exit(JUDGE_ERROR);
    }

    char *cmd[32];
    split(cmd, argv[1], "@");

    Config config = {
            .timeout=strtol(argv[2], nullptr, 10),
            .memory=strtol(argv[3], nullptr, 10) << 10,
            .max_memory=strtol(argv[4], nullptr, 10) << 10,
            .output_size=strtol(argv[5], nullptr, 10) << 10
    };

    std::string data_dir = argv[6];
    std::vector<std::string> input_files;
    std::vector<std::string> output_files;

    try {
        input_files = utils::get_files(data_dir, "in");
        output_files = utils::get_files(data_dir, "out");
    } catch (const std::invalid_argument &error) {
        std::cerr << error.what();
        exit(JUDGE_ERROR);
    }

    std::vector<Result> results;

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            std::cerr << "[ERROR] The number of input and output files is not equal\n";
            exit(JUDGE_ERROR);
        }
        for (auto i = 0; i < input_files.size(); i++) {
            std::string real_output_file = std::to_string(i + 1) + ".out";

            config.in = input_files[i];
            config.out = real_output_file;
            config.expect = output_files[i];

            Result res = Runner::run(cmd, config);

            results.push_back(res);
        }

        utils::write_result(results);
    } else if (!output_files.empty()) {
        // 没有输出文件，读取第一个 .out 文件
        std::string expect = utils::get_files(data_dir, ".out")[0];

        config.in = "/dev/null";
        config.out = "./1.out";
        config.expect = expect;

        Result res = Runner::run(cmd, config);

        results.push_back(res);

        utils::write_result(results);
    } else {
        std::cerr << "[ERROR] Test data required\n";
        exit(JUDGE_ERROR);
    }

    return 0;
}
