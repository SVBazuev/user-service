package edu.example.core.dto;

import java.time.LocalDateTime;
import java.util.List;

import edu.example.core.entity.UserRole;

public record UserResponse(
    Long id,
    String name,
    String email,
    Integer age,
    String password,
    List<UserRole> roles,
    LocalDateTime createdAt
) {}
