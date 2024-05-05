package cloud.oj.gateway.filter;

import cloud.oj.gateway.entity.User;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class JwtUtil {
    private final static ObjectMapper mapper = new ObjectMapper();

    private static SecretKey stringToSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String createJwt(User user, Object authorities, Integer validTime) {
        var now = System.currentTimeMillis();
        var expire = now + validTime * 3600000L;

        return Jwts.builder().issuer("Cloud OJ")
                .issuedAt(new Date(now))
                .expiration(new Date(expire))
                .claim("uid", user.getUid())
                .claim("username", user.getUsername())
                .claim("nickname", user.getNickname())
                .claim("email", user.getEmail())
                .claim("section", user.getSection())
                .claim("hasAvatar", user.getHasAvatar())
                .claim("role", user.getRole())
                .claim("authorities", authorities)
                .signWith(stringToSecretKey(user.getSecret()))
                .compact();
    }

    /**
     * 验证并返回 JWT Body
     *
     * @return {@link Claims}
     */
    public static Claims getClaims(String jwt, String secret)
            throws JwtException, IllegalArgumentException {
        return Jwts.parser()
                .verifyWith(stringToSecretKey(secret))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    /**
     * 从 JWT 中取出 uid
     * <p>此操作不验证 JWT 签名</p>
     *
     * @return uid or null
     */
    public static Integer getUid(String jwt) {
        // JWT 是 Base64Url 编码
        var base64 = jwt.substring(jwt.indexOf('.') + 1, jwt.lastIndexOf('.'))
                .replaceAll("-", "+")
                .replaceAll("_", "/");

        var payload = new String(Base64.getDecoder().decode(base64));

        try {
            var node = mapper.readTree(payload);
            return node.get("uid").intValue();
        } catch (JacksonException e) {
            log.error("Cannot read uid from token");
            return null;
        }
    }
}
