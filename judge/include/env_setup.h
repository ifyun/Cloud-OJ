#ifndef ENV_SETUP_H
#define ENV_SETUP_H 1

#include <string>

int exec_cmd(const char *fmt, ...);

void setup_env(const char *work_dir);

void end_env(const char *work_dir);

#endif // ENV_SETUP_H
