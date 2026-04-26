package edu.example.cli.parser;


import edu.example.core.dto.UserRequest;


public record ParsedCommand(String name, UserRequest request) {
    public boolean isEmpty() {
        return (
            (this.name == null || this.name.equals(""))
            && request.isEmpty()
        );
    }
}
