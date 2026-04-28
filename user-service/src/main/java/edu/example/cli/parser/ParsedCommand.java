package edu.example.cli.parser;


import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;


public record ParsedCommand(String name, DTO<UserRequest> dto) {
    public boolean isEmpty() {
        return (this.name == null || this.name.equals(""));
    }
}
