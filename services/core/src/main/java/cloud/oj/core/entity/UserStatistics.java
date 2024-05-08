package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserStatistics {
    private Results results;
    private List<Language> preference;
    private List<Activity> activities;
}
