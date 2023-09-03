#include <cstring>
#include <unistd.h>
#include <sys/wait.h>
#include "thread_pool.h"
#include "common.h"
#include "log.h"

const std::string &thread_name(const std::string &name) {
    const thread_local std::string _name = name;
    return _name;
}

static inline void build_err_res(char *des, char *err) {
    sprintf(des, R"({"code": %d, "error": "%s"})", INTERNAL_ERROR, err);
}

static inline void write_to(int &fd, const void *_buf, size_t _n) {
    if (write(fd, _buf, _n) == -1) {
        ERROR("write to %d: %s", fd, strerror(errno));
    }
}

ThreadPool::ThreadPool(int size) {
    for (auto i = 0; i < size; i++) {
        auto th = std::thread([this, i] { take_task(i); });
        threads.push_back(std::move(th));
    }

    INFO("pool(size=%d) created", size);
}

void ThreadPool::destroy() {
    push_task(ThreadArgs(true));

    for (auto &th: threads) {
        th.join();
    }

    INFO("pool(size=%d) exit", threads.size());
}

void ThreadPool::push_task(ThreadArgs args) {
    std::lock_guard<std::mutex> lock(mtx);
    task_queue.push(args);

    if (args.stop) {
        cond.notify_all();
    } else {
        cond.notify_one();
    }
}

void *ThreadPool::take_task(int i) {
    thread_name("JUDGE-" + std::to_string(i));
    while (true) {
        std::unique_lock<std::mutex> lock(mtx);
        // 队列为空时阻塞
        while (task_queue.empty()) {
            cond.wait(lock);
        }

        ThreadArgs args = task_queue.front();

        if (args.stop) {
            // 检测到停止标志，结束线程
            return nullptr;
        }

        task_queue.pop();
        lock.unlock();

        char *argv[16];
        char buf[BUF_SIZE];
        ssize_t read_size;

        while ((read_size = read(args.cfd, buf, BUF_SIZE)) > 0) {
            buf[read_size] = '\0';
            argv[0] = (char *) "judge";
            split(argv + 1, buf, " ");
            exec(args.cfd, argv);
        }

        if (read_size == -1) {
            ERROR("read: %s", strerror(errno));
        }

        if (close(args.cfd) == -1) {
            ERROR("close socket: %s", strerror(errno));
        }
    }
}

void ThreadPool::exec(int &cfd, char *argv[]) {
    pid_t pid;
    int pip[2];

    if (pipe(pip) == -1) {
        char res[32];
        char *err = strerror(errno);
        ERROR("pipe: %s", err);
        build_err_res(res, err);
        write_to(cfd, res, strlen(res));
        return;
    }

    pid = fork();

    if (pid < 0) {
        char *err = strerror(errno);
        char res[32];
        ERROR("fork: %s", err);
        build_err_res(res, err);
        write_to(cfd, res, strlen(res));
    } else if (pid == 0) {
        dup2(pip[1], STDOUT_FILENO);

        if (execvp(argv[0], argv) == -1) {
            char res[32];
            char *err = strerror(errno);
            ERROR("execvp: %s", err);
            build_err_res(res, err);
            write_to(pip[1], res, strlen(res));
            _exit(errno);
        }
    } else {
        size_t read_size;
        char buf[BUF_SIZE];
        waitpid(pid, nullptr, 0);

        if ((read_size = read(pip[0], buf, BUF_SIZE)) == -1) {
            char res[32];
            char *err = strerror(errno);
            ERROR("read: %s", err);
            build_err_res(res, err);
            write_to(cfd, res, strlen(res));
            return;
        }

        write_to(cfd, buf, read_size);
        close(pip[0]);
        close(pip[1]);
    }
}