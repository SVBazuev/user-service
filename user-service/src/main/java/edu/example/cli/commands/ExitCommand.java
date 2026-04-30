package edu.example.cli.commands;


import java.util.concurrent.Callable;


import picocli.CommandLine.Command;


import edu.example.cli.util.Printer;


@Command(name = "exit",
        description = "Выход из программы")
public class ExitCommand implements Callable<Integer> {

    private final Runnable exitAction;

    public ExitCommand(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    @Override
    public Integer call() {
        Printer.printExit("Выход из программы.");
        exitAction.run();
        return 0;
    }
}
