package edu.example.core.dto;

public class DTO<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final int code;

    private DTO(boolean success, T data, String message, int code) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> DTO<T> success(T data) {
        return new DTO<>(true, data, null, 200);
    }

    public static <T> DTO<T> success(T data, String message) {
        return new DTO<>(true, data, message, 200);
    }

    public static <T> DTO<T> error(String message, int code) {
        return new DTO<>(false, null, message, code);
    }

    public static <T> DTO<T> error(String message) {
        return error(message, 500);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
    public int getCode() { return code; }

    public static void main(String[] args) {
        System.out.println("Компилируется.");
    }
}
