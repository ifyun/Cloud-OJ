package group._204.oj.manager.dao;

import group._204.oj.manager.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {

    List<List<?>> getAll(Integer start, Integer limit);

    int add(User user);

    int update(User user);

    int delete(String userId);
}
