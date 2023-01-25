package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    private String icp;
    private String icpUrl;
    private String siteName;
    private boolean hideLogo;
    private boolean showRankingAfterEnded;
    private boolean showNotStartedContest;
}
