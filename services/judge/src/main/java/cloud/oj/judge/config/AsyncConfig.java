package cloud.oj.judge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {
    private final static String THREAD_PREFIX = "JUDGE-";

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
                    log.error(e.getMessage());
                    throw new RejectedExecutionException("Task " + r + " rejected");
                }
            }
        }
    }

    @Bean
    public Executor judgeExecutor() {
        var threads = appConfig.getCpus().size();
        var executor = new ThreadPoolTaskExecutor();

        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setCorePoolSize(threads);
        executor.setMaxPoolSize(threads);
        // 队列由 RabbitMQ 承担，容量设为 0 构造同步队列
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new BlockPolicy());
        executor.initialize();

        return executor;
    }

    @Bean
    public HashMap<String, Integer> cpus() {
        var cpuMap = new HashMap<String, Integer>();
        var cpuList = appConfig.getCpus();

        // 将 CPU 与线程名称绑定
        // 注意：线程池 ID 从 1 开始
        for (int i = 0; i < cpuList.size(); i++) {
            log.info("{} -> CPU-{}", THREAD_PREFIX + (i + 1), cpuList.get(i));
            cpuMap.put(THREAD_PREFIX + (i + 1), cpuList.get(i));
        }

        return cpuMap;
    }
}
