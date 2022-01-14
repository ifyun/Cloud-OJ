package group._204.oj.judge.component;

import group._204.oj.judge.config.AppHealth;
import group._204.oj.judge.service.DataSyncService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ConditionalOnBean(DataSyncService.class)
public class TestDataSyncHealthIndicator implements HealthIndicator {

    @Resource
    private AppHealth appHealth;

    @Override
    public Health health() {
        if (appHealth.isFileSynced()) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("reason", appHealth.getDetail()).build();
        }
    }
}
