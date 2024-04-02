package cloud.oj.gateway.service;

import cloud.oj.gateway.repo.UserRepo;
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

    private final UserRepo userRepo;

    private static final List<Role> ROLE_LIST = new ArrayList<>() {
        {
            add(new Role(0, "ROLE_ADMIN"));
            add(new Role(1, "ROLE_USER"));
        }
    };

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByUsername(username).flatMap(user -> {
            if (user == null) {
                var error = String.format("User(id=%s) not found", username);
                log.error(error);
                return Mono.error(new UsernameNotFoundException(error));
            }

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
        });
    }

    public Mono<User> findById(Integer uid) {
        return userRepo.findById(uid);
    }

    public Mono<String> getSecret(Integer uid) {
        return userRepo.getSecret(uid);
    }

    public Mono<Void> updateSecret(Integer uid, String newSecret) {
       return userRepo.updateSecret(uid, newSecret);
    }
}
