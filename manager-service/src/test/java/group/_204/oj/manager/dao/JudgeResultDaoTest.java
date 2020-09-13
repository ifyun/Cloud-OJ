package group._204.oj.manager.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
class JudgeResultDaoTest {

    @Resource
    private JudgeResultDao judgeResultDao;

    @Test
    void getByUserId() {
        List<List<?>> result = judgeResultDao.getHistoryByUserId("root", 1, 10);
        log.info("Count: {}", result.get(0).size());
        log.info("Total: {}", result.get(1));
    }
}