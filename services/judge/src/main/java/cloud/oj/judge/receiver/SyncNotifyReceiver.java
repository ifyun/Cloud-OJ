package cloud.oj.judge.receiver;

import cloud.oj.judge.entity.FileInfo;
import cloud.oj.judge.service.DataSyncService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnBean(DataSyncService.class)
public class SyncNotifyReceiver {

    private final DataSyncService dataSyncService;

    @Autowired
    public SyncNotifyReceiver(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

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
