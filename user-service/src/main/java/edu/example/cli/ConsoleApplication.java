package edu.example.cli;


import java.util.Scanner;


import edu.example.cli.commands.*;
import picocli.CommandLine;
import edu.example.cli.util.Printer;
import edu.example.core.controller.UserController;


public class ConsoleApplication {
    private final UserController userController;
    private final CommandLine command;
    private boolean running = true;

    public ConsoleApplication(UserController userController) {
        this.userController = userController;
        this.command = new CommandLine(new RootCommand());
        initializeCommands(this.userController);
    }

    public void start() {
        Printer.printWelcome();
        command.usage(System.out);

        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                // Разбиваем входную строку на аргументы (простейший вариант)
                String[] args = input.split("\\s+");
                try {
                    command.execute(args);
                } catch (CommandLine.ParameterException ex) {
                    Printer.printError(ex.getMessage());
                    // Справка по той команде, которая вызвала ошибку
                    ex.getCommandLine().usage(System.err);
                } catch (Exception ex) {
                    Printer.printError("Ошибка выполнения: " + ex.getMessage());
//                    ex.printStackTrace();
                }
            }
        }
    }

    private void initializeCommands(UserController userController) {
        this.command.addSubcommand(
            "create", new CreateCommand(userController));
        this.command.addSubcommand(
            "read",   new ReadCommand(userController));
        this.command.addSubcommand(
            "update", new UpdateCommand(userController));
        this.command.addSubcommand(
            "delete", new DeleteCommand(userController));
        this.command.addSubcommand(
            "exit",   new ExitCommand(() -> running = false));
        this.command.addSubcommand(
            "help",   new HelpCommand());
    }
}
