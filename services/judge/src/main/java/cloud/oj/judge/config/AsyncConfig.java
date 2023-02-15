package cloud.oj.judge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {
    private final static String THREAD_PREFIX = "JUDGE-";

    private final AppConfig appConfig;

    @Autowired
    public AsyncConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public Executor judgeExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setCorePoolSize(appConfig.getJudgePoolSize());
        executor.setMaxPoolSize(appConfig.getJudgePoolSize());
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public HashMap<String, Integer> cpus() {
        var cpus = new HashMap<String, Integer>();

        if (appConfig.getJudgePoolSize() == 1) {
            cpus.put(THREAD_PREFIX + 1, 0);
        } else {
            // 将 CPU 核心 ID 与线程名称绑定，CPU0 留给提交线程
            for (int i = 1; i <= appConfig.getJudgePoolSize(); i++) {
                cpus.put(THREAD_PREFIX + i, i);
            }
        }

        return cpus;
    }
}
