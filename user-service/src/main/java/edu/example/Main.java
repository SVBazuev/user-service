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
        System.out.println("Default charset: " + Charset.defaultCharset());
        System.out.println("Console encoding: " + System.getProperty("sun.stdout.encoding"));

        // Диагностика кодировки ввода
        System.out.println("Введите что-нибудь по-русски (например, Стас):");
        try {
            byte[] buffer = new byte[256];
            int len = System.in.read(buffer);
            System.out.println("Raw bytes (decimal): ");
            for (int i = 0; i < len; i++) {
                System.out.print(buffer[i] + " ");
            }
            System.out.println();

            // Пробуем интерпретировать как UTF-8
            String utf8 = new String(buffer, 0, len, StandardCharsets.UTF_8);
            System.out.println("As UTF-8: " + utf8);

            // Пробуем как UTF-16LE
            String utf16le = new String(buffer, 0, len, StandardCharsets.UTF_16LE);
            System.out.println("As UTF-16LE: " + utf16le);
        } catch (IOException e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
        }

        // Сборка зависимостей (без Spring)
        var sessionFactory = HibernateUtil.getSessionFactory();
        UserRepository userRepository = new HibernateUserRepository(sessionFactory);
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        new ConsoleApplication(userController).start();
        HibernateUtil.shutdown();
    }

    private static String bytesToHex(byte[] bytes, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }
}
/*
* help
* help read
* help create
* help update
* help delete
* help exit
*
*
*
*/
