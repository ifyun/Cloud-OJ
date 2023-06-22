package cloud.oj.gateway.dao;

import cloud.oj.gateway.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findById(String userId);

    String getSecret(String userId);

    void updateSecret(String userId, String secret);
}
