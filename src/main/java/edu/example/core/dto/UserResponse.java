package edu.example.core.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String name,
    String email,
    Integer age,
    LocalDateTime createdAt
) {}
