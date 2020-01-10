package top.cloudli.loadbalanced.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 管理服务的调用
 */
@Slf4j
@Service
public class ManagerService {

    private final static String PROBLEM_URL = "http://manager-service/problem/";

    @Resource
    private RestTemplate restTemplate;

    public ResponseEntity<Object> getProblems() {
        return restTemplate.getForEntity(PROBLEM_URL, Object.class);
    }

    public ResponseEntity<Object> searchProblems(String keyword) {
        return restTemplate.getForEntity(PROBLEM_URL + "search/" + keyword, Object.class);
    }

    public ResponseEntity<String> addProblem(Object problem) {
        return restTemplate.postForEntity(PROBLEM_URL, problem, String.class);
    }

    public ResponseEntity<Object> updateProblem(Object problem) {
        return restTemplate.exchange(PROBLEM_URL, HttpMethod.PUT, new HttpEntity<>(problem), Object.class);
    }

    public ResponseEntity<Object> deleteProblem(int problemId) {
        return restTemplate.exchange(PROBLEM_URL + problemId, HttpMethod.DELETE, null, Object.class);
    }
}
