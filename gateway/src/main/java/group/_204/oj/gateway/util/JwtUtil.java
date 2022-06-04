package group._204.oj.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JwtUtil {
    private static SecretKey stringToSecretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public static String createJwt(String userId, Object authorities, Date expiration, String secret) {
        return Jwts.builder()
                .claim("authorities", authorities)
                .setIssuer("Cloud OJ")
                .setSubject(userId)
                .setExpiration(expiration)
                .signWith(stringToSecretKey(secret))
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
    public static String getSubject(String jwt) {
        String payload = new String(
                Base64.getDecoder().decode(jwt.substring(jwt.indexOf('.') + 1, jwt.lastIndexOf('.')))
        );

        Matcher matcher = Pattern.compile("\"sub\":(\"(.+?)\"|(\\d*))").matcher(payload);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
}
