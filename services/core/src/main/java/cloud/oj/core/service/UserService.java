package cloud.oj.core.service;

import cloud.oj.core.entity.PageData;
import cloud.oj.core.entity.User;
import cloud.oj.core.entity.UserStatistic;
import cloud.oj.core.error.GenericException;
import cloud.oj.core.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<PageData<User>> getUsersByFilter(Integer filter, String filterValue, Integer page, Integer limit) {
        return userRepo.selectByFilter(filter, filterValue, page, limit)
                .collectList()
                .zipWith(commonRepo.selectFoundRows())
                .map(t -> new PageData<>(t.getT1(), t.getT2()));
    }

    public Mono<User> getUserInfo(Integer uid) {
        return userRepo.selectById(uid);
    }

    public Mono<HttpStatus> addUser(User user) {
        return userRepo.exists(user.getUsername())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "用户名重复"));
                    }

                    user.setRole(1);
                    user.setPassword(bcrypt.encode(user.getPassword()));
                    user.setSecret(newUUID());

                    return Mono.just(user);
                })
                .then(userRepo.insert(user))
                .flatMap(rows -> rows == 1 ?
                        Mono.just(HttpStatus.CREATED) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "请求数据可能不正确"))
                );
    }

    /**
     * 更新用户信息(管理员)
     */
    public Mono<HttpStatus> updateUser(User user) {
        if (user.getUid() == 1 && user.getRole() != 0) {
            return Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不准移除初始管理员权限"));
        }

        if (user.getPassword() != null) {
            user.setPassword(bcrypt.encode(user.getPassword()));
        }

        return userRepo.update(user)
                .flatMap(rows -> rows > 0 ?
                        Mono.just(HttpStatus.OK) :
                        Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "更新失败"))
                );
    }

    /**
     * 更新用户信息(个人)
     */
    public Mono<HttpStatus> updateProfile(Integer uid, User user) {
        user.setUid(uid);
        user.setRole(null);
        return updateUser(user);
    }

    /**
     * 逻辑删除用户及其相关信息
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<HttpStatus> deleteUser(Integer uid) {
        return uid.equals(1) ?
                Mono.error(new GenericException(HttpStatus.BAD_REQUEST, "不准删除初始管理员")) :
                userRepo.delete(uid)
                        .then(scoreboardRepo.deleteByUid(uid))
                        .then(solutionRepo.deleteByUid(uid))
                        .thenReturn(HttpStatus.NO_CONTENT);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<UserStatistic> getOverview(Integer uid, Integer year, String tz) {
        String timezone = "+8:00";

        if (tz != null && Set.of(TimeZone.getAvailableIDs()).contains(tz)) {
            timezone = ZonedDateTime.now(ZoneId.of(tz)).getOffset().getId();
        }

        var userStatistic = new UserStatistic();

        return commonRepo.setTimezone(timezone)
                .then(userStatisticRepo.selectLanguages(uid).collectList())
                .doOnNext(userStatistic::setPreference)
                .then(userStatisticRepo.selectActivities(uid, year).collectList())
                .doOnNext(userStatistic::setActivities)
                .then(userStatisticRepo.selectResults(uid))
                .map(result -> {
                    userStatistic.setResult(result);
                    return userStatistic;
                });
    }
}
