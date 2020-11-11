package group._204.oj.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ManagerServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ManagerServiceApp.class, args);
    }

}
