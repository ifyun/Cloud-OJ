package cloud.oj.gateway.filter;

import cloud.oj.gateway.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

public class JwtUtil {
    private static SecretKey stringToSecretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public static String createJwt(User user, Object authorities, Integer validTime) {
        var now = System.currentTimeMillis();
        var expire = now + validTime * 3600000L;

        return Jwts.builder()
                .setIssuer("Cloud OJ")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expire))
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
        return Jwts.parserBuilder()
                .setSigningKey(stringToSecretKey(secret))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 从 JWT 中取出 uid
     * <p>此操作不验证 JWT 签名</p>
     *
     * @return uid or null
     */
    public static Integer getUid(String jwt) throws StringIndexOutOfBoundsException {
        // JWT 是 Base64Url 编码
        var base64 = jwt.substring(jwt.indexOf('.') + 1, jwt.lastIndexOf('.'))
                .replaceAll("-", "+")
                .replaceAll("_", "/");

        var payload = new String(Base64.getDecoder().decode(base64));
        var matcher = Pattern.compile("\"uid\":((.+?)|(\\d*))").matcher(payload);

        if (matcher.find()) {
            return Integer.valueOf(matcher.group(2));
        } else {
            return null;
        }
    }
}
