package cloud.oj.judge.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Objects;

/**
 * 自定义 Log Appender，将日志保存到数据库
 */
public class DBAppender extends AppenderBase<ILoggingEvent> {

    private final String appName;

    private final String instanceId;

    private final JdbcClient client;

    public DBAppender(Environment env, JdbcClient client) {
        appName = Objects.requireNonNull(env.getProperty("spring.application.name")).toUpperCase();
        instanceId = Objects.requireNonNull(env.getProperty("spring.cloud.consul.discovery.instance-id")).toUpperCase();
        this.client = client;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        var sql = """
                insert into log(service, instance_id, level, thread, class_name, message, time)
                values (:service, :instanceId, :level, :thread, :className, :message, :time)
                """;
        var level = eventObject.getLevel().levelStr;
        var thread = eventObject.getThreadName();
        var className = eventObject.getLoggerName();
        var msg = eventObject.getFormattedMessage();
        var time = eventObject.getTimeStamp();

        client.sql(sql)
                .param("service", appName)
                .param("instanceId", instanceId)
                .param("level", level)
                .param("thread", thread)
                .param("className", className)
                .param("message", msg)
                .param("time", time)
                .update();
    }
}
