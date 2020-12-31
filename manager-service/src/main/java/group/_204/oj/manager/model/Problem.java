package group._204.oj.manager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "题目")
public class Problem {

    @ApiModelProperty(hidden = true)
    private Integer contestId;

    @ApiModelProperty(hidden = true)
    private String contestName;

    @ApiModelProperty(hidden = true)
    private Date startAt;

    @ApiModelProperty(hidden = true)
    private Date endAt;

    private Integer problemId;

    @ApiModelProperty(hidden = true)
    private Integer result;

    @ApiModelProperty(hidden = true)
    private Integer passed;

    @ApiModelProperty(value = "题目名称")
    private String title;

    @ApiModelProperty(value = "题目描述")
    private String description;

    @ApiModelProperty(value = "分类，多个用逗号分隔")
    private String category;

    @ApiModelProperty(value = "超时时间，单位：ms", example = "1000")
    private Long timeout;

    private Integer languages;

    private Integer score;
    private Boolean enable;
    private Date createAt;

    @ApiModelProperty(hidden = true)
    private TestData testData;
}
