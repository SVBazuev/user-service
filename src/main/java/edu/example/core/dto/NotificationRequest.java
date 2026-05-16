package edu.example.core.dto;

public record NotificationRequest(
    NotificationOperation operation,
    String email
) {}
