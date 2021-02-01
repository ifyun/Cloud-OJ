package group._204.oj.manager.service;

import group._204.oj.manager.dao.UserDao;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Resource
    private UserDao userDao;

    public List<List<?>> getUsers(int page, int limit) {
        return userDao.getAll((page - 1) * limit, limit);
    }

    public User getUserInfo(String userId) {
        return userDao.getSingle(userId);
    }

    public boolean addUser(User user) {
        user.setRoleId(0);
        return userDao.add(user) == 1;
    }

    public Msg updateUser(User user) {
        int status = userDao.update(user) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    public Msg updateProfile(String userId, User user) {
        user.setRoleId(null);
        user.setUserId(userId);
        return updateUser(user);
    }

    public Msg deleteUser(String userId) {
        int status = userDao.delete(userId) == 1 ? 204 : 410;
        return new Msg(status);
    }

    public HashMap<String, Object> getOverview(String userId, Integer year) {
        List<HashMap<Integer, Integer>> preference = userDao.getLanguagePreference(userId);
        List<HashMap<String, Integer>> activities = userDao.getActivities(userId, year);
        HashMap<String, String> statistics = userDao.getResultStatistics(userId);

        HashMap<String, Object> overview = new HashMap<>();

        overview.put("preference", preference);
        overview.put("statistics", statistics);
        overview.put("activities", activities);

        return overview;
    }
}
