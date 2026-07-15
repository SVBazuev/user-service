package edu.example.core.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import edu.example.core.entity.UserRole;

public record UserResponse(
    @Schema(description = "Unique identifier of the user", example = "1")
    Long id,

    @Schema(description = "Full name of the user", example = "John Doe")
    String name,

    @Schema(description = "Email address of the user", example = "john@example.com")
    String email,

    @Schema(description = "Age of the user", example = "30")
    Integer age,

    @Schema(
        description = "List of roles assigned to the user",
        example = "[\"USER\"]"
    )
    List<UserRole> roles,

    @Schema(
        description = "Date and time when the user was created",
        example = "2025-05-30T12:00:00"
    )
    LocalDateTime createdAt
) {}
