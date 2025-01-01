package org.example.server.Daos;

import jakarta.servlet.annotation.WebListener;
import org.example.server.Database.DbUtils;
import org.example.server.Entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {
    public void save(User user) throws SQLException, ClassNotFoundException {
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

}
