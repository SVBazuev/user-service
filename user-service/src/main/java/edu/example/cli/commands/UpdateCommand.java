package edu.example.cli.commands;


import java.util.concurrent.Callable;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.cli.util.Printer;


@Command(name = "update",
        description = "Обновить поля пользователя")
public class UpdateCommand implements Callable<Integer> {

    private final UserController userController;

    @Parameters(index = "0", description = "ID пользователя")
    private Long id;

    @Option(names = "name", description = "Новое имя")
    private String name;

    @Option(names = "email", description = "Новый email")
    private String email;

    @Option(names = "age", description = "Новый возраст")
    private Integer age;

    public UpdateCommand(UserController userController) {
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
        if (name != null) request.setName(name);
        if (email != null) request.setEmail(email);
        if (age != null) request.setAge(age);

        DTO<UserResponse> response = userController.updateUser(DTO.success(request));
        if (response.isSuccess()) {
            Printer.printSuccess("Пользователь обновлён:");
            Printer.printUser(response.getData());
            return 0;
        } else {
            Printer.printError(response.getMessage());
            return 1;
        }
    }
}
