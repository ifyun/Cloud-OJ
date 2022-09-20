/// 判题守护进程，使用 UNIX Domain Socket 通信
/// @author Cloud
#include <sys/un.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <cstdio>
#include <cerrno>
#include <csignal>
#include "judged.h"
#include "thread_pool.h"
#include "log.h"

int sfd;
ThreadPool *pool;

inline int cpu_cores() {
    cpu_set_t cs;
    CPU_ZERO(&cs);
    sched_getaffinity(0, sizeof(cs), &cs);
    return CPU_COUNT(&cs);
}

void sig_handler(int sig) {
    if (sig == SIGPIPE) {
        // 处理 SIGPIPE 信号，读写数据时意外断开会收到此信号
        ERROR("%s", strerror(errno));
    } else if (sig == SIGINT || sig == SIGTERM) {
        // 处理 Ctrl + C 和 SIGTERM，关闭线程池并退出
        INFO("%s", "stopping judged by SIGINT/SIGTERM");
        close(sfd);
        pool->destroy();
        delete pool;

        if (unlink(SV_SOCK_PATH) == -1) {
            ERROR("unlink %s failed", SV_SOCK_PATH);
            exit(errno);
        }

        exit(0);
    }
}

int main() {
    thread_name("main");
    struct sockaddr_un addr{};
    int cfd, cores;

    if (getuid() != 0) {
        ERROR("permission denied");
        exit(errno);
    }

    cores = cpu_cores();
    sfd = socket(AF_UNIX, SOCK_STREAM, 0);

    if (sfd == -1) {
        ERROR("socket: %s", strerror(errno));
        exit(errno);
    }

    if (remove(SV_SOCK_PATH) == -1 && errno != ENOENT) {
        ERROR("remove %s: %s", SV_SOCK_PATH, strerror(errno));
        exit(errno);
    }

    memset(&addr, 0, sizeof(struct sockaddr_un));
    addr.sun_family = AF_UNIX;
    strncpy(addr.sun_path, SV_SOCK_PATH, sizeof(addr.sun_path) - 1);

    if (bind(sfd, (struct sockaddr *) &addr, sizeof(struct sockaddr_un)) == -1) {
        ERROR("bind: %s.\n", strerror(errno));
        exit(errno);
    }

    if (listen(sfd, cores) == -1) {
        ERROR("listen: %s", strerror(errno));
        exit(errno);
    }

    INFO("judged start, listen %s", SV_SOCK_PATH);

    chmod(SV_SOCK_PATH, 0777);
    signal(SIGPIPE, sig_handler);
    signal(SIGINT, sig_handler);
    signal(SIGTERM, sig_handler);

    pool = new ThreadPool(cores);

    while (true) {
        cfd = accept(sfd, nullptr, nullptr);

        if (cfd == -1) {
            ERROR("%s", strerror(errno));
            exit(errno);
        }

        INFO("connected: socket=%d", cfd);
        pool->push_task(ThreadArgs(cfd));
    }
}
