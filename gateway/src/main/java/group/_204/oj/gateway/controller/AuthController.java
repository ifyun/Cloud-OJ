package group._204.oj.gateway.controller;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.model.Msg;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class AuthController {

    @Resource
    private UserDao userDao;

    /**
     * 登出
     */
    @DeleteMapping(path = "logoff")
    public ResponseEntity<?> logoff(@RequestHeader String userId, @RequestHeader String token) {
        String secret = userDao.getSecret(userId);

        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

            if (claims.getSubject().equals(userId)) {
                log.info("Update secret of user({}): {}", userId, userDao.updateSecret(userId) == 1);
                log.info("User({}) logoff.", userId);
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok(new Msg("已退出"));
            } else {
                return ResponseEntity.status(403).body(new Msg("JWT Token 与 userId 不匹配"));
            }
        } catch (JwtException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
