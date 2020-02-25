package top.cloudli.managerservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ProblemDaoTest {

    @Resource
    private ProblemDao problemDao;

    @Test
    void getAll() {
        List<List<?>> problems = problemDao.getAll("20164225133", 1, 10, false);
        log.info(String.valueOf(problems.get(0).size()));
        log.info(String.valueOf(problems.get(1).get(0)));
    }
}