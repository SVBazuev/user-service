package edu.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.example.cli.api.ConsoleApplication;
import edu.example.core.controller.UserController;
import edu.example.core.repository.UserRepository;
import edu.example.core.service.UserService;
import edu.example.infrastructure.repository.HibernateUserRepository;
import edu.example.infrastructure.util.HibernateUtil;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        // Сборка зависимостей (без Spring)
        var sessionFactory = HibernateUtil.getSessionFactory();
        UserRepository userRepository = new HibernateUserRepository(sessionFactory);
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        new ConsoleApplication(userController).start();
        HibernateUtil.shutdown();
    }
}

/*
* help&create
* help&create
* help&create
* help&create
*/
