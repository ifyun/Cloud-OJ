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

    private boolean singleThread = false;

    @Autowired
    public AsyncConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean isSingleThread() {
        return singleThread;
    }

    @Bean
    public Executor judgeExecutor() {
        var threads = appConfig.getCpus().size();

        if (threads == 1) {
            singleThread = true;
        } else {
            threads -= 1;
        }

        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(THREAD_PREFIX);
        executor.setCorePoolSize(threads);
        executor.setMaxPoolSize(threads);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public HashMap<String, Integer> cpus() {
        var cpuMap = new HashMap<String, Integer>();
        var cpuList = appConfig.getCpus();

        // 将 CPU 与线程名称绑定，第 1 个留给提交线程
        for (int i = 1; i < cpuList.size(); i++) {
            log.info("{} -> CPU-{}", THREAD_PREFIX + i, i);
            cpuMap.put(THREAD_PREFIX + i, i);
        }

        return cpuMap;
    }
}
