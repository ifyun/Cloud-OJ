#include <cstdarg>
#include "env_setup.h"

inline int exec_cmd(const char *fmt, ...) {
    char cmd[256];
    va_list ap;

    va_start(ap, fmt);
    vsprintf(cmd, fmt, ap);

    int r = system(cmd);
    va_end(ap);
    return r;
}

/**
 * @brief 设置运行环境
 */
void setup_env(const char *work_dir) {
    exec_cmd("test -d %s/proc || mkdir %s/proc", work_dir, work_dir);
    exec_cmd("test -d %s/etc || mkdir %s/etc", work_dir, work_dir);
    exec_cmd("test -d %s/dev || mkdir %s/dev", work_dir, work_dir);
    exec_cmd("test -d %s/bin || mkdir %s/bin", work_dir, work_dir);
    exec_cmd("test -d %s/usr || mkdir %s/usr", work_dir, work_dir);
    exec_cmd("test -d %s/lib || mkdir %s/lib", work_dir, work_dir);
    exec_cmd("test -d %s/lib64 || mkdir %s/lib64", work_dir, work_dir);
    exec_cmd("test -d %s/tmp || mkdir %s/tmp", work_dir, work_dir);
    exec_cmd("test -e %s/dev/null || mknod -m 666 %s/dev/null c 1 3", work_dir, work_dir);
    exec_cmd("chown root:root %s/dev/null", work_dir);
    // 挂载环境
    exec_cmd("mountpoint %s/proc > /dev/null 2>&1 || mount --bind -o ro /proc %s/proc", work_dir, work_dir);
    exec_cmd("mountpoint %s/etc > /dev/null 2>&1 || mount --bind -o ro /etc %s/etc", work_dir, work_dir);
    exec_cmd("mountpoint %s/bin > /dev/null 2>&1 || mount --bind -o ro /bin %s/bin", work_dir, work_dir);
    exec_cmd("mountpoint %s/usr > /dev/null 2>&1 || mount --bind -o ro /usr %s/usr", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib > /dev/null 2>&1 || mount --bind -o ro /lib %s/lib", work_dir, work_dir);
#ifdef __x86_64__
    exec_cmd("mountpoint %s/lib64 > /dev/null 2>&1 || mount --bind -o ro /lib64 %s/lib64", work_dir, work_dir);
#endif
    exec_cmd("ln -sf /etc/dotnet.runtimeconfig.json %s/Solution.runtimeconfig.json", work_dir);
}

/**
 * @brief 清理环境，解除挂载
 */
void end_env(const char *work_dir) {
    exec_cmd("unlink %s/Solution.runtimeconfig.json > /dev/null 2>&1", work_dir);
    exec_cmd("test -e %s/dev/null && rm -rf %s/dev/null", work_dir, work_dir);
    exec_cmd("mountpoint %s/proc > /dev/null 2>&1 && umount -l %s/proc", work_dir, work_dir);
    exec_cmd("mountpoint %s/etc > /dev/null 2>&1 && umount -l %s/etc", work_dir, work_dir);
    exec_cmd("mountpoint %s/bin > /dev/null 2>&1 && umount -l %s/bin", work_dir, work_dir);
    exec_cmd("mountpoint %s/usr > /dev/null 2>&1 && umount -l %s/usr", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib > /dev/null 2>&1 && umount -l %s/lib", work_dir, work_dir);
    exec_cmd("mountpoint %s/lib64 > /dev/null 2>&1 && umount -l %s/lib64", work_dir, work_dir);
    exec_cmd("rmdir %s/proc > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/dev > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/tmp > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/etc > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/bin > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/usr > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/lib > /dev/null 2>&1", work_dir);
    exec_cmd("rmdir %s/lib64 > /dev/null 2>&1", work_dir);
}
