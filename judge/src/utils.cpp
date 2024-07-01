#include <string>
#include <vector>
#include <iostream>
#include <filesystem>
#include <algorithm>
#include <fcntl.h>
#include <unistd.h>
#include <sys/mman.h>
#include "utils.h"

namespace fs = std::filesystem;

std::vector<std::string> Utils::get_files(const std::string &path, const std::string &ext) {
    std::vector<std::string> files;

    if (!fs::is_directory(path)) {
        throw std::invalid_argument("测试数据目录不存在");
    }

    for (const auto &entry: fs::directory_iterator(path)) {
        if (entry.path().extension() == ext) {
            files.push_back(entry.path());
        }
    }

    std::sort(files.begin(), files.end());

    return files;
}

void Utils::calc_results(RTN &rtn, const std::vector<Result> &results, std::vector<std::string> &test_points) {
    /// results.size() <= test_points.size()
    if (rtn.result == IE) {
        return;
    }

    int status;
    int total = (int) test_points.size();
    long time = 0, memory = 0;
    double pass_rate = 0;
    // * 1 -> 9: AC ... OLE
    int results_cnt[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    for (auto i = 0; i < results.size(); i++) {
        results_cnt[results[i].status]++;
        if (results[i].status == AC) { rtn.detail.push_back(test_points[i]); }
        if (results[i].time > time) { time = results[i].time; }
        if (results[i].mem > memory) { memory = results[i].mem; }
    }

    if (results_cnt[AC] == 0) {
        status = WA;
    } else if (results_cnt[AC] < total) {
        status = PA;
        pass_rate = (double) results_cnt[AC] / (double) total;
    } else {
        pass_rate = 1;
        status = AC;
    }

    if (results_cnt[RE] > 0) {
        status = RE;
    } else if (results_cnt[OLE] > 0) {
        status = OLE;
    } else if (results_cnt[MLE] > 0) {
        status = MLE;
    } else if (results_cnt[TLE] > 0) {
        status = TLE;
    }

    rtn.result = status;
    rtn.total = total;
    rtn.passed = results_cnt[AC];
    rtn.passRate = pass_rate;
    rtn.time = time;
    rtn.memory = memory;
}

__off_t Utils::get_rtrim_offset(int fd) {
    __off_t offset;
    char buf;

    offset = lseek(fd, -1, SEEK_END);
    read(fd, &buf, sizeof(buf));

    while (true) {
        // 从文件尾部向前读，不是空格或 LF 就退出
        if (buf != 10 && buf != 32) {
            offset += 1;
            break;
        }

        offset = lseek(fd, -2, SEEK_CUR);
        read(fd, &buf, sizeof(buf));
    }

    return offset;
}

bool Utils::compare(Config config, spj_func spj) {
    bool same = true;

    if (spj != nullptr) {
        // Special Judge
        return spj(config.in, config.out, config.ans);
    }

    auto len1 = get_rtrim_offset(config.out_fd);
    auto len2 = get_rtrim_offset(config.ans_fd);

    char *addr1 = (char *) mmap(nullptr, len1, PROT_READ, MAP_SHARED, config.out_fd, 0);
    char *addr2 = (char *) mmap(nullptr, len2, PROT_READ, MAP_SHARED, config.ans_fd, 0);

    if (len1 != len2) {
        same = false;
    } else {
        for (auto i = 0; i < len1; i++) {
            if (addr1[i] != addr2[i]) {
                same = false;
                break;
            }
        }
    }

    munmap(addr1, len1);
    munmap(addr2, len2);

    return same;
}
