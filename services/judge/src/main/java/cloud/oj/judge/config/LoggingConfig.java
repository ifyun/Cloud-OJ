package cloud.oj.judge.config;

import ch.qos.logback.classic.LoggerContext;
import cloud.oj.judge.logging.DBAppender;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class LoggingConfig {

    @Bean
    public DBAppender dbAppender(Environment env, JdbcClient client) {
        var appender = new DBAppender(env, client);
        appender.start();
        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        var logger = loggerContext.getLogger("ROOT");
        logger.addAppender(appender);
        return appender;
    }
}
