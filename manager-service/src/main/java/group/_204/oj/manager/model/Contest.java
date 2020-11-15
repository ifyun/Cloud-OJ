package group._204.oj.manager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "竞赛/作业")
public class Contest {
    private Integer contestId;
    private String contestName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endAt;

    @ApiModelProperty(value = "语言使用二进制掩码表示，每一位对应一种语言，为 1 表示允许使用")
    private int languages;
    private boolean started;
    private boolean ended;
}
