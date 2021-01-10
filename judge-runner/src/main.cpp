#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include "utils.h"
#include "runner.h"

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
 * @note argv[5]: 输出限制(KB)
 * @note argv[6]: 测试数据目录路径
 */
int main(int argc, char *argv[]) {
    if (argc < 7) {
        std::cerr << "Args required.\n";
        exit(EXIT_FAILURE);
    }

    char *cmd[32];
    split(cmd, argv[1], "@");

    resource_limit limit = {
            .timeout=strtol(argv[2], nullptr, 10),
            .memory=strtol(argv[3], nullptr, 10) * 1024,
            .max_memory=strtol(argv[4], nullptr, 10) * 1024,
            .output_size=strtol(argv[5], nullptr, 10)
    };

    std::string data_dir = argv[6];
    std::vector<std::string> input_files;
    std::vector<std::string> output_files;

    try {
        input_files = utils::get_files(data_dir, "in");
        output_files = utils::get_files(data_dir, "out");
    } catch (const std::invalid_argument &error) {
        std::cerr << error.what();
        exit(EXIT_FAILURE);
    }

    std::vector<std::string> results;

    if (!input_files.empty() && !output_files.empty()) {
        if (input_files.size() != output_files.size()) {
            std::cerr << "[ERROR] The number of input and output files is not equal.";
            return 1;
        }
        // Run with each input.
        for (auto i = 0; i < input_files.size(); i++) {
            std::string real_output_file_path = std::to_string(i + 1) + ".out";
            result res = runner::run(cmd, limit, input_files[i], real_output_file_path, output_files[i]);

            results.push_back(res.to_json());
        }

        utils::write_result(results);
    } else if (!output_files.empty()) {
        // No input files, read first ".out" file.
        std::string expect = utils::get_files(data_dir, ".out")[0];
        result res = runner::run(cmd, limit, "/dev/null", "./1.out", expect);
        results.push_back(res.to_json());

        utils::write_result(results);
    } else {
        std::cerr << "[ERROR] No test data.";
        return 1;
    }

    return 0;
}
