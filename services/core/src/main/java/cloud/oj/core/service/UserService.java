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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CommonRepo commonRepo;

    private final UserRepo userRepo;

    private final ScoreboardRepo scoreboardRepo;

    private final SolutionRepo solutionRepo;

    private final UserStatisticRepo userStatisticRepo;

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private static final Set<String> VALID_TIMEZONES = Set.of(TimeZone.getAvailableIDs());

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
        var total = data.isEmpty() ? 0 : data.get(0)._total;

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
    public UserStatistics getOverview(Integer uid, String tz, Integer year) {
        var timezone = Optional.ofNullable(tz)
                .filter(VALID_TIMEZONES::contains)
                .orElse("+08:00");

        var zone = ZoneId.of(timezone);
        long start;
        long end;

        if (year != null && year != 0) {
            start = LocalDate.of(year, 1, 1).atStartOfDay(zone).toInstant().toEpochMilli();
            end = LocalDate.of(year + 1, 1, 1).atStartOfDay(zone).toInstant().toEpochMilli();
        } else {
            var now = LocalDate.now(zone);
            // 明天 0 点 ~ 去年今天 0 点
            start = now.minusYears(1).atStartOfDay(zone).toInstant().toEpochMilli();
            end = now.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
        }

        commonRepo.setTimezone(timezone);

        var userStatistic = new UserStatistics();
        var dataMap = userStatisticRepo.selectHeatmap(uid, start, end)
                .stream()
                .collect(Collectors.toMap(HeatmapData::timestamp, HeatmapData::value, (t, v) -> t));
        var heatmap = new ArrayList<HeatmapData>(366);
        // 生成 [start, end) 之间的时间戳，填充 heatmap
        var startDate = Instant.ofEpochMilli(start).atZone(zone).toLocalDate();
        var endDate = Instant.ofEpochMilli(end).atZone(zone).toLocalDate();

        for (var date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            var timestamp = date.atStartOfDay(zone).toInstant().toEpochMilli();
            var value = dataMap.getOrDefault(timestamp, 0);
            heatmap.add(new HeatmapData(timestamp, value));
        }

        userStatistic.setHeatmap(heatmap);
        userStatistic.setPreference(userStatisticRepo.selectLanguages(uid));
        userStatistic.setResults(userStatisticRepo.selectResults(uid));

        return userStatistic;
    }
}
