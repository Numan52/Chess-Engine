package org.example.server.Daos;

import jakarta.servlet.annotation.WebListener;
import org.example.server.Database.DbUtils;
import org.example.server.Entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {

    public void save(User user) throws SQLException {
        try (
            Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement("insert into users (username, email, password) values (?, ?, ?)")
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Couldn't insert user: " + e.getMessage());
            throw e;
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        try (
            Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from users where username = ?")
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new User(
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password")
                    ));
                } else {
                    return Optional.empty();
                }

            }

        } catch (SQLException e) {
            System.err.println("Couldn't find user: " + e.getMessage());
            throw e;
        }
    }

}
