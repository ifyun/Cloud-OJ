package cloud.oj.judge.component;

import cloud.oj.judge.config.AppHealth;
import cloud.oj.judge.service.DataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(DataSyncService.class)
public class FileSyncHealthIndicator implements HealthIndicator {

    private final AppHealth appHealth;

    @Autowired
    public FileSyncHealthIndicator(AppHealth appHealth) {
        this.appHealth = appHealth;
    }

    @Override
    public Health health() {
        if (appHealth.isFileSynced()) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("reason", appHealth.getDetail()).build();
        }
    }
}
