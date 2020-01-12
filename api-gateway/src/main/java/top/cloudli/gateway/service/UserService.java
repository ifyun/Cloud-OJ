package top.cloudli.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.cloudli.gateway.dao.UserDao;
import top.cloudli.gateway.model.Role;
import top.cloudli.gateway.model.User;

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

        user.setRoles(new ArrayList<Role>(){
            {
                add(new Role(user.getRoleId(), user.getRoleName()));
            }
        });

        return user;
    }

    public void login(String userId, String password) {
        User user = userDao.findUserById(userId);
        if (password.equals(user.getPassword()))
            log.debug("Login success.");
    }
}
