package cloud.oj.fileservice.service;

import cloud.oj.fileservice.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

@Service
@Slf4j
public class NotifyService {

    @Value("${app.file-dir}")
    private String fileDir;

    @Resource
    private FanoutExchange testDataExchange;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送广播通知测试数据改变
     *
     * @param file 测试数据文件
     * @param isDeleted 是否删除
     */
    public void notifyTestData(File file, boolean isDeleted) {
        var path = file.getAbsolutePath().replace(fileDir + "test_data", "");
        var fileInfo = new FileInfo(path, file.lastModified(), isDeleted);
        rabbitTemplate.convertAndSend(testDataExchange.getName(), "", fileInfo);
        log.info(fileInfo.toString());
    }
}
