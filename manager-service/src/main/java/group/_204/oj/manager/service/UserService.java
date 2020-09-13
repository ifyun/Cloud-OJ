package group._204.oj.manager.service;

import group._204.oj.manager.dao.UserDao;
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
        return userDao.add(user) == 1;
    }

    public boolean updateUser(User user) {
        return userDao.update(user) == 1;
    }

    public boolean deleteUser(String userId) {
        return userDao.delete(userId) == 1;
    }
}
