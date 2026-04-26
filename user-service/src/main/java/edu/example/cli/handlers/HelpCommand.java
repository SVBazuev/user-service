package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.api.CommandRegistry;
import edu.example.cli.util.Printer;
import edu.example.core.dto.UserRequest;


public class HelpCommand implements Command {
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry){
        this.registry = registry;
    }

    @Override
    public void execute(UserRequest request) {
        if (request.isEmpty()) {
            System.out.println("\nДоступные команды:");

            registry.getCommandNames().stream()
                .sorted()
                .forEach(name -> {
                    Command cmd = registry.getCommand(name);
                    System.out.printf(
                        "  %-10s - %s%n", name, cmd.getDescription()
                    );
                });

            System.out.printf(
                "\nДля получения справки %s%s",
                "по конкретной команде используйте: ",
                "help <команда>\n"
            );
        } else {
            try {
                System.out.println(
                    registry.getCommand(request.getName()).getDescription()
                );
            } catch (NullPointerException e) {
                Printer.printError(
                    String.format(
                        "Передан некорректный аргумент: %s",
                        request.getName()
                    )
                );
                execute(new UserRequest());
            }
        }
    }

    @Override
    public String getDescription() {
        return "help [<команда>]- показать эту справку [описание команды]";
    }
}
