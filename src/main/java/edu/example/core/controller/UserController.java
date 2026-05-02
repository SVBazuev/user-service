package edu.example.core.controller;


import java.util.List;


import edu.example.core.dto.DTO;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.service.UserService;


public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public DTO<UserResponse> createUser(DTO<UserRequest> dto) {
        return userService.create(dto);
    }

    public DTO<UserResponse> getUser(DTO<UserRequest> dto) {
        return userService.getById(dto);
    }

    public DTO<UserResponse> updateUser(DTO<UserRequest> dto) {
        return userService.update(dto);
    }

    public DTO<List<UserResponse>> getAllUsers() {
        return userService.getAll();
    }

    public DTO<Void> deleteUser(DTO<UserRequest> dto) {
        return userService.delete(dto);
    }
}
