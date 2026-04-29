package edu.example;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.example.cli.ConsoleApplication;
import edu.example.core.controller.UserController;
import edu.example.core.repository.UserRepository;
import edu.example.core.service.UserService;
import edu.example.infrastructure.repository.HibernateUserRepository;
import edu.example.infrastructure.util.HibernateUtil;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        var sessionFactory = HibernateUtil.getSessionFactory();
        UserRepository userRepository = new HibernateUserRepository(sessionFactory);
        UserService userService = new UserService(userRepository, sessionFactory);
        UserController userController = new UserController(userService);

        new ConsoleApplication(userController).start();
        HibernateUtil.shutdown();
    }
}
