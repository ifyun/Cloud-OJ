#ifndef _ENV_SETUP_H
#define _ENV_SETUP_H 1

#include <string>

int exec_cmd(const char *fmt, ...);

const char *setup_env(const char *work_dir, const char *data_dir);

void end_env(const char *work_dir, const char *data_dir);

#endif //_ENV_SETUP_H
