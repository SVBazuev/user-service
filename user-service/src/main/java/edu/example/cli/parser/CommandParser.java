package edu.example.cli.parser;


import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import edu.example.core.dto.DTO;
import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;


public class CommandParser {
    private final Set<String> commandNames;

    public CommandParser(Set<String> commandNames) {
        this.commandNames = commandNames;
    }

    public ParsedCommand parse(String input)
    throws
    IllegalArgumentException {

        String[] tokens = splitCommandLine(input);
        int len = tokens.length;
        String commandName = tokens[0].toLowerCase();
        if (!commandNames.contains(commandName)) {
            throw new IllegalArgumentException(
                String.format("Неизвестная команда: %s.", commandName)
            );
        } else if (len == 0) {
            throw new IllegalArgumentException(
                "Получен пустой ввод."
            );
        } else if (len > 5) {
            throw new IllegalArgumentException(
                "Получено больше 5 аргументов."
            );
        }
        String[] args = Arrays.copyOfRange(tokens, 1, len);
        DTO<UserRequest> dto = null;
        UserRequest request = new UserRequest();

        if (--len == 0
                && Set.of("exit", "help", "read").contains(commandName)) {
            dto = DTO.error("Continue", 100);
        } else if (len == 0) {
            throw new IllegalArgumentException(
                "Для команд: \"create\", \"update\" и \"delete\" "
                + "- параметры обязательны."
            );
        } else if (len == 1 && !args[0].contains("=")
                && Set.of("help", "read", "delete").contains(commandName)) {
            dto = parsePositional(commandName, args[0], request);
        } else {
            boolean positionalAllowed = true;
            for (int i = 0; i < len; i++) {
                String arg = args[i];
                if (!positionalAllowed && !arg.contains("=")) {
                    throw new IllegalArgumentException(
                        "Позиционный аргумент после именованного."
                    );
                } else if (arg.contains("=")) {
                    positionalAllowed = false;
                    parseKeyValue(arg, request);
                } else {
                    throw new IllegalArgumentException(
                        "Одновременное использование позиционных и "
                        + "именованных аргументов не реализовано."
                    );
                    // parsePositional(commandName, arg, request, i, len);
                }
            }
            dto = DTO.success(request);
        }

        return new ParsedCommand(commandName, dto);
    }

    private String[] splitCommandLine(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
            .map(String::trim)
            .map(s -> s.replaceAll("^[\"\']+|[\"\']+$", ""))
            .map(String::trim)
            .toArray(String[]::new);
    }

    private void parseKeyValue(String arg, UserRequest req)
    throws
    IllegalArgumentException {
        String[] kv = arg.split("=", 2);
        if (kv.length == 2) {
            switch (kv[0].trim()) {
                case "id":
                    Long id = parseNumbers(
                        kv[1].trim(), Long::parseLong,
                        "id", Long.class
                    );
                    req.setId(id);
                    break;
                case "name":
                    req.setName(kv[1].trim());
                    break;
                case "email":
                    var email = kv[1].trim();
                    if (email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
                        req.setEmail(email);
                    } else {
                        throw new IllegalArgumentException(
                            "Введён некорректный email: " + email
                        );
                    }
                    break;
                case "age":
                    Integer age = parseNumbers(
                        kv[1].trim(), Integer::parseInt,
                        "age", Integer.class
                    );
                    req.setAge(age);
                    break;
                default:
                    throw new IllegalArgumentException(
                        "Передан неизвестный ключ: " + kv[0].trim()
                    );
            }
        } else {
            throw new IllegalArgumentException(
                "Введён некорректный аргумент: "
                + arg
            );
        }
    }

    private DTO<UserRequest> parsePositional(
    String commandName, String arg, UserRequest req, int i)
    throws
    IllegalArgumentException {
        boolean isNumeric = arg != null && arg.matches("^-?\\d+$");
        if (isNumeric
                && Set.of("delete", "read").contains(commandName)) {
            Long id = parseNumbers(
                arg.trim(), Long::parseLong,
                "id", Long.class
            );
            req.setId(id);
        } else if (!isNumeric && commandName.equals("help")){
            // pass
        } else {
            //TODO Реализовать обработку нескольких аргументов.
            throw new IllegalArgumentException(
                "Обработка несколький позиционных аргументов не реализована."
            );
        }

        if (req.isEmpty()) {
            return DTO.<UserRequest>error(arg, 100);
        }
        return DTO.success(req);
    }

    private DTO<UserRequest> parsePositional(
    String commandName, String arg, UserRequest req)
    throws
    IllegalArgumentException {
        return parsePositional(commandName, arg, req, -1);
    }

    private <T extends Number> T parseNumbers(
    String arg, Function<String, T> parser, String keyName, Class<T> type)
    throws
    IllegalArgumentException {

        T numT = null;
        String template = (
            "Значение ключа %s должно принадлежать "
            + "положительному диапазону типа %s."
        );

        IllegalArgumentException exception = new IllegalArgumentException(
            String.format(template, keyName, type)
        );
        if ((keyName.equals("age") && arg.matches("^[^-0]\\d*$|0"))
                || arg.matches("^[^-0]\\d*$")) {
            try {
                numT = parser.apply(arg.trim());
                return numT;
            } catch (NumberFormatException e) {
                exception.setStackTrace(e.getStackTrace());
                throw exception;
            }
        } else throw exception;
    }

/*
    private UserRequest parseArguments(String[] args)
    throws
    IllegalArgumentException {

        UserRequest userRequest = new UserRequest();

        boolean positionalAllowed = true;
        for (String arg : args) {
            if (!positionalAllowed && !arg.contains("=")) {
                throw new IllegalArgumentException(
                    "Позиционный аргумент после именованного."
                );
            } else if (arg.contains("=")) {
                positionalAllowed = false;
                String[] kv = arg.split("=", 2);
                if (kv.length == 2) {
                    switch (kv[0].trim()) {
                        case "id":
                            Long id = parseNumbers(
                                kv[1].trim(), Long::parseLong,
                                "id", Long.class
                            );
                            userRequest.setId(id);
                            break;
                        case "name":
                            userRequest.setName(kv[1].trim());
                            break;
                        case "email":
                            var email = kv[1].trim();
                            if (email.contains("@")) {
                                userRequest.setEmail(email);
                            } else {
                                throw new IllegalArgumentException(
                                    "Введён некорректный email: " + email
                                );
                            }
                            break;
                        case "age":
                            Integer age = parseNumbers(
                                kv[1].trim(), Integer::parseInt,
                                "age", Integer.class
                            );
                            userRequest.setAge(age);
                            break;
                        default:
                            throw new IllegalArgumentException(
                                "Передан неизвестный ключ: " + kv[0].trim()
                            );
                    }
                } else {
                    throw new IllegalArgumentException(
                        "Введён некорректный аргумент: "
                        + arg
                    );
                }
            } else if (positionalAllowed && args.length == 1) {
                if (arg.matches("^-?\\d+$")) {
                    Long id = parseNumbers(
                        arg.trim(), Long::parseLong,
                        "id", Long.class
                    );
                    userRequest.setId(id);
                } else {
                    userRequest.setName(arg);
                }
            } else {
                throw new IllegalArgumentException(
                    "Введён некорректный аргумент: "
                    + arg
                );
            }
        }
        return userRequest;
    }
*/
}
