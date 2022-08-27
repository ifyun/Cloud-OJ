package cloud.oj.core.service;

import cloud.oj.core.dao.UserDao;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

    public Optional<User> getUserInfo(String userId) {
        return Optional.ofNullable(userDao.getSingle(userId));
    }

    public Integer addUser(User user) {
        user.setRoleId(0);
        user.setSecret(newUUID());
        if (userDao.add(user) == 1) {
            return HttpStatus.CREATED.value();
        } else {
            throw new GenericException(HttpStatus.BAD_REQUEST.value(), "请求数据可能不正确");
        }
    }

    public Integer updateUser(User user) {
        if (user.getPassword() != null || user.getRoleId() != null) {
            user.setSecret(newUUID());
        }

        if (userDao.update(user) == 1) {
            return HttpStatus.OK.value();
        } else {
            throw new GenericException(
                    HttpStatus.NOT_MODIFIED.value(),
                    String.format("%s 用户更新失败", user.getUserId())
            );
        }
    }

    public Integer updateProfile(String userId, User user) {
        user.setRoleId(null);
        user.setUserId(userId);

        if (user.getPassword() != null) {
            user.setSecret(newUUID());
        }

        return updateUser(user);
    }

    public Integer deleteUser(String userId) {
        if (userDao.delete(userId) == 1) {
            return HttpStatus.NO_CONTENT.value();
        } else {
            throw new GenericException(HttpStatus.GONE.value(), String.format("用户 %s 不存在", userId));
        }
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
