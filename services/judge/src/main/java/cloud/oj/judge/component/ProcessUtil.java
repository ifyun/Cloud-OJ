package cloud.oj.judge.component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ProcessUtil {
    public static void watchProcess(int seconds, Process process, AtomicBoolean timeout) {
        final int[] time = {seconds};
        new Thread(() -> {
            while (time[0] > 0) {
                if (!process.isAlive()) {
                    // 进程已结束
                    break;
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    process.destroyForcibly();
                    break;
                }

                time[0]--;
            }

            if (time[0] == 0) {
                // 超时 destroy
                timeout.set(true);
                process.destroyForcibly();
            }
        }).start();
    }
}
