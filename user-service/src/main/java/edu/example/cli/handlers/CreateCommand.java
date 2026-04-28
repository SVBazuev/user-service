package edu.example.cli.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;


public class CreateCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(
        CreateCommand.class
    );
    private final UserController userController;

    public CreateCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(DTO<UserRequest> dto) {
        DTO<UserResponse> response = userController.createUser(dto);
        if (response.isSuccess()) {
            Printer.printSuccess("Пользователь создан:");
            Printer.printUser(response.getData());
        } else {
            Printer.printError(
                "Не удалось создать пользователя: "
                + response.getMessage()
            );
        }
    }

    @Override
    public String getDescription() {
        return "create <name> <email> [age] – создание нового пользователя";
    }
}
