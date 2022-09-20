#ifndef _OPTIONS_H
#define _OPTIONS_H 1

#include <getopt.h>
#include "runner.h"

int get_args(int argc, char *argv[], char *cmd, int &lang, char workdir[], char datadir[], Config &config);

#endif