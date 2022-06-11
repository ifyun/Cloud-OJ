package group._204.oj.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * todo 写注释提醒小助手
 * 快些注释啊！
 */
@SpringBootApplication
@EnableTransactionManagement
public class CoreServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(CoreServiceApp.class, args);
    }

}
