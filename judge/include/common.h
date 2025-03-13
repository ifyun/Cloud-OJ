#ifndef COMMON_H
#define COMMON_H 1

#include "runner.h"

/// 解析参数
int get_args(int argc, char *argv[], char *cmd, char workdir[], char datadir[], Config &config);

/**
* 分割字符串到数组
* @param arr 保存结果的字符串数组
* @param str 被分割的字符串
* @param separator 分隔符
*/
void split(char **arr, char *str, const char *separator);

#endif // COMMON_H
