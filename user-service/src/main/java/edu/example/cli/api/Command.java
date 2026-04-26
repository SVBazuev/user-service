package edu.example.cli.api;


import edu.example.core.dto.UserRequest;


public interface Command {
    /**
     * Выполняет запрос, и отдаёт ответ в Printer.
     * @param request UserRequest
     */
    void execute(UserRequest request);

    /**
     * @return описание команды для справки
     */
    String getDescription();
}
