package group._204.oj.judge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {
    private final static String THREAD_PREFIX = "JUDGE-";

    @Value("${project.judge-pool-size:4}")
    private int poolSize;

    @PostConstruct
    private void init() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        if (poolSize >= cpuCores) {
            poolSize = cpuCores - 1;
        }

        log.info("Judge Pool Size: {}.", poolSize);
    }

    @Bean
    public Executor judgeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(1);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public HashMap<String, Integer> cpus() {
        HashMap<String, Integer> cpus = new HashMap<>();

        // 将 CPU 核心 ID 与线程名称绑定
        for (int i = 0; i < poolSize; i++) {
            cpus.put(THREAD_PREFIX + 1 + i, i);
        }

        return cpus;
    }
}
