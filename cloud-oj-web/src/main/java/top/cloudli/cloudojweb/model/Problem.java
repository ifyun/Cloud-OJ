package top.cloudli.cloudojweb.model;

import lombok.Data;

@Data
public class Problem {
    private int problemId;
    private String title;
    private String description;
    private String input;
    private String output;
    private String sampleInput;
    private String sampleOutput;
    private int score;
    private boolean enable;
    private String createAt;
}
