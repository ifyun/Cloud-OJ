package group._204.oj.manager.service;

import group._204.oj.manager.dao.UserDao;
import group._204.oj.manager.model.Msg;
import group._204.oj.manager.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserDao userDao;

    public List<List<?>> getUsers(int page, int limit) {
        return userDao.getAll((page - 1) * limit, limit);
    }

    public boolean addUser(User user) {
        user.setRoleId(0);
        return userDao.add(user) == 1;
    }

    public Msg updateUser(User user) {
        int status = userDao.update(user) == 1 ? 200 : 304;
        return new Msg(status, null);
    }

    public Msg deleteUser(String userId) {
        int status = userDao.delete(userId) == 1 ? 204 : 410;
        return new Msg(status);
    }
}
