package cloud.oj.gateway.dao;

import cloud.oj.gateway.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findById(Integer uid);

    User findByUsername(String username);

    String getSecret(Integer userId);

    void updateSecret(Integer uid, String secret);
}
