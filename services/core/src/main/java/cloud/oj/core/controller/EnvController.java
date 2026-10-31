package cloud.oj.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.oj.core.repo.EnvRepo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EnvController {

    private final EnvRepo envRepo;

    @GetMapping("env")
    public List<Map<String, Object>> getEnv() {
        return envRepo.get();
    }
}
