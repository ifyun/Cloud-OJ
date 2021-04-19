#include "env_setup.h"
#include <cstdarg>
#include <random>

const char *random_str(const int len) {
    char *str = new char[len];
    std::random_device rd;
    std::uniform_int_distribution<int> dist(65, 90);

    for (auto i = 0; i < len; i++) {
        str[i] = (char) dist(rd);
    }

    return str;
}

inline int exec_cmd(const char *fmt, ...) {
    char cmd[256];
    va_list ap;

    va_start(ap, fmt);
    vsprintf(cmd, fmt, ap);

    int rtn = system(cmd);
    va_end(ap);
    return rtn;
}

/**
 * @brief 设置运行环境
 */
const char *setup_env(const char *work_dir, const char *data_dir) {
    const char *random_dir = random_str(10);
    exec_cmd("mkdir %s/%s", work_dir, random_dir);
    exec_cmd("test -d %s/proc || mkdir %s/proc", work_dir, work_dir);
    exec_cmd("test -d %s/etc/alternatives || mkdir -p %s/etc/alternatives", work_dir, work_dir);
    exec_cmd("test -d %s/dev || mkdir %s/dev", work_dir, work_dir);
    exec_cmd("test -d %s/bin || mkdir %s/bin", work_dir, work_dir);
    exec_cmd("test -d %s/usr || mkdir %s/usr", work_dir, work_dir);
    exec_cmd("test -d %s/lib || mkdir %s/lib", work_dir, work_dir);
    exec_cmd("test -d %s/lib64 || mkdir %s/lib64", work_dir, work_dir);
    exec_cmd("test -d %s/tmp || mkdir %s/tmp", work_dir, work_dir);
    exec_cmd("test -e %s/dev/null || mknod -m 666 %s/dev/null c 1 3", work_dir, work_dir);
    exec_cmd("chown root:root %s/dev/null", work_dir);
    // mount env
    exec_cmd("mount --bind -o ro %s %s/%s", data_dir, work_dir, random_dir);
    exec_cmd("mountpoint %s/proc > /dev/null || mount --bind -o ro /proc %s/proc", work_dir, work_dir);
    exec_cmd("mountpoint %s/etc/alternatives > /dev/null || mount --bind -o ro /etc/alternatives %s/etc/alternatives",
             work_dir, work_dir);
    exec_cmd("mountpoint %s/bin > /dev/null || mount --bind -o ro /bin %s/bin", work_dir, work_dir);
    exec_cmd("mountpoint %s/usr > /dev/null || mount --bind -o ro /usr %s/usr", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib > /dev/null || mount --bind -o ro /lib %s/lib", work_dir, work_dir);
#ifdef __x86_64__
    exec_cmd("mountpoint %s/lib64 > /dev/null || mount --bind -o ro /lib64 %s/lib64", work_dir, work_dir);
#endif

    return random_dir;
}

/**
 * @brief 清理环境，解除挂载
 */
void end_env(const char *work_dir, const char *random_dir) {
    exec_cmd("mountpoint %s/%s > /dev/null 2>&1 && umount -l %s/%s && rmdir %s/%s",
             work_dir, random_dir, work_dir, random_dir, work_dir, random_dir);
    exec_cmd("mountpoint %s/proc > /dev/null 2>&1 && umount %s/proc", work_dir, work_dir);
    exec_cmd("mountpoint %s/etc/alternatives > /dev/null 2>&1 && umount %s/etc/alternatives",
             work_dir, work_dir);
    exec_cmd("mountpoint %s/bin > /dev/null 2>&1 && umount %s/bin", work_dir, work_dir);
    exec_cmd("mountpoint %s/usr > /dev/null 2>&1 && umount %s/usr", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib > /dev/null 2>&1 && umount %s/lib", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib64 > /dev/null 2>&1 && umount %s/lib64", work_dir, work_dir);
    exec_cmd("test -e %s/dev/null || rm -f %s/dev/null", work_dir, work_dir);
}