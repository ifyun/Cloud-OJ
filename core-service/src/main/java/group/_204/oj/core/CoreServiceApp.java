package group._204.oj.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CoreServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(CoreServiceApp.class, args);
    }

}
