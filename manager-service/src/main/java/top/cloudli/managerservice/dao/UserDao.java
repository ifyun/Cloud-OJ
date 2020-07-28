package top.cloudli.managerservice.dao;

import org.apache.ibatis.annotations.Mapper;
import top.cloudli.managerservice.model.User;

import java.util.List;

@Mapper
public interface UserDao {

    List<List<?>> getAll(Integer start, Integer limit);

    int add(User user);

    int update(User user);

    int delete(String userId);
}
