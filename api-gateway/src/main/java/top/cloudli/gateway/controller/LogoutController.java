package top.cloudli.gateway.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import top.cloudli.gateway.dao.UserDao;

import javax.annotation.Resource;

@RestController
public class LogoutController {

    @Resource
    private UserDao userDao;

    @DeleteMapping("/logoff")
    public ResponseEntity<?> logout(String userId, @RequestHeader String token) {
        try {
            String user_id = verifyUserIdFromToken(userId, token);
            userDao.updateSecret(user_id);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("已退出");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String verifyUserIdFromToken(String userId, String token) {
        String secret = userDao.getSecret(userId);

        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
