package cloud.oj.gateway.service;

import cloud.oj.gateway.entity.Role;
import cloud.oj.gateway.entity.User;
import cloud.oj.gateway.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

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
    public UserDetails loadUserByUsername(String username) {
        var user = userRepo.findByUsername(username);

        if (user.isEmpty()) {
            var error = String.format("User(id=%s) not found", username);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }

        int role = user.get().getRole();
        List<Role> roles;

        if (role == 0) {
            // ADMIN ROLE
            roles = ROLE_LIST;
        } else {
            roles = Arrays.asList(ROLE_LIST.get(1), ROLE_LIST.get(role));
        }

        user.get().setRoles(roles);

        return user.get();
    }

    public Optional<User> findById(Integer uid) {
        return userRepo.findById(uid);
    }

    public Optional<String> getSecret(Integer uid) {
        return userRepo.getSecret(uid);
    }

    public void updateSecret(Integer uid, String newSecret) {
        userRepo.updateSecret(uid, newSecret);
    }
}
