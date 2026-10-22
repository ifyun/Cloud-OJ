package cloud.oj.judge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class AsyncConfig {
    private final static String THREAD_PREFIX = "JUDGE-";

    public final static ThreadLocal<Integer> CPU_ID = new ThreadLocal<>();

    private final AppConfig appConfig;

    public AsyncConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * 阻塞策略，线程都被占用时阻塞提交
     */
    private static class BlockPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    throw new RejectedExecutionException(r + " rejected: " + e.getMessage());
                }
            }
        }
    }

    @Bean
    public Executor judgeExecutor() {
        var counter = new AtomicInteger(0);
        var cpus = appConfig.getCpus();
        var threads = appConfig.getCpus().size();
        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(threads);
        executor.setMaxPoolSize(threads);
        // 队列由 RabbitMQ 承担，容量设为 0 构造同步队列
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new BlockPolicy());
        executor.setThreadFactory(r -> {
            var t = new Thread(r);
            var i = counter.getAndIncrement();

            t.setName(THREAD_PREFIX + i);
            // 将 CPU 与线程名称绑定
            CPU_ID.set(cpus.get(i));
            log.info("{} -> CPU-{}", t.getName(), i);

            return t;
        });
        executor.initialize();
        executor.setPrestartAllCoreThreads(true);

        return executor.getThreadPoolExecutor();
    }
}
