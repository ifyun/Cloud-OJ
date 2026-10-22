package cloud.oj.core.entity;

import java.util.List;

public record SolutionSummary(
        Results results,
        List<LanguageUserUsage> language
) {
}
