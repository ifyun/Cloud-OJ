package cloud.oj.core.model;

import lombok.Data;

@Data
public class Settings {
    private String icp;
    private String icpUrl;
    private String siteName;
    private boolean hideLogo;
    private boolean showRankingAfterEnded;
    private boolean showNotStartedContest;
}
