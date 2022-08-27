package cloud.oj.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TestData {
    private List<String> input;
    private List<String> output;
}
