package cloud.oj.gateway.controller;

import cloud.oj.gateway.entity.User;
import cloud.oj.gateway.error.ErrorMessage;
import cloud.oj.gateway.filter.JwtUtil;
import cloud.oj.gateway.service.UserService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
public class AuthController {

    @Value("${app.token-valid-time:4}")
    private int tokenValidTime;

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    private String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 登录接口
     * <p>登录验证由过滤器完成，验证成功后此方法被执行，并返回 JWT</p>
     *
     * @param auth 此对象不为 null 说明登录验证成功
     * @return 登录成功返回 JWT Token，用户名或密码错误返回 401
     */
    @PostMapping(path = "login")
    public Mono<ResponseEntity<?>> login(Authentication auth) {
        if (auth == null) {
            var status = HttpStatus.INTERNAL_SERVER_ERROR;
            return Mono.just(ResponseEntity.status(status).body(new ErrorMessage(status, "认证失败")));
        }

        log.info("Login success, user={}.", auth.getName());

        var authorities = auth.getAuthorities();
        StringBuilder authoritiesString = new StringBuilder();

        for (var authority : authorities) {
            authoritiesString.append(authority.getAuthority()).append(",");
        }

        var user = (User) auth.getPrincipal();
        var secret = newUUID();

        user.setSecret(userService.updateSecret(user.getUserId(), secret));
        String jwt = JwtUtil.createJwt(user, authoritiesString, tokenValidTime);

        return Mono.just(ResponseEntity.ok(jwt));
    }

    /**
     * 登出
     * <p>更新用户 secret 使 JWT 失效</p>
     */
    @DeleteMapping(path = "logoff")
    public Mono<ResponseEntity<?>> logoff(@RequestHeader String Authorization) {
        var token = Authorization.substring(6);
        var userId = JwtUtil.getSubject(token);
        var secret = userService.getSecret(userId);

        try {
            JwtUtil.getClaims(token, secret);
            userService.updateSecret(userId, newUUID());
            log.info("Logoff: user={}.", userId);
            return Mono.just(ResponseEntity.ok("用户" + userId + "已退出"));
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * 刷新 Token
     * <p>用户信息修改时调用此接口</p>
     *
     * @param Authorization Bearer JWT Token
     * @param auth          若验证成功则不为 null
     * @return JWT Token
     */
    @GetMapping(path = "refresh_token")
    public Mono<ResponseEntity<?>> refreshToken(@RequestHeader String Authorization, Authentication auth) {
        var token = Authorization.substring(6);
        var userId = JwtUtil.getSubject(token);
        var user = userService.findById(userId);
        var authorities = auth.getAuthorities();
        var secret = newUUID();

        user.setSecret(userService.updateSecret(userId, secret));
        StringBuilder authoritiesString = new StringBuilder();

        for (var authority : authorities) {
            authoritiesString.append(authority.getAuthority()).append(",");
        }

        String jwt = JwtUtil.createJwt(user, authoritiesString, tokenValidTime);

        return Mono.just(ResponseEntity.ok(jwt));
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
