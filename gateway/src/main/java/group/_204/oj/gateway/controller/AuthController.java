package group._204.oj.gateway.controller;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.filter.LoginFilter;
import group._204.oj.gateway.model.Msg;
import group._204.oj.gateway.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@Api(tags = "身份认证")
public class AuthController {

    @Resource
    private UserDao userDao;

    /**
     * 此接口仅用于生成文档，不会被调用，登录由 {@link LoginFilter} 实现
     */
    @ApiOperation(value = "登录", notes = "登录成功返回用户信息，包括 JWT Token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "ID", required = true),
            @ApiImplicitParam(name = "password", value = "密码 = bcrypt(md5(明文密码))", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "登录成功", response = User.class),
            @ApiResponse(code = 400, message = "用户名或密码错误")
    })
    @PostMapping(path = "login")
    public User login(String username, String password) throws NotImplementedException {
        assert username != null;
        assert password != null;
        throw new NotImplementedException();
    }

    @ApiOperation(value = "登出")
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
