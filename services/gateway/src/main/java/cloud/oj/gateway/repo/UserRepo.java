package cloud.oj.gateway.repo;

import cloud.oj.gateway.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final JdbcClient client;

    public Optional<User> findById(Integer uid) {
        return client.sql("""
                        select uid,
                               username,
                               nickname,
                               password,
                               secret,
                               email,
                               section,
                               has_avatar,
                               role
                        from user
                        where uid = :uid
                          and deleted = false
                        """
                ).param("uid", uid)
                .query(User.class)
                .optional();
    }

    public Optional<User> findByUsername(String username) {
        return client.sql("""
                        select uid,
                               username,
                               nickname,
                               password,
                               secret,
                               email,
                               section,
                               has_avatar,
                               role
                        from user
                        where username = :username
                          and deleted = false
                        """
                ).param("username", username)
                .query(User.class)
                .optional();
    }

    public Optional<String> getSecret(Integer uid) {
        return client.sql("select secret from user where uid = :uid")
                .param("uid", uid)
                .query(String.class)
                .optional();
    }

    public void updateSecret(Integer uid, String secret) {
        client.sql("update user set secret = :secret where uid = :uid")
                .param("secret", secret)
                .param("uid", uid)
                .update();
    }
}
