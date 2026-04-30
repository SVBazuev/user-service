package edu.example.cli.commands;


import java.util.concurrent.Callable;


import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;


import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.cli.util.Printer;


@Command(name = "delete",
        description = "Удалить пользователя по ID")
public class DeleteCommand implements Callable<Integer> {

    private final UserController userController;

    @Parameters(index = "0", description = "ID пользователя")
    private Long id;

    public DeleteCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Integer call() {
        if (id == null) {
            Printer.printError("Не указан ID пользователя.");
            return 1;
        }
        UserRequest request = new UserRequest();
        request.setId(id);

        // сначала проверим, существует ли пользователь (опционально)
        DTO<UserResponse> check = userController.getUser(DTO.success(request));
        if (!check.isSuccess()) {
            Printer.printError(check.getMessage());
            return 1;
        }

        DTO<Void> response = userController.deleteUser(DTO.success(request));
        if (response.isSuccess()) {
            Printer.printSuccess(response.getMessage());
            return 0;
        } else {
            Printer.printError(response.getMessage());
            return 1;
        }
    }
}
