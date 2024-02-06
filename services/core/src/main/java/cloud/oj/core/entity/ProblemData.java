package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProblemData {
    private Integer pid;
    private String title;
    private Boolean spj = false;
    @JsonProperty("SPJSource")
    private String SPJSource;
    private List<TestData> testData = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestData {
        private String fileName;
        private long size;
    }
}
