package cloud.oj.judge.receiver;

import com.rabbitmq.client.Channel;
import cloud.oj.judge.model.FileInfo;
import cloud.oj.judge.service.DataSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnBean(DataSyncService.class)
public class SyncNotifyReceiver {

    @Resource
    private DataSyncService dataSyncService;

    @RabbitHandler
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(),
            exchange = @Exchange(name = "fanout.test_data", type = ExchangeTypes.FANOUT)
    ))
    public void handleSyncNotify(@Payload FileInfo fileInfo, @Headers Map<String, Object> headers, Channel channel) {
        dataSyncService.syncFile(fileInfo);

        try {
            channel.basicAck((Long) headers.get(AmqpHeaders.DELIVERY_TAG), false);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
