package cloud.oj.fileservice.service;

import cloud.oj.fileservice.entity.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class NotifyService {
    private final FanoutExchange testDataExchange;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public NotifyService(FanoutExchange testDataExchange, RabbitTemplate rabbitTemplate) {
        this.testDataExchange = testDataExchange;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送广播通知测试数据改变
     *
     * @param file      测试数据文件 {@link File}
     * @param isDeleted 是否删除
     */
    public void notifyTestData(File file, boolean isDeleted) {
        var path = file.getAbsolutePath();
        var rPath = path.substring(path.indexOf("test_data") + 9).replaceAll("\\\\", "/");
        var fileInfo = new FileInfo(rPath, file.lastModified(), isDeleted);
        rabbitTemplate.convertAndSend(testDataExchange.getName(), "", fileInfo);
        log.info(fileInfo.toString());
    }
}
