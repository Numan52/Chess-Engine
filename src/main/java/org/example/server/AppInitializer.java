package org.example.server;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.server.Daos.UserDao;
import org.example.server.Service.UserService;
import org.example.server.Servlets.AuthServlet;

@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // Instantiate DAO and Service
        UserDao userDao = new UserDao();
        UserService userService = new UserService(userDao);

        // Instantiate and register servlet
        AuthServlet authServlet = new AuthServlet(userService);
        context.addServlet("AuthServlet", authServlet).addMapping("/register", "/login");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
