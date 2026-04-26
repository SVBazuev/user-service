package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;


public class CreateCommand implements Command {
    private final UserController userController;

    public CreateCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(UserRequest request) {
        // TODO Убрать try, валидация на уровне парсинга строки пользователя.
        try {
            UserResponse response = userController.createUser(request);
            Printer.printSuccess("Пользователь создан:");
            Printer.printUser(response);
        // } catch (NumberFormatException e) {
        //     Printer.printError("Возраст должен быть числом");
        } catch (Exception e) {
            Printer.printError(
                "Не удалось создать пользователя: " + e.getMessage()
            );
        }
    }

    @Override
    public String getDescription() {
        return "create <name> <email> [age] – создание нового пользователя";
    }
}
