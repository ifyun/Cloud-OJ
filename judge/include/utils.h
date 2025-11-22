#ifndef UTILS_H
#define UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class Utils
{
    /**
    * 返回去除文件末尾所有空格/换行符后的偏移量
    * @return 偏移量
    */
    static __off_t get_rtrim_offset(int fd);

public:
    /**
    * 从指定目录获取测试数据文件的路径
    */
    static std::vector<std::string> get_files(const std::string&, const std::string&);

    /**
    * 比较文件
    * @return @c true - 相同, @c false - 不同
    */
    static bool compare(const Config& config, spj_func spj);

    /**
    * 计算结果
    */
    static void calc_results(RTN& rtn, const std::vector<Result>& results, const std::vector<std::string>& test_points);
};

#endif // UTILS_H
