package org.example.server.Service;

import org.example.server.Daos.UserDao;
import org.example.server.Entities.User;
import org.example.server.Utils.RequestUtils;
import org.example.server.jwt.JwtUtils;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void save(User user) throws Exception {
        String[] password = RequestUtils.hashPassword(user.getPassword(), null);
        user.setPassword(password[0] + "#" + password[1]); // store salt and password together
        userDao.save(user);
    }


    public String checkLogin(String username, String password) throws Exception {
        User user = userDao.findByUsername(username).orElseThrow();


        int saltIndex = user.getPassword().indexOf("#");
        String salt = user.getPassword().substring(0, saltIndex);
        String storedHash = user.getPassword().substring(saltIndex + 1);
        String hash = RequestUtils.hashPassword(password, salt)[1];

        System.out.println("user.getPassword: " + storedHash);
        System.out.println("hashedPassword: " + hash);

        if (!storedHash.equals(hash)) {
            throw new IllegalArgumentException("Wrong password");
        }
        String token = JwtUtils.generateToken(username);
        return token;
    }


    public Optional<User> findByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }
}
