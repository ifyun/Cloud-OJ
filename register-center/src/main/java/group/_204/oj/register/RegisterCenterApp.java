package group._204.oj.register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@Slf4j
@SpringBootApplication
@EnableEurekaServer
public class RegisterCenterApp {

    public static void main(String[] args) {
        SpringApplication.run(RegisterCenterApp.class, args);
    }

}
