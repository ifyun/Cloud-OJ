package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Results {
    @JsonProperty("AC")
    private Integer AC;

    @JsonProperty("WA")
    private Integer WA;

    @JsonProperty("CE")
    private Integer CE;

    @JsonProperty("RE")
    private Integer RE;

    @JsonProperty("MLE")
    private Integer MLE;

    @JsonProperty("TLE")
    private Integer TLE;

    private Integer total;
}
