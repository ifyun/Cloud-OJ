#include <cstring>
#include <getopt.h>
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

static struct option long_options[] = {
        {"cmd",     1, nullptr, 'c'},
        {"time",    1, nullptr, 't'},
        {"ram",     1, nullptr, 'm'},
        {"output",  1, nullptr, 'o'},
        {"workdir", 1, nullptr, 'w'},
        {"data",    1, nullptr, 'd'},
        {"cpu",     1, nullptr, 'u'},
        {nullptr,   0, nullptr, 0}
};

static const char *short_options = "c:r:t:m:o:w:d:u:";

int get_args(int argc, char *argv[], char *cmd, char workdir[], char datadir[], Config &config) {
    int opt;
    int index = 0;
    int count = 0;

    while ((opt = getopt_long_only(argc, argv, short_options, long_options, &index)) != EOF) {
        switch (opt) {
            case 'c':
                strcpy(cmd, optarg);
                count++;
                break;
            case 'w':
                strcpy(workdir, optarg);
                count++;
                break;
            case 'd':
                strcpy(datadir, optarg);
                count++;
                break;
            case 'u':
                config.cpu = (int) strtol(optarg, nullptr, 10);
                count++;
                break;
            case 't':
                config.timeout = (int) strtol(optarg, nullptr, 10) * 1000;
                count++;
                break;
            case 'm':
                config.memory = (int) strtol(optarg, nullptr, 10) << 10;
                count++;
                break;
            case 'o':
                config.output_size = (int) strtol(optarg, nullptr, 10) << 10;
                count++;
                break;
            case '?':
            default:
                return -1;
        }
    }

    if (count < 7) {
        return -1;
    }

    return 0;
}
