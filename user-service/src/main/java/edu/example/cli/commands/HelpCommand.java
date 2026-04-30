package edu.example.cli.commands;


import java.util.concurrent.Callable;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;


@Command(name = "help",
        description = "Показать справку по командам")
public class HelpCommand implements Callable<Integer> {

    @Spec
    private CommandLine.Model.CommandSpec spec;

    @Parameters(index = "0", arity = "0..1", description = "Название команды (опционально)")
    private String commandName;

    @Override
    public Integer call() {
        CommandLine parent = spec.commandLine().getParent();
        if (parent == null) {
            parent = spec.commandLine();
        }
        if (commandName == null) {
            parent.usage(System.out);
        } else {
            CommandLine sub = parent.getSubcommands().get(commandName);
            if (sub == null) {
                System.err.println("Неизвестная команда: " + commandName);
                return 1;
            }
            sub.usage(System.out);
        }
        return 0;
    }
}
