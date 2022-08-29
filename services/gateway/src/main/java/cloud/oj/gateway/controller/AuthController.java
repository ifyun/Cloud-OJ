package cloud.oj.gateway.controller;

import cloud.oj.gateway.dao.UserDao;
import cloud.oj.gateway.entity.User;
import cloud.oj.gateway.error.ErrorMessage;
import cloud.oj.gateway.filter.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class AuthController {

    @Value("${app.token-valid-time:4}")
    private int tokenValidTime;

    @Resource
    private UserDao userDao;

    private String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 登录接口
     * <p>登录验证由过滤器完成，验证成功后此方法被执行，并返回 JWT</p>
     *
     * @param authentication 此对象不为 null 说明登录验证成功
     * @return 登录成功返回 {@link User}，用户名或密码错误返回 401
     */
    @PostMapping(path = "login")
    public Mono<ResponseEntity<?>> login(Authentication authentication) {
        if (authentication == null) {
            var status = HttpStatus.INTERNAL_SERVER_ERROR;
            return Mono.just(ResponseEntity.status(status).body(new ErrorMessage(status, "认证失败")));
        }

        log.info("Login success, user={}.", authentication.getName());

        var authorities = authentication.getAuthorities();
        StringBuilder authoritiesString = new StringBuilder();

        for (var authority : authorities) {
            authoritiesString.append(authority.getAuthority()).append(",");
        }

        var user = (User) authentication.getPrincipal();
        var userId = user.getUserId();
        // 更新 Secret
        log.info("Update secret: [user: {}, success: {}]", userId, userDao.updateSecret(userId, newUUID()) == 1);
        user.setSecret(userDao.getSecret(userId));
        // 生成 JWT
        var expireAt = new Date(System.currentTimeMillis() + tokenValidTime * 3600000L);
        String jwt = JwtUtil.createJwt(userId, authoritiesString, expireAt, user.getSecret());

        user.setToken(jwt);
        user.setExpire(expireAt);

        return Mono.just(ResponseEntity.ok(user));
    }

    /**
     * 登出
     * <p>更新用户 secret 使 JWT 失效</p>
     */
    @DeleteMapping(path = "logoff")
    public Mono<ResponseEntity<?>> logoff(@RequestHeader String token) {
        var userId = JwtUtil.getSubject(token);
        var secret = userDao.getSecret(userId);

        try {
            JwtUtil.getClaims(token, secret);
            log.debug("Logoff: user={}.", userId);
            log.debug("Update secret: [user: {}, success: {}]", userId, userDao.updateSecret(userId, newUUID()) == 1);
            return Mono.just(ResponseEntity.ok("用户" + userId + "已退出"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * 验证 Token 是否有效
     * <p>此接口用于主动验证 JWT，验证操作由过滤器完成</p>
     *
     * @return <p>200：验证通过，40x：Token 无效</p>
     */
    @GetMapping(path = "verify")
    public Mono<ResponseEntity<?>> verify() {
        return Mono.just(ResponseEntity.ok().build());
    }
}
