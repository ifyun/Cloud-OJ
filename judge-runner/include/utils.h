/// 文件工具类
/// @author Cloud
#ifndef _UTILS_H
#define _UTILS_H 1

#include <vector>
#include <string>
#include "runner.h"

class Utils {
private:
    /**
    * @brief 计算结果、通过率
    * @return 判题结果的 JSON 字符串
    */
    static std::string calc_results(const std::vector<Result> &results);

    /**
    * @brief 返回去除文件末尾所有空格/换行符后的偏移量
    * @param path 文件路径
    * @return 偏移量
    */
    static __off_t get_rtrim_offset(const std::string &);

public:
    /**
    * @brief 从指定目录获取测试数据文件的路径
    * @param path 测试数据目录
    * @param ext 扩展名(.in | .out)
    */
    static std::vector<std::string> get_files(const std::string &, const std::string &);

    /**
    * @brief 比较文件
    * @return @c true - 不同, @c false - 相同
    */
    static bool diff(const std::string &, const std::string &);

    static void write_result(RTN &rtn, const std::vector<Result> &results, const std::string& work_dir);
};

#endif //_UTILS_H