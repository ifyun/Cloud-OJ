#ifndef _ENV_SETUP_H
#define _ENV_SETUP_H 1

#include <string>

int exec_cmd(const char *fmt, ...);

void setup_env(const char *work_dir);

void end_env(const char *work_dir);

#endif //_ENV_SETUP_H
