package cloud.oj.core.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Ranking {
    private Integer rank;
    private Integer uid;
    private String username;
    private String nickname;
    private String realName;
    private Integer committed;
    private Integer passed;
    private Double score;
    private Boolean hasAvatar;
    private Boolean star;

    private Set<ScoreDetail> details;

    @JsonGetter
    public String badge() {
        // 🏅
        if (rank == 1) {
            return "\uD83C\uDFC5";
        }

        // 🥇
        if (rank < 5) {
            return "\uD83E\uDD47";
        }

        // 🥈
        if (rank < 10) {
            return "\uD83E\uDD48";
        }

        // 🥉
        if (rank < 25) {
            return "\uD83E\uDD49";
        }

        // 🎉
        if (rank < 35) {
            return "\uD83C\uDF89";
        }

        return "";
    }
}
