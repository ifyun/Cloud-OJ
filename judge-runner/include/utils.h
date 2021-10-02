#ifndef _UTILS_H
#define _UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class Utils {
private:
    static std::string calc_results(const std::vector<Result> &results);

    static __off_t get_rtrim_offset(const std::string &);

public:
    static std::vector<std::string> get_files(const std::string &, const std::string &);

    static bool diff(const std::string &, const std::string &);

    static std::string write_result(const std::vector<Result> &results);
};

#endif //_UTILS_H