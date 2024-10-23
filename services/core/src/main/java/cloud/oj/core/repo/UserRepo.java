package cloud.oj.core.repo;

import cloud.oj.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepo {

    private final JdbcClient client;

    /**
     * 判断用户名是否存在
     */
    public Boolean exists(String username) {
        return client.sql("select exists(select 1 from user where username = :username)")
                .param("username", username)
                .query(Boolean.class)
                .single();
    }

    public Optional<Integer> selectUidByUsername(String username) {
        return client.sql("select uid from user where username = :username")
                .param("username", username)
                .query(Integer.class)
                .optional();
    }

    public Optional<User> selectById(Integer uid) {
        return client.sql("""
                        select uid,
                               username,
                               nickname,
                               email,
                               section,
                               has_avatar,
                               star
                        from user
                        where uid = :uid
                          and deleted = false
                        """)
                .param("uid", uid)
                .query(User.class)
                .optional();
    }

    /**
     * 根据过滤条件查询用户
     *
     * @param filter 1: by username, 2: by nickname
     * @param value  Value of filter
     */
    public List<User> selectByFilter(Integer filter, String value, Integer page, Integer size) {
        return client.sql("""
                        select sql_calc_found_rows uid,
                                                   username,
                                                   nickname,
                                                   email,
                                                   section,
                                                   has_avatar,
                                                   star,
                                                   role,
                                                   create_at
                        from user
                        where deleted = false
                          and if(:filter = 1, username like concat(:value, '%'), true)
                          and if(:filter = 2, nickname like concat('%', :value, '%'), true)
                        order by role, create_at
                        limit :start, :count
                        """)
                .param("filter", filter)
                .param("value", value)
                .param("start", (page - 1) * size)
                .param("count", size)
                .query(User.class)
                .list();
    }

    public Integer insert(User user) {
        return client.sql("""
                        insert into `user`(username, nickname, password, secret, email, section, role)
                        values (:username, :nickname, :password, :secret, :email, :section, :role)
                        """)
                .paramSource(user)
                .update();
    }

    public Integer update(User user) {
        return client.sql("""
                        update `user`
                        set nickname = :nickname,
                            password = if(:password is not null, :password, password),
                            role = if(:role is not null, :role, role),
                            secret = if(:secret is not null, :secret, secret),
                            star = if(:star is not null, :star, star)
                        where uid = :uid
                        """)
                .paramSource(user)
                .update();
    }

    public Integer updateAvatar(Integer uid) {
        return client.sql("update user set has_avatar = true where uid = :uid")
                .param("uid", uid)
                .update();
    }

    public Integer delete(Integer uid) {
        return client.sql("update `user` set deleted = true where uid = :uid")
                .param("uid", uid)
                .update();
    }
}
