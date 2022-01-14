package group._204.oj.judge.config;

import lombok.Data;

@Data
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
