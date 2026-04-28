package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;


public class ExitCommand implements Command {
    private final Runnable exitAction;

    public ExitCommand(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    @Override
    public void execute(DTO<UserRequest> request) {
        System.out.println("Выход из программы.");
        exitAction.run();
    }

    @Override
    public String getDescription() {
        return "exit - выход из программы";
    }
}
