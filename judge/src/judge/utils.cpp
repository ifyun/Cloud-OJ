#include <string>
#include <vector>
#include <iostream>
#include <sstream>
#include <fstream>
#include <algorithm>
#include <cstring>
#include <fcntl.h>
#include <dirent.h>
#include <unistd.h>
#include <sys/mman.h>
#include "utils.h"

const char *status_str[] = {"AC", "WA", "TLE", "MLE", "OLE", "PA"};

std::vector<std::string> Utils::get_files(const std::string &path, const std::string &ext) {
    std::vector<std::string> files;
    DIR *dir = opendir(path.c_str());

    if (dir == nullptr) {
        throw std::invalid_argument("测试数据目录不存在");
    }

    struct dirent *d_ent;

    const char *self = ".";
    const char *parent = "..";

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
    closedir(dir);

    return files;
}

std::string Utils::calc_results(const std::vector<Result> &results) {
    int status;
    long time = 0, memory = 0;
    double pass_rate = 0;
    int status_cnt[] = {0, 0, 0, 0, 0};
    auto total = results.size();

    for (auto r: results) {
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
       << "  " << R"("code": )" << 0 << ",\n"
       << "  " << R"("status": )" << status << ",\n"
       << "  " << R"("result": ")" << status_str[status] << R"(",)" << "\n"
       << "  " << R"("total": )" << total << ",\n"
       << "  " << R"("passed": )" << status_cnt[AC] << ",\n"
       << "  " << R"("passRate": )" << pass_rate << ",\n"
       << "  " << R"("time": )" << time << ",\n"
       << "  " << R"("memory": )" << memory << "\n"
       << "}\n";

    return ss.str();
}

__off_t Utils::get_rtrim_offset(int fd) {
    __off_t offset;
    char buf;

    offset = lseek(fd, -2, SEEK_END);

    read(fd, &buf, sizeof(buf));

    while (true) {
        if (buf != 10 && buf != 32) {
            offset += 2;
            break;
        }

        offset = lseek(fd, -2, SEEK_CUR);
        read(fd, &buf, sizeof(buf));
    }

    return offset;
}

bool Utils::diff(const std::string &user_output, const std::string &expect_output) {
    int fd1 = open(user_output.c_str(), O_RDONLY, 0644);
    int fd2 = open(expect_output.c_str(), O_RDONLY, 0644);

    auto len1 = get_rtrim_offset(fd1);
    auto len2 = get_rtrim_offset(fd2);

    char *addr1 = (char *) mmap(nullptr, len1, PROT_READ, MAP_PRIVATE, fd1, 0);
    char *addr2 = (char *) mmap(nullptr, len2, PROT_READ, MAP_PRIVATE, fd2, 0);

    bool r = false;

    if (len1 != len2) {
        r = true;
    } else {
        for (auto i = 0; i < len1; i++) {
            if (addr1[i] != addr2[i]) {
                r = true;
                break;
            }
        }
    }

    close(fd1);
    close(fd2);

    munmap(addr1, len1);
    munmap(addr2, len2);

    return r;
}

void Utils::write_result(RTN &rtn, const std::vector<Result> &results, const std::string &work_dir) {
    std::ofstream of;
    std::string res;

    if (rtn.code == 0) {
        res = calc_results(results);
    } else {
        std::ostringstream ss;
        ss << "{\n"
           << "  " << R"("code": )" << rtn.code << ",\n"
           << "  " << R"("error": ")" << rtn.err << "\"\n"
           << "}\n";
        res = ss.str();
    }

    of.open(work_dir + "/result.json", std::ios_base::out | std::ios_base::trunc);
    of << res;
    of.close();

    rtn.result = res;
}
