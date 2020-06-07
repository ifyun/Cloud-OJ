package top.cloudli.managerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TestData {
    private List<String> input;
    private List<String> output;
}
