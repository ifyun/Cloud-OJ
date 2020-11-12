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

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Resource
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userDao.findUserById(userId);

        if (user != null) {
            user.setRoles(new ArrayList<Role>() {
                {
                    add(new Role(user.getRoleId(), user.getRoleName()));
                }
            });

            return user;
        } else {
            String error = String.format("User(%s) not found.", userId);
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
    }
}
