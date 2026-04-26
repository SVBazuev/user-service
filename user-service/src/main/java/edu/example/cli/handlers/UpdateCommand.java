package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;


public class UpdateCommand implements Command {
    private final UserController userController;

    public UpdateCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(UserRequest request) {
        if (request.isEmpty()) {
            Printer.printError(
                "Использование: update <id> <key=value> [key=value...]");
            Printer.printError("Доступные ключи: name, email, age");
            return;
        }
        try {
            UserResponse updated = userController.updateUser(request);
            Printer.printSuccess("Пользователь обновлён:");
            Printer.printUser(updated);
        } catch (NumberFormatException e) {
            Printer.printError("ID или возраст должны быть числом");
        } catch (Exception e) {
            Printer.printError("Ошибка обновления: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return (
            "update <id> <key=value> [key=value...] - "
            +"обновить поле(я) объекта с указаным id, "
            +"\n               доступные ключи: <name> <email> <age>"
        );
    }

}
