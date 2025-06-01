package cloud.oj.core.service;

import cloud.oj.core.entity.*;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CommonRepo commonRepo;

    private final UserRepo userRepo;

    private final ScoreboardRepo scoreboardRepo;

    private final SolutionRepo solutionRepo;

    private final UserStatisticRepo userStatisticRepo;

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 根据过滤条件分页查询用户
     *
     * @param filter 过滤条件
     * @param page   页数
     * @param limit  每页数量
     * @return {@link PageData} of {@link User}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PageData<User> getUsersByFilter(UserFilter filter, Integer page, Integer limit) {
        if (filter == null) {
            filter = new UserFilter();
        }

        var data = userRepo.selectByFilter(filter, page, limit);
        var total = (commonRepo.selectFoundRows());

        return new PageData<>(data, total);
    }

    /**
     * 查询用户个人信息
     *
     * @param uid 用户 Id
     * @return {@link User}
     */
    public User getUserInfo(Integer uid, boolean admin) {
        var user = userRepo.selectById(uid);

        if (user.isEmpty()) {
            throw new GenericException(HttpStatus.NOT_FOUND, "找不到用户");
        }

        var u = user.get();

        // 仅允许管理员/用户自己查看用户名和真实姓名
        if (!admin) {
            u.setUsername(null);
            u.setRealName(null);
        }

        return u;
    }

    /**
     * 创建用户
     *
     * @param user {@link User}
     */
    public void addUser(User user) {
        if (userRepo.exists(user.getUsername())) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "用户名重复");
        }

        user.setRole(1);
        user.setPassword(bcrypt.encode(user.getPassword()));
        user.setSecret(newUUID());

        if (userRepo.insert(user) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
        }
    }

    /**
     * 更新用户信息(管理员)
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        if (user.getUid() == 1) {
            if (user.getRole() != 0) {
                throw new GenericException(HttpStatus.BAD_REQUEST, "不准移除初始管理员权限");
            }
            // 初始管理员不允许修改用户名
            user.setUsername(null);
        }

        if (user.getPassword() != null) {
            user.setPassword(bcrypt.encode(user.getPassword()));
        }

        if (userRepo.update(user) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "操作失败");
        }
    }

    /**
     * 更新用户信息(个人)
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(User user) {
        user.setRole(null);
        updateUser(user);
    }

    /**
     * 逻辑删除用户及其相关信息
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteUser(Integer uid) {
        if (uid.equals(1)) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "不准删除初始管理员");
        }

        if (userRepo.delete(uid) == 0 || scoreboardRepo.deleteByUid(uid) == 0 || solutionRepo.deleteByUid(uid) == 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "删除失败");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserStatistics getOverview(Integer uid, Integer year, String tz) {
        String timezone = "+8:00";

        if (tz != null && Set.of(TimeZone.getAvailableIDs()).contains(tz)) {
            timezone = ZonedDateTime.now(ZoneId.of(tz)).getOffset().getId();
        }

        commonRepo.setTimezone(timezone);

        var userStatistic = new UserStatistics();

        userStatistic.setPreference(userStatisticRepo.selectLanguages(uid));
        userStatistic.setResults(userStatisticRepo.selectResults(uid));

        var acCounts = userStatisticRepo.selectActivities(uid, year);
        // date -> count
        var activities = new HashMap<String, Integer>();
        AcCount last = null;
        // 原始数据按 (problem_id, date) 分组，处理重复的数据
        for (var t : acCounts) {
            if (last != null) {
                // 日期重复，题目不同，合并 count
                if (last.getDate().equals(t.getDate())) {
                    t.setCount(t.getCount() + last.getCount());
                } else if (last.getPid().equals(t.getPid())) {
                    // 题目 Id 重复，保留日期较小的
                    last = t;
                    continue;
                }
            }

            activities.put(t.getDate(), t.getCount());
            last = t;
        }

        userStatistic.setActivities(activities);
        return userStatistic;
    }
}
