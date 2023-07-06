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
                .setSubject(user.getUserId())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expire))
                .claim("name", user.getName())
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
     * 从 JWT 中取出 Subject
     * <p>此操作不验证 JWT 签名</p>
     *
     * @return Subject or null
     */
    public static String getSubject(String jwt) throws StringIndexOutOfBoundsException {
        var payload = new String(
                Base64.getDecoder().decode(jwt.substring(jwt.indexOf('.') + 1, jwt.lastIndexOf('.')))
        );

        var matcher = Pattern.compile("\"sub\":(\"(.+?)\"|(\\d*))").matcher(payload);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
}
