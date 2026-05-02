package edu.example.cli.util;


import java.util.List;


import edu.example.core.dto.UserResponse;


public class Printer {
    private Printer() {}

    public static void printWelcome() {
        System.out.println("Добро пожаловать в UserService!");
        System.out.println("Введите 'help' для списка команд.");
    }

    public static void printUnknownCommand(String command) {
        System.out.println(
            "Неизвестная команда: "
            + command
            + ". Введите 'help' для справки."
        );
    }

    public static void printError(String message) {
        System.err.println("Ошибка: " + message);
    }

    public static void printUser(UserResponse user) {
        if (user == null) {
            System.out.println("Пользователь не найден");
            return;
        }
        System.out.printf(
            "ID: %d | Имя: %s | Email: %s | Возраст: %d | Создан: %s%n",
            user.getId(), user.getName(), user.getEmail(),
            user.getAge(), user.getCreatedAt()
        );
    }

    public static void printUsers(List<UserResponse> users) {
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст");
            return;
        }
        users.forEach(Printer::printUser);
    }

    public static void printSuccess(String message) {
        System.out.println("[Успех] " + message);
    }

    public static void printExit(String message) {
        System.out.println(message);
    }
}
