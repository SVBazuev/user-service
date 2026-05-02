package edu.example.exception;


public class DataAccessException extends UserServiceException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
