package org.example.server.Servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.server.Entities.User;
import org.example.server.Service.UserService;
import org.example.server.Utils.RequestUtils;

import java.io.IOException;
import java.util.NoSuchElementException;

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
            case "/login": handleLogin(request, response); break;
            case "/register": handleRegister(request, response); break;
        }
    }


    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Login request received");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.getReader());
            String username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();

            String token = userService.checkLogin(username, password);
            response.setStatus(HttpServletResponse.SC_OK);
            String jsonResponse = mapper.createObjectNode()
                    .put("message", "Login successful")
                    .put("token", token)
                    .toString();
            response.getWriter().write(jsonResponse);

        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.err.println("Incorrect Username or Password: " + e);
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Incorrect Username or Password");
            return;
        }
        catch (Exception e) {
            System.err.println(e + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An internal server error occurred.");
        }
    }


    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Register request received");
        try {
            User user = RequestUtils.toUser(request.getReader()).orElseThrow();
            userService.save(user);

            response.setStatus(200);
            response.getOutputStream().println("Registration Successful");
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            response.getOutputStream().println("Error registering user");
        }



    }

}
