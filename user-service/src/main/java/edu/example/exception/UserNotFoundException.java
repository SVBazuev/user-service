package edu.example.exception;


public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден");
    }
}
