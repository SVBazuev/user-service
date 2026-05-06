package edu.example.core.exception;


public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }
}
