package org.example.server.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.server.Database.DbUtils;
import org.example.server.Entities.User;
import org.example.server.Service.UserService;
import org.example.server.Utils.RequestUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class AuthServlet extends HttpServlet {
    private UserService userService;

    public AuthServlet(UserService userService) {
        this.userService = userService;
    }

    public AuthServlet() {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletPath();

        switch(path) {
            case "/login": handleLogin(request, response);
            case "/register": handleRegister(request, response);
        }
    }


    private void handleLogin(HttpServletRequest request, HttpServletResponse response) {

    }


    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = RequestUtils.toUser(request.getReader()).orElseThrow();
            userService.save(user);

            response.setStatus(200);
            response.getOutputStream().println("Registration Successful");
        } catch (Exception e) {
            System.err.println("Error registering user" + e.getMessage());
            response.setStatus(500);
            response.getOutputStream().println("Error registering user");
        }



    }

}
