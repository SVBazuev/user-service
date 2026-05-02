package edu.example.cli.commands;


import java.util.concurrent.Callable;


import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;


import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.cli.util.Printer;


@Command(name = "create",
        description = "Создание нового пользователя")
public class CreateCommand implements Callable<Integer> {

    private final UserController userController;

    @Parameters(index = "0", description = "Имя пользователя")
    private String name;

    @Parameters(index = "1", description = "Email пользователя")
    private String email;

    @Parameters(index = "2", arity = "0..1", description = "Возраст (опционально)")
    private Integer age;

    public CreateCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public Integer call() {
        UserRequest request = new UserRequest(name, email, age);
        DTO<UserResponse> response = userController.createUser(DTO.success(request));
        if (response.isSuccess()) {
            Printer.printSuccess("Пользователь создан:");
            Printer.printUser(response.getData());
            return 0;
        } else {
            Printer.printError(
                "Не удалось создать пользователя: "
                + response.getMessage()
            );
            return 1;
        }
    }
}
