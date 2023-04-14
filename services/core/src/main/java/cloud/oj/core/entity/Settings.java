package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {
    private String icp = "";
    private String icpUrl = "";
    private boolean alwaysShowRanking;
    private boolean showAllContest;
    private boolean showPassedPoints;
}
