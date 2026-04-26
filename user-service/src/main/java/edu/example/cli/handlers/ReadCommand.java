package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;

import java.util.List;

public class ReadCommand implements Command {
    private final UserController userController;

    public ReadCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(UserRequest request) {
        if (request.getId() == null) {
            // показать всех
            try {
                List<UserResponse> users = userController.getAllUsers();
                Printer.printUsers(users);
            } catch (Exception e) {
                Printer.printError(
                    "Ошибка получения списка: " + e.getMessage()
                );
            }
        } else {
            // показать по ID
            try {
                UserResponse user = userController.getUser(request.getId());
                Printer.printUser(user);
            // } catch (NumberFormatException e) {
            //     Printer.printError("ID должен быть числом");
            } catch (Exception e) {
                Printer.printError(e.getMessage());
            }
        }
    }

    @Override
    public String getDescription() {
        return "read [<id>] – показать всех пользователей или одного по ID";
    }
}
