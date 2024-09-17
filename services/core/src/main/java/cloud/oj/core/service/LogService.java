package cloud.oj.core.service;

import cloud.oj.core.entity.Log;
import cloud.oj.core.repo.LogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepo logRepo;

    public List<Log> getLatest10(Long time) {
        return logRepo.selectLatest10(time == null ? 0 : time);
    }

    public List<String> getRange(Long start, Long end) {
        return logRepo.selectRange(start, end)
                .stream()
                .map(Log::toString)
                .toList();
    }
}
