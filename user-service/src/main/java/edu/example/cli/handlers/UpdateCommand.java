package edu.example.cli.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;


public class UpdateCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(
        UpdateCommand.class
    );
    private final UserController userController;

    public UpdateCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(DTO<UserRequest> dto) {
        if (!dto.isSuccess()) {
            Printer.printError(
                "Использование: update <id> <key=value> [key=value...]");
            Printer.printError("Доступные ключи: name, email, age");
            return;
        }
        DTO<UserResponse> response = userController.updateUser(dto);
        if (response.isSuccess()) {
            Printer.printSuccess("Пользователь обновлён:");
            Printer.printUser(response.getData());
        } else {
            Printer.printError(response.getMessage());
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
