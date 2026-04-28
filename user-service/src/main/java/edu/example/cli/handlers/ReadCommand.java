package edu.example.cli.handlers;


import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;


public class ReadCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(
        ReadCommand.class
    );
    private final UserController userController;

    public ReadCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(DTO<UserRequest> dto) {
        if (!dto.isSuccess()) {
            DTO<List<UserResponse>> response = (
                userController.getAllUsers()
            );
            if (response.isSuccess()) {
                Printer.printUsers(response.getData());
            } else {
                Printer.printError(response.getMessage());
            }
        } else {
            DTO<UserResponse> response = (
                userController.getUser(dto)
            );
            if (response.isSuccess()) {
                Printer.printUser(response.getData());
            } else {
                Printer.printError(response.getMessage());
            }
        }
    }

    @Override
    public String getDescription() {
        return "read [<id>] – показать всех пользователей или одного по ID";
    }
}
