package cloud.oj.core.repo;

import cloud.oj.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final DatabaseClient client;

    /**
     * 判断用户名是否存在
     */
    public Mono<Boolean> exists(String username) {
        return client.sql("select 1 from user where username = :username")
                .bind("username", username)
                .mapValue(Boolean.class)
                .first();
    }

    public Mono<User> selectById(Integer uid) {
        return client.sql("select uid," +
                        "         username," +
                        "         nickname," +
                        "         email," +
                        "         section," +
                        "         has_avatar," +
                        "         star" +
                        "  from user" +
                        "  where uid = :uid" +
                        "    and deleted = false"
                ).bind("uid", uid)
                .mapProperties(User.class)
                .first();
    }

    /**
     * 根据过滤条件查询用户
     *
     * @param filter 1: by username, 2: by nickname
     * @param value  Value of filter
     */
    public Flux<User> selectByFilter(Integer filter, String value, Integer page, Integer size) {
        return client.sql("select sql_calc_found_rows uid," +
                        "                             username," +
                        "                             nickname," +
                        "                             email," +
                        "                             section," +
                        "                             has_avatar," +
                        "                             star," +
                        "                             role," +
                        "                             create_at" +
                        "  from user" +
                        "  where deleted = false" +
                        "    and if(:filter = 1, username like concat(:value, '%'), true)" +
                        "    and if(:filter = 2, nickname like concat('%', :value, '%'), true)" +
                        "  order by role, create_at" +
                        "  limit :start, :count")
                .bind("filter", filter)
                .bind("value", value)
                .bind("start", (page - 1) * size)
                .bind("count", size)
                .mapProperties(User.class)
                .all();
    }

    public Mono<Long> insert(User user) {
        return client.sql("insert into `user`(username, nickname, password, secret, email, section, role)" +
                        "      values (:username, :nickname, :password, :secret, :email, :section, :role)")
                .bindProperties(user)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> update(User user) {
        return client.sql("update `user`" +
                        "  set nickname = :nickname," +
                        "      password = if(:password is not null, :password, password)," +
                        "      role = if(:role is not null, :role, role)," +
                        "      secret = if(:secret is not null, :secret, secret)," +
                        "      star = if(:star is not null, :star, star)" +
                        "  where uid = :uid")
                .bindProperties(user)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> updateAvatar(Integer uid) {
        return client.sql("update user set has_avatar = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }

    public Mono<Long> delete(Integer uid) {
        return client.sql("update `user` set deleted = true where uid = :uid")
                .bind("uid", uid)
                .fetch()
                .rowsUpdated();
    }
}
