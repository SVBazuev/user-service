package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;
import edu.example.core.dto.UserRequest;


public class DeleteCommand implements Command {
    private final UserController userController;

    public DeleteCommand(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void execute(UserRequest request) {
        // TODO Убрать try, валидация на уровне парсинга строки пользователя.
        try {
            userController.deleteUser(request);
            Printer.printSuccess(
                "Пользователь с ID=" + request.getId() + " удалён"
            );
        } catch (NumberFormatException e) {
            Printer.printError("ID должен быть числом");
        } catch (Exception e) {
            Printer.printError("Ошибка удаления: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "delete <id> – удалить пользователя по ID";
    }
}
