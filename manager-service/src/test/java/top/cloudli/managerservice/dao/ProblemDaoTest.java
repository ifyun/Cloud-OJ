package top.cloudli.managerservice.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProblemDaoTest {

    @Resource
    private ProblemDao problemDao;

    @Test
    void getProblems() {
        assertTrue(problemDao.getAll().size() > 0);
    }

    @Test
    void searchByTitle() {
        assertTrue(problemDao.searchByTitle("%Hello%").size() > 0);
    }
}