#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <dirent.h>
#include <algorithm>
#include <cstring>
#include "utils.h"

const char *status_str[] = {"AC", "WA", "TLE", "MLE", "OLE", "PA"};

/**
 * @brief 从指定目录获取测试数据文件的路径
 * @param path 测试数据目录
 * @param ext 扩展名(.in | .out)
 */
std::vector<std::string> Utils::get_files(const std::string &path, const std::string &ext) {
    std::vector<std::string> files;
    DIR *dir = opendir(path.c_str());

    if (dir == nullptr) {
        throw std::invalid_argument("[ERROR] Path of test data does not exists.\n");
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
inline void Utils::rtrim(std::string &str) {
    if (str.empty()) { return; }
    str.erase(str.find_last_not_of('\r') + 1);
    str.erase(str.find_last_not_of('\n') + 1);
    str.erase(str.find_last_not_of(' ') + 1);
}

/**
 * @brief 计算结果、通过率
 * @return 判题结果的 JSON 字符串
 */
std::string Utils::calc_results(const std::vector<Result> &results) {
    int status;
    long time = 0, memory = 0;
    double pass_rate = 0;
    int status_cnt[] = {0, 0, 0, 0, 0};
    auto total = results.size();

    for (auto r : results) {
        status_cnt[r.status]++;
        if (r.timeUsed > time) time = r.timeUsed;
        if (r.memUsed > memory) memory = r.memUsed;
    }

    if (status_cnt[AC] == 0) {
        status = WA;
    } else if (status_cnt[AC] < total) {
        status = PA;
        pass_rate = (double) status_cnt[AC] / (double) total;
    } else {
        pass_rate = 1;
        status = AC;
    }

    if (status_cnt[OLE] > 0) {
        status = OLE;
    } else if (status_cnt[MLE] > 0) {
        status = MLE;
    } else if (status_cnt[TLE] > 0) {
        status = TLE;
    }

    std::stringstream ss;

    ss << "{\n"
       << "\t" << R"("status": )" << status << ",\n"
       << "\t" << R"("result": ")" << status_str[status] << R"(",)" << "\n"
       << "\t" << R"("total": )" << total << ",\n"
       << "\t" << R"("passed": )" << status_cnt[AC] << ",\n"
       << "\t" << R"("passRate": )" << pass_rate << ",\n"
       << "\t" << R"("time": )" << time << ",\n"
       << "\t" << R"("memory": )" << memory << "\n"
       << "}\n";

    return ss.str();
}

/**
 * @brief 比较文件
 */
bool Utils::diff(const std::string &path1, const std::string &path2) {
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
std::string Utils::write_result(const std::vector<Result> &results) {
    std::string res = calc_results(results);
    std::ofstream of;

    of.open("result.json", std::ios_base::out | std::ios_base::trunc);

    of << res;
    of.close();

    return res;
}