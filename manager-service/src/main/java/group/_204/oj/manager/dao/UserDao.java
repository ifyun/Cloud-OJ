package group._204.oj.manager.dao;

import group._204.oj.manager.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserDao {

    List<List<?>> getAll(Integer start, Integer limit);

    User getSingle(String userId);

    int add(User user);

    int update(User user);

    int delete(String userId);

    List<HashMap<Integer, Integer>> getLanguagePreference(String userId);

    List<HashMap<String, Integer>> getActivities(String userId, Integer year);

    HashMap<String, String> getResultStatistics(String userId);
}
