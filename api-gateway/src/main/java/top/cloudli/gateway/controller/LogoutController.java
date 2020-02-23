package top.cloudli.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import top.cloudli.gateway.dao.UserDao;

import javax.annotation.Resource;

@RestController
public class LogoutController {

    @Resource
    private UserDao userDao;

    @DeleteMapping("/logoff")
    public ResponseEntity<?> logout(String userId) {
        userDao.updateSecret(userId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("已退出");
    }
}
