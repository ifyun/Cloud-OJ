#include <cstring>
#include "common.h"

void split(char **arr, char *str, const char *separator) {
    char *s;
    s = strtok(str, separator);

    while (s != nullptr) {
        *arr++ = s;
        s = strtok(nullptr, separator);
    }

    *arr = nullptr;
}