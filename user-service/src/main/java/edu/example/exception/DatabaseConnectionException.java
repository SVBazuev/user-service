package edu.example.exception;


public class DatabaseConnectionException extends UserServiceException {
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
