#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <dirent.h>
#include <algorithm>
#include <cstring>
#include "utils.h"

/**
 * @brief 从指定目录获取测试数据文件的路径
 * @param path 测试数据目录
 * @param ext 扩展名(.in | .out)
 */
std::vector<std::string> utils::get_files(const std::string &path, const std::string &ext) {
    std::vector<std::string> files;
    DIR *dir = opendir(path.c_str());

    if (dir == nullptr) {
        throw std::invalid_argument("[ERROR] Path of test data does not exists\n");
    }

    struct dirent *d_ent;

    char self[3] = ".";
    char parent[6] = "..";

    while ((d_ent = readdir(dir)) != nullptr) {
        // Exclude "." and ".."
        if (strcmp(d_ent->d_name, self) != 0 && strcmp(d_ent->d_name, parent) != 0) {
            if (d_ent->d_type != DT_DIR) {
                std::string file_name(d_ent->d_name);

                if (strcmp(file_name.c_str() + file_name.length() - ext.length(), ext.c_str()) == 0) {
                    std::string abs_path;
                    if (path[path.length() - 1] == '/')
                        abs_path = path + file_name;
                    else
                        abs_path.append(path).append("/").append(file_name);
                    files.push_back(abs_path);
                }
            }
        }
    }

    std::sort(files.begin(), files.end());

    return files;
}

/**
 * @brief 移除字符串尾部的换行符和空格
 */
void utils::rtrim(std::string &str) {
    if (str.empty())
        return;
    str.erase(str.find_last_not_of('\r') + 1);
    str.erase(str.find_last_not_of('\n') + 1);
    str.erase(str.find_last_not_of(' ') + 1);
}

/**
 * @brief 比较文件
 */
bool utils::diff(const std::string &path1, const std::string &path2) {
    std::ifstream file1(path1);
    std::ifstream file2(path2);

    std::ostringstream oss1, oss2;
    oss1 << file1.rdbuf();
    oss2 << file2.rdbuf();

    std::string s1 = oss1.str(), s2 = oss2.str();

    rtrim(s1);
    rtrim(s2);

    if (s1.length() != s2.length()) {
        return true;
    } else {
        return s1 != s2;
    }
}

/**
 * @brief 将结果写入文件(result.json)
 */
void utils::write_result(std::vector<Result> results) {
    std::string res;

    if (results.size() == 1) {
        res = "[" + results[0].to_json() + "]";
    } else {
        res.append("[");

        for (auto i = 0; i < results.size(); i++) {
            res.append(results[i].to_json());
            if (i < results.size() - 1) {
                res.append(",");
            }
        }

        res.append("]");
    }

    std::ofstream of;
    of.open("./result.json", std::ios_base::out | std::ios_base::trunc);
    of << res;
    of.close();
}