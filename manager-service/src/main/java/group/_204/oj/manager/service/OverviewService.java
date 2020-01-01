package group._204.oj.manager.service;

import group._204.oj.manager.dao.StatisticsDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
public class OverviewService {

    @Resource
    private StatisticsDao statisticsDao;

    public HashMap<String, Object> getOverview(String userId, Integer year) {
        List<HashMap<Integer, Integer>> preference = statisticsDao.getPreference(userId);
        List<HashMap<String, Integer>> activities = statisticsDao.getActivities(userId, year);
        HashMap<String, Object> overview = new HashMap<>();
        overview.put("preference", preference);
        overview.put("activities", activities);
        return overview;
    }
}
