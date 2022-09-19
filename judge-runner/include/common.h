#ifndef _COMMON_H
#define _COMMON_H 1

#define RUNTIME_ERROR 1
#define JUDGE_ERROR 2

/**
* 分割字符串到数组
* @param arr 保存结果的字符串数组
* @param str 被分割的字符串
* @param separator 分隔符
*/
void split(char **arr, char *str, const char *separator);

#endif