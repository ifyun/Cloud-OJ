package top.cloudli.judgeservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class SolutionDaoTest {

    @Resource
    private SolutionDao solutionDao;

    @Test
    void getSubmitted() {
        log.info(String.valueOf(solutionDao.getSubmitted()));
    }

    @Test
    void getJudged() {
        log.info(String.valueOf(solutionDao.getJudged()));
    }
}