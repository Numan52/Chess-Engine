package org.example.server.Service;

import org.example.server.Daos.UserDao;
import org.example.server.Entities.User;

import java.sql.SQLException;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(User user) throws SQLException, ClassNotFoundException {
        userDao.save(user);
    }

}
