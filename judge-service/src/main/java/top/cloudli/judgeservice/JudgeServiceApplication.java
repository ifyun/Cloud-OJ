package top.cloudli.judgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JudgeServiceApplication.class, args);
    }

}
