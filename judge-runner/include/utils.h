#ifndef _UTILS_H
#define _UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class utils {
private:
    static void rtrim(std::string &str);

    static std::string calc_results(const std::vector<Result> &results);

public:
    static std::vector<std::string> get_files(const std::string &, const std::string &);

    static bool diff(const std::string &, const std::string &);

    static std::string write_result(const std::vector<Result> &results);
};

#endif //_UTILS_H