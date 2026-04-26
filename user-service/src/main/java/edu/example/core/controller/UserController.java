package edu.example.core.controller;


import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.service.UserService;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserResponse createUser(UserRequest request) {
        return userService.create(request);
    }

    public UserResponse getUser(Long id) {
        return userService.getById(id);
    }

    public List<UserResponse> getAllUsers() {
        return userService.getAll();
    }

    public UserResponse updateUser(UserRequest request) {
        return userService.update(request);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        return userService.update(id, request);
    }

    public void deleteUser(Long id) {
        userService.delete(id);
    }

    public void deleteUser(UserRequest request) {
        userService.delete(request.getId());
    }
}
