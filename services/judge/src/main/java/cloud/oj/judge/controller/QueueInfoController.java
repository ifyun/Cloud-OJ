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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class QueueInfoController {

    private final ObjectMapper mapper = new ObjectMapper();

    private final DetailsParameters params = new DetailsParameters();

    private final Client rabbitClient;

    private String queueInfo = "";

    private long lastRequestTime = 0L;

    @JsonIncludeProperties({
            "name", "consumers", "messages", "messages_ready",
            "messages_unacknowledged", "messages_details",
            "effective_policy_definition"
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
    @GetMapping(path = "queue_info", produces = "application/json")
    public String queueInfo() throws JsonProcessingException {
        var now = System.currentTimeMillis();

        if (lastRequestTime == 0 || now - lastRequestTime > 2500L) {
            var submit = rabbitClient.getQueue("/", RabbitConfig.SUBMIT_QUEUE, params);
            var judge = rabbitClient.getQueue("/", RabbitConfig.JUDGE_QUEUE, params);
            var map = Map.of(RabbitConfig.SUBMIT_QUEUE, submit, RabbitConfig.JUDGE_QUEUE, judge);

            queueInfo = mapper.writeValueAsString(map);
            lastRequestTime = now;
        }

        return queueInfo;
    }
}
