package cloud.oj.core.service;

import cloud.oj.core.dao.UserDao;
import cloud.oj.core.model.Msg;
import cloud.oj.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Resource
    private UserDao userDao;

    private String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public List<List<?>> getUsers(int page, int limit, String userId, String name) {
        if (userId != null && !userId.isEmpty()) {
            return userDao.getAllByUserId((page - 1) * limit, limit, userId);
        } else if (name != null && !name.isEmpty()) {
            return userDao.getAllByName((page - 1) * limit, limit, name);
        } else {
            return userDao.getAll((page - 1) * limit, limit);
        }
    }

    public User getUserInfo(String userId) {
        return userDao.getSingle(userId);
    }

    public boolean addUser(User user) {
        user.setRoleId(0);
        user.setSecret(newUUID());
        return userDao.add(user) == 1;
    }

    public Msg updateUser(User user) {
        if (user.getPassword() != null || user.getRoleId() != null) {
            user.setSecret(newUUID());
        }

        int status = userDao.update(user) == 1 ? 200 : 304;

        return new Msg(status, null);
    }

    public Msg updateProfile(String userId, User user) {
        user.setRoleId(null);
        user.setUserId(userId);

        if (user.getPassword() != null) {
            user.setSecret(newUUID());
        }

        return updateUser(user);
    }

    public Msg deleteUser(String userId) {
        var status = userDao.delete(userId) == 1 ? 204 : 410;
        return new Msg(status);
    }

    public HashMap<String, Object> getOverview(String userId, Integer year) {
        var preference = userDao.getLanguagePreference(userId);
        var activities = userDao.getActivities(userId, year);
        var statistics = userDao.getResultStatistics(userId);

        var overview = new HashMap<String, Object>();

        overview.put("preference", preference);
        overview.put("statistics", statistics);
        overview.put("activities", activities);

        return overview;
    }
}
