#ifndef UTILS_H
#define UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class Utils {
private:
    /**
    * @brief 返回去除文件末尾所有空格/换行符后的偏移量
    * @param path 文件路径
    * @return 偏移量
    */
    static __off_t get_rtrim_offset(int fd);

public:
    /**
    * @brief 从指定目录获取测试数据文件的路径
    * @param path 测试数据目录
    * @param ext 扩展名(.in | .out)
    */
    static std::vector<std::string> get_files(const std::string &, const std::string &);

    /**
    * @brief 比较文件
    * @return @c true - 相同, @c false - 不同
    */
    static bool compare(Config config, spj_func spj);

    /**
    * @brief 计算结果
    */
    static void calc_results(RTN &rtn, const std::vector<Result> &results, std::vector<std::string> &test_points);
};

#endif // UTILS_H
