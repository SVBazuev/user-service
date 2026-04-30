package edu.example.cli.commands;


import picocli.CommandLine;
import picocli.CommandLine.Command;


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
