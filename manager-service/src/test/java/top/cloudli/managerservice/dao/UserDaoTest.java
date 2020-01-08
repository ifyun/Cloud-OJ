package top.cloudli.managerservice.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class UserDaoTest {

    @Resource
    private UserDao userDao;

    @Test
    void getAll() {
        log.info(String.valueOf(userDao.getAll()));
    }
}