package cloud.oj.gateway.service;

import cloud.oj.gateway.dao.UserDao;
import cloud.oj.gateway.entity.Role;
import cloud.oj.gateway.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserDao userDao;

    private static final List<Role> ROLE_LIST = new ArrayList<>() {
        {
            add(new Role(0, "ROLE_ADMIN"));
            add(new Role(1, "ROLE_USER"));
            add(new Role(2, "ROLE_USER_ADMIN"));
            add(new Role(3, "ROLE_PROBLEM_ADMIN"));
        }
    };

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        var user = userDao.findById(username);
        if (user != null) {
            int roleId = user.getRoleId();
            List<Role> roles;

            if (roleId == 0) {
                // Admin 拥有所有角色
                roles = ROLE_LIST;
            } else {
                roles = Arrays.asList(ROLE_LIST.get(1), ROLE_LIST.get(roleId));
            }

            user.setRoles(roles);
            return Mono.just(user);
        } else {
            var error = String.format("User(id=%s) not found.", username);
            log.error(error);
            return Mono.error(new UsernameNotFoundException(error));
        }
    }

    public User findById(String userId) {
        return userDao.findById(userId);
    }

    @Cacheable(key = "#userId", cacheNames = "userSecret")
    public String getSecret(String userId) {
        log.info("查询 Secret");
        return userDao.getSecret(userId);
    }

    @CachePut(key = "#userId", cacheNames = "userSecret")
    public String updateSecret(String userId, String newSecret) {
        userDao.updateSecret(userId, newSecret);
        return newSecret;
    }
}
