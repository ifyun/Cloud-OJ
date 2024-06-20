package cloud.oj.judge.controller;

import cloud.oj.judge.config.RabbitConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.DetailsParameters;
import com.rabbitmq.http.client.domain.QueueInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class QueueInfoController {

    private final ObjectMapper mapper = new ObjectMapper();

    private final DetailsParameters params = new DetailsParameters();

    private final Client rabbitClient;

    @JsonIncludeProperties({
            "name", "consumers", "messages", "messages_ready",
            "messages_unacknowledged", "messages_details"
    })
    private static abstract class MixIn {
    }

    @PostConstruct
    private void init() {
        params.lengths(60, 5);
        params.messageRates(60, 5);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        mapper.addMixIn(QueueInfo.class, MixIn.class);
    }

    /**
     * 返回所有队列信息
     */
    @GetMapping(path = "queue_info")
    public SseEmitter queueInfo() {
        var emitter = new SseEmitter(0L);
        var executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                while (true) {
                    try {
                        var event = SseEmitter.event()
                                .name("message")
                                .data(getQueueInfo());
                        emitter.send(event);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        var error = SseEmitter.event()
                                .name("error")
                                .data("Iternal Server Error");
                        emitter.send(error);
                        emitter.complete();
                        break;
                    }
                }
            }
        });

        return emitter;
    }

    private String getQueueInfo() throws JsonProcessingException {
        var submit = rabbitClient.getQueue("/", RabbitConfig.SUBMIT_QUEUE, params);
        var judge = rabbitClient.getQueue("/", RabbitConfig.JUDGE_QUEUE, params);
        var map = new HashMap<String, Object>();

        map.put(RabbitConfig.SUBMIT_QUEUE, submit);
        map.put(RabbitConfig.JUDGE_QUEUE, judge);

        return mapper.writeValueAsString(map);
    }
}
