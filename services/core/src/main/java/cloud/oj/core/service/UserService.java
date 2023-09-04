package cloud.oj.core.service;

import cloud.oj.core.dao.DatabaseConfig;
import cloud.oj.core.dao.RankingDao;
import cloud.oj.core.dao.SolutionDao;
import cloud.oj.core.dao.UserDao;
import cloud.oj.core.entity.User;
import cloud.oj.core.error.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    private final RankingDao rankingDao;

    private final SolutionDao solutionDao;

    private final DatabaseConfig databaseConfig;

    public UserService(UserDao userDao, RankingDao rankingDao,
                       SolutionDao solutionDao, DatabaseConfig databaseConfig) {
        this.userDao = userDao;
        this.rankingDao = rankingDao;
        this.solutionDao = solutionDao;
        this.databaseConfig = databaseConfig;
    }

    private String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public List<List<?>> getUsersByFilter(Integer page, Integer limit, Integer filter, String filterValue) {
        return userDao.getByFilter((page - 1) * limit, limit, filter, filterValue);
    }

    public Optional<User> getUserInfo(Integer uid) {
        return Optional.ofNullable(userDao.getById(uid));
    }

    public HttpStatus addUser(User user) {
        user.setRole(1);
        user.setSecret(newUUID());
        if (userDao.add(user) == 1) {
            return HttpStatus.CREATED;
        } else {
            throw new GenericException(400, "请求数据可能不正确");
        }
    }

    public HttpStatus updateUser(User user) {
        if (user.getPassword() != null || user.getRole() != null) {
            user.setSecret(newUUID());
        }

        if (userDao.update(user) == 1) {
            return HttpStatus.OK;
        } else {
            throw new GenericException(400, String.format("用户(%s)更新失败", user.getUid()));
        }
    }

    public HttpStatus updateProfile(Integer uid, User user) {
        user.setRole(null);
        user.setUid(uid);

        if (user.getPassword() != null) {
            user.setSecret(newUUID());
        }

        if (userDao.update(user) == 1) {
            return HttpStatus.OK;
        } else {
            throw new GenericException(400, String.format("用户(%s)更新失败", user.getUid()));
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public HttpStatus deleteUser(Integer uid) {
        if (uid.equals(1)) {
            throw new GenericException(400, "不准删除初始管理员");
        }

        if (userDao.delete(uid) == 1 && rankingDao.deleteByUser(uid) == 1) {
            solutionDao.deleteByUser(uid);
            return HttpStatus.NO_CONTENT;
        } else {
            throw new GenericException(410, String.format("用户(%s)不存在", uid));
        }
    }

    @Transactional
    public HashMap<String, Object> getOverview(Integer uid, Integer year, String tz) {
        if (tz != null && Set.of(TimeZone.getAvailableIDs()).contains(tz)) {
            var offset = ZonedDateTime.now(ZoneId.of(tz)).getOffset().getId();
            databaseConfig.setTimezone(offset);
        } else {
            databaseConfig.setTimezone("+8:00");
        }

        var preference = userDao.getLanguagePreference(uid);
        var activities = userDao.getActivities(uid, year);
        var statistics = userDao.getResultStatistics(uid);

        var overview = new HashMap<String, Object>();

        overview.put("preference", preference);
        overview.put("statistics", statistics);
        overview.put("activities", activities);

        return overview;
    }
}
