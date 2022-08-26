package cloud.oj.gateway.dao;

import cloud.oj.gateway.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findUserById(String userId);
    String getSecret(String userId);
    int updateSecret(String userId, String secret);
}
