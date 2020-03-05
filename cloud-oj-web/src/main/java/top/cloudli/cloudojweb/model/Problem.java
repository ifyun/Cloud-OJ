package top.cloudli.cloudojweb.model;

import lombok.Data;

@Data
public class Problem {
    private int problemId;
    private Integer result;
    private String title;
    private String description;
    private long timeout;
    private String input;
    private String output;
    private String sampleInput;
    private String sampleOutput;
    private String category;
    private int score;
    private boolean enable;
    private String createAt;
}
