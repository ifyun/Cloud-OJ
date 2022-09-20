/// judged 线程池
/// @author Cloud
#ifndef _THREAD_POOL_H
#define _THREAD_POOL_H 1

#include <queue>
#include <condition_variable>
#include <thread>

#define BUF_SIZE 2048

const std::string &thread_name(const std::string &name = "");

/**
 * @brief 线程参数
 */
struct ThreadArgs {
    int cfd;            // 客户端 Socket FD
    bool stop{false};   // 设为 true 表示停止所有线程

    explicit ThreadArgs(int cfd) : stop(false) {
        this->cfd = cfd;
    }

    explicit ThreadArgs(bool stop) : cfd(0) {
        this->stop = stop;
    }
};

class ThreadPool {
private:
    std::vector<std::thread> threads;
    std::queue<ThreadArgs> task_queue;
    std::condition_variable cond;
    mutable std::mutex mtx;
private:
    /**
     * 线程执行函数，从队列取走任务
     * @arg i 线程名称后缀
     * @return 队列中存在 stop 标志时，函数返回，线程结束
     */
    void *take_task(int i);

    /**
    * 创建进程执行 judge
    * @param cfd 客户端 Socket
    * @param argv 判题参数
    */
    static void exec(int &cfd, char *argv[]);

public:
    explicit ThreadPool(int size);

    void push_task(ThreadArgs args);

    /**
     * 销毁线程池，向队列加入一个 stop 标志，等待所有线程退出
     */
    void destroy();
};

#endif //_THREAD_POOL_H
