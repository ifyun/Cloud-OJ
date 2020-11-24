package group._204.oj.gateway.service;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.model.Role;
import group._204.oj.gateway.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Resource
    private UserDao userDao;

    private static final List<Role> ROLE_LIST = new ArrayList<Role>() {
        {
            add(new Role(0, "ROLE_USER"));
            add(new Role(1, "ROLE_USER_ADMIN"));
            add(new Role(2, "ROLE_PROBLEM_ADMIN"));
            add(new Role(3, "ROLE_ROOT"));
        }
    };

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userDao.findUserById(userId);

        if (user != null) {
            int roleId = user.getRoleId();
            List<Role> roles;

            if (roleId == 3) {
                // root has all roles
                roles = new ArrayList<>(ROLE_LIST);
            } else {
                roles = new ArrayList<>();
                roles.add(ROLE_LIST.get(0));
                roles.add(ROLE_LIST.get(roleId));
            }

            user.setRoles(roles);
            return user;
        } else {
            String error = String.format("User(%s) not found.", userId);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
    }
}
