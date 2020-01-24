package top.cloudli.cloudojweb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CloudOjWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudOjWebApplication.class, args);
        log.info("OJ Web 已启动.");
    }

}
