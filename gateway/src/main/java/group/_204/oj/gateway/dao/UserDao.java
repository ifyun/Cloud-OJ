package group._204.oj.gateway.dao;

import org.apache.ibatis.annotations.Mapper;
import group._204.oj.gateway.model.User;

@Mapper
public interface UserDao {
    User findUserById(String userId);
    String getSecret(String userId);
    int updateSecret(String userId, String secret);
}
