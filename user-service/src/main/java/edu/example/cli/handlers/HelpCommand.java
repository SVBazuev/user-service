package edu.example.cli.handlers;


import edu.example.cli.api.Command;
import edu.example.cli.api.CommandRegistry;
import edu.example.cli.util.Printer;
import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;


public class HelpCommand implements Command {
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry){
        this.registry = registry;
    }

    @Override
    public void execute(DTO<UserRequest> dto) {
        if (!dto.isSuccess()) {
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
                    registry.getCommand(dto.getData().getName()).getDescription()
                );
            } catch (NullPointerException e) {
                Printer.printError(
                    String.format(
                        "Передан некорректный аргумент: %s",
                        dto.getData().getName()
                    )
                );
                execute(DTO.<UserRequest>error("Continue", 100));
            }
        }
    }

    @Override
    public String getDescription() {
        return "help [<команда>]- показать эту справку [описание команды]";
    }
}
