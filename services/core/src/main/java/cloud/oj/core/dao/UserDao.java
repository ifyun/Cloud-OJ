package cloud.oj.core.dao;

import cloud.oj.core.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserDao {

    List<List<?>> getByFilter(Integer start, Integer limit, Integer filter, String filterValue);

    User getById(Integer uid);

    int add(User user);

    int update(User user);

    void updateAvatar(Integer uid);

    int delete(Integer uid);

    List<HashMap<Integer, Integer>> getLanguagePreference(Integer uid);

    List<HashMap<String, Integer>> getActivities(Integer uid, Integer year);

    HashMap<String, String> getResultStatistics(Integer uid);
}
