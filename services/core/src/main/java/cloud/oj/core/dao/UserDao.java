package cloud.oj.core.dao;

import cloud.oj.core.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserDao {

    List<List<?>> getByFilter(Integer start, Integer limit, Integer filter, String filterValue);

    User getById(String userId);

    int add(User user);

    int update(User user);

    void updateAvatar(String userId);

    int delete(String userId);

    List<HashMap<Integer, Integer>> getLanguagePreference(String userId);

    List<HashMap<String, Integer>> getActivities(String userId, Integer year);

    HashMap<String, String> getResultStatistics(String userId);
}
