#ifndef OPTIONS_H
#define OPTIONS_H 1

#include "runner.h"

/// 解析参数
int get_args(int argc, char *argv[], char *cmd, int &lang, char workdir[], char datadir[], Config &config);

#endif // OPTIONS_H
