package top.cloudli.gateway.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.gateway.model.User;

@Mapper
public interface UserDao {
    User findUserById(String userId);
}
