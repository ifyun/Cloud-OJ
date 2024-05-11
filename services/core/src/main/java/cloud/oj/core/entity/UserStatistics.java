package cloud.oj.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserStatistics {
    private Results results;
    private List<Language> preference;
    private Map<String, Integer> activities;
}
