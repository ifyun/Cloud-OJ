#ifndef _UTILS_H
#define _UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class utils {
private:
    static inline void rtrim(std::string &str);
public:
    static std::vector<std::string> get_files(const std::string &, const std::string &);

    static bool diff(const std::string &, const std::string &);

    static void write_result(std::vector<Result>);
};

#endif //_UTILS_H