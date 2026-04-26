package edu.example;


import edu.example.cli.api.ConsoleApplication;
import edu.example.core.controller.UserController;
import edu.example.core.repository.UserRepository;
import edu.example.core.service.UserService;
import edu.example.infrastructure.repository.HibernateUserRepository;
import edu.example.infrastructure.util.HibernateUtil;


public class Main {
    public static void main(String[] args) {
        // Сборка зависимостей (без Spring)
        var sessionFactory = HibernateUtil.getSessionFactory();
        UserRepository userRepository = new HibernateUserRepository(sessionFactory);
        UserService userService = new UserService(userRepository, sessionFactory);
        UserController userController = new UserController(userService);

        new ConsoleApplication(userController).start();
    }
}

/*
* help&create
* help&create
* help&create
* help&create
*/
