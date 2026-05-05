package edu.example.core.dto;

import jakarta.validation.constraints.*;

public record UserRequest(
    @NotBlank(message = "Имя не может быть пустым")
    String name,

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    String email,

    @Min(value = 0, message = "Возраст должен быть >= 0")
    @Max(value = 150, message = "Возраст должен быть <= 150")
    Integer age
) {}
