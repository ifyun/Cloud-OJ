package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Results(
        @JsonProperty("AC")
        int AC,

        @JsonProperty("PA")
        int PA,

        @JsonProperty("WA")
        int WA,

        @JsonProperty("MLE")
        int MLE,

        @JsonProperty("TLE")
        int TLE,

        @JsonProperty("OLE")
        int OLE,

        @JsonProperty("RE")
        int RE,

        @JsonProperty("CE")
        int CE
) {
}
