package edu.example.core.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;

public record UserRequest(
    @NotBlank(message = "Имя не может быть пустым", groups = OnCreate.class)
    String name,

    @NotBlank(message = "Email не может быть пустым", groups = OnCreate.class)
    @Email(message = "Некорректный email", groups = OnCreate.class)
    String email,

    @Min(value = 0, message = "Возраст должен быть >= 0",
        groups = {Default.class, OnCreate.class, OnUpdate.class}
    )
    @Max(value = 150, message = "Возраст должен быть <= 150",
        groups = {Default.class, OnCreate.class, OnUpdate.class}
    )
    Integer age,

    @NotBlank(message = "Пароль не может быть пустым", groups = OnCreate.class)
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа", groups = OnCreate.class)
    String password
) {}
