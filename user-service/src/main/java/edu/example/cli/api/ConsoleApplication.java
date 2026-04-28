package edu.example.cli.api;


import edu.example.cli.handlers.*;
import edu.example.cli.parser.CommandParser;
import edu.example.cli.parser.ParsedCommand;
import edu.example.cli.util.Printer;

import edu.example.core.controller.UserController;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
// import edu.example.infrastructure.repository.HibernateUserRepository;
import edu.example.infrastructure.util.HibernateUtil;


public class ConsoleApplication {
    private final CommandRegistry registry;
    private final UserController userController;
    private final CommandParser parser;
    private boolean running = true;

    public ConsoleApplication(UserController userController) {
        this.userController = userController;
        this.registry = new CommandRegistry();
        initializeCommands(this.userController);
        this.parser = new CommandParser(registry.getCommandNames());
    }

    public void start() {
        Printer.printWelcome();
        printHelp();

        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            while (running) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                ParsedCommand parsed = new ParsedCommand(
                    "", DTO.<UserRequest>error("Continue", 100)
                );
                try {
                    parsed = parser.parse(input);
                } catch (IllegalArgumentException e) {
                    Printer.printError(e.getMessage());
                }

                Command command = registry.getCommand(parsed.name());
                try {
                    command.execute(parsed.dto());
                } catch (Exception e) {
                    Printer.printError(
                        "Ошибка выполнения команды: "
                        + e.getMessage()
                    );
                    e.printStackTrace();
                }
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private void initializeCommands(UserController userController) {
        registry.register("help", new HelpCommand(registry));
        registry.register("create", new CreateCommand(userController));
        registry.register("read", new ReadCommand(userController));
        registry.register("update", new UpdateCommand(userController));
        registry.register("delete", new DeleteCommand(userController));
        registry.register("exit", new ExitCommand(() -> running = false));
    }

    private void printHelp() {
        Command help = registry.getCommand("help");
        if (help != null) {
            help.execute(DTO.error("Continue", 100));
        }
    }
}
