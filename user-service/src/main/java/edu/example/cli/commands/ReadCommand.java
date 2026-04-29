package edu.example.cli.commands;


import java.util.List;
import java.util.concurrent.Callable;


import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.cli.picocli.CommandLine.Command;
import edu.example.cli.picocli.CommandLine.Parameters;
import edu.example.cli.util.Printer;


@Command(name = "read",
        description = "Показать всех пользователей или одного по ID")
public class ReadCommand implements Callable<Integer> {

    private final UserController userController;

    @Parameters(
        index = "0",
        arity = "0..1",
        description = "ID пользователя (необязательно)")
    private Long id;

    public ReadCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Integer call() {
        if (id == null) {
            DTO<List<UserResponse>> response = userController.getAllUsers();
            if (response.isSuccess()) {
                Printer.printUsers(response.getData());
                return 0;
            } else {
                Printer.printError(response.getMessage());
                return 1;
            }
        } else {
            UserRequest request = new UserRequest();
            request.setId(id);
            DTO<UserResponse> response = userController.getUser(
                DTO.success(request)
            );
            if (response.isSuccess()) {
                Printer.printUser(response.getData());
                return 0;
            } else {
                Printer.printError(response.getMessage());
                return 1;
            }
        }
    }
}
