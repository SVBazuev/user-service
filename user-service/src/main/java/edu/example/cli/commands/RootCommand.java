package edu.example.cli.commands;


import edu.example.cli.picocli.CommandLine;
import edu.example.cli.picocli.CommandLine.Command;


@Command(
    name = "",
    description = "UserService CLI – управление пользователями"
)
public class RootCommand implements Runnable {
    @Override
    public void run() {
        // Если команда не указана – показываем справку
        new CommandLine(this).usage(System.out);
    }
}
