#include <ctime>
#include <cstdio>
#include <cstdarg>
#include "thread_pool.h"
#include "log.h"

char TIME[20];

static inline void time_now() {
    time_t now = time(nullptr);
    strftime(TIME, 20, "%Y-%m-%d %H:%M:%S", localtime(&now));
}

static inline void print_log(int type, const char *str) {
    time_now();

    if (type == 0) {
        fprintf(stdout, "%s  INFO [%10s] %s.\n", TIME, thread_name().c_str(), str);
        fflush(stdout);
    } else {
        fprintf(stderr, "%s ERROR [%10s] %s.\n", TIME, thread_name().c_str(), str);
    }
}

void INFO(const char *fmt, ...) {
    char str[128];
    va_list ap;
    va_start(ap, fmt);
    vsprintf(str, fmt, ap);
    print_log(0, str);
    va_end(ap);
}

void ERROR(const char *fmt, ...) {
    char str[128];
    va_list ap;
    va_start(ap, fmt);
    vsprintf(str, fmt, ap);
    print_log(1, str);
    va_end(ap);
}
