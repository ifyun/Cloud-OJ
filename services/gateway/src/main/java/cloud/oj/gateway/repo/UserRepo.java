package cloud.oj.gateway.repo;

import cloud.oj.gateway.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final DatabaseClient client;

    public Mono<User> findById(Integer uid) {
        return client.sql("select uid," +
                        "         username," +
                        "         nickname," +
                        "         password," +
                        "         secret," +
                        "         email," +
                        "         section," +
                        "         has_avatar," +
                        "         role" +
                        "  from user" +
                        "  where uid = :uid" +
                        "    and deleted = false"
                ).bind("uid", uid)
                .mapProperties(User.class)
                .first();
    }

    public Mono<User> findByUsername(String username) {
        return client.sql("select uid," +
                        "         username," +
                        "         nickname," +
                        "         password," +
                        "         secret," +
                        "         email," +
                        "         section," +
                        "         has_avatar," +
                        "         role" +
                        "  from user\n" +
                        "  where username = :username" +
                        "    and deleted = false"
                ).bind("username", username)
                .mapProperties(User.class)
                .first();
    }

    public Mono<String> getSecret(Integer uid) {
        return client.sql("select secret from user where uid = :uid")
                .bind("uid", uid)
                .mapValue(String.class)
                .first();
    }

    public Mono<Void> updateSecret(Integer uid, String secret) {
        return client.sql("update user set secret = :secret where uid = :uid")
                .bind("secret", secret)
                .bind("uid", uid)
                .then();
    }
}
