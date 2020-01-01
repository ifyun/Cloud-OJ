package group._204.oj.manager.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface StatisticsDao {
    List<HashMap<Integer, Integer>> getPreference(String userId);
    List<HashMap<String, Integer>> getActivities(String userId, Integer year);
}
