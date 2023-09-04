package cloud.oj.gateway.service;

import cloud.oj.gateway.dao.UserDao;
import cloud.oj.gateway.entity.Role;
import cloud.oj.gateway.entity.User;
import lombok.extern.slf4j.Slf4j;
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
        }
    };

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        var user = userDao.findByUsername(username);
        if (user != null) {
            int role = user.getRole();
            List<Role> roles;

            if (role == 0) {
                // Admin
                roles = ROLE_LIST;
            } else {
                roles = Arrays.asList(ROLE_LIST.get(1), ROLE_LIST.get(role));
            }

            user.setRoles(roles);
            return Mono.just(user);
        } else {
            var error = String.format("User(id=%s) not found.", username);
            log.error(error);
            return Mono.error(new UsernameNotFoundException(error));
        }
    }

    public User findById(Integer uid) {
        return userDao.findById(uid);
    }

    public String getSecret(Integer uid) {
        return userDao.getSecret(uid);
    }

    public String updateSecret(Integer uid, String newSecret) {
        userDao.updateSecret(uid, newSecret);
        return newSecret;
    }
}
