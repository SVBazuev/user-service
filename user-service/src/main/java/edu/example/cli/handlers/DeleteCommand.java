package edu.example.cli.handlers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;


public class DeleteCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(
        DeleteCommand.class
    );
    private final UserController userController;

    public DeleteCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(DTO<UserRequest> dto) {
        if (dto.getData().getId() == null) {
            Printer.printError(
                "Для удаления необходимо указать id. "
                + "Пример: delete id=5"
            );
            return;
        }
        DTO<Void> response = userController.deleteUser(dto);
        if (response.isSuccess()) {
            Printer.printSuccess(response.getMessage());
        } else {
            Printer.printError(response.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "delete <id>|id=Long – удалить пользователя по ID";
    }
}
