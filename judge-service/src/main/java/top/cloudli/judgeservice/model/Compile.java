package top.cloudli.judgeservice.model;

import lombok.Data;

@Data
public class Compile {
    private int id;
    private int solutionId;
    private int state;
    private String info;

    public Compile(int solutionId, int state, String info) {
        this.solutionId = solutionId;
        this.state = state;
        this.info = info;
    }
}
