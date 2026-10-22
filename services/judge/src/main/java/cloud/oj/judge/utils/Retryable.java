package cloud.oj.judge.utils;

import cloud.oj.judge.function.ThrowingRunnable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Retryable {

    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run(ThrowingRunnable<Exception> r) {
        while (true) {
            try {
                r.run();
                break;
            } catch (Exception e) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }
    }
}
