package cloud.oj.judge.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppHealth {
    private boolean fileSynced = false;
    private String detail = "waiting for sync.";

    public void setFileSynced(boolean fileSynced) {
        this.fileSynced = fileSynced;
        if (fileSynced) {
            detail = "All test data has been synchronized.";
        }
    }
}
