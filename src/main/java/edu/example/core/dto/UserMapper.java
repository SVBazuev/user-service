package edu.example.core.dto;

import edu.example.core.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static User toEntity(UserRequest request) {
        if (request == null) return null;
        return new User(request.name(), request.email(), request.age());
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getAge(),
            user.getCreatedAt()
        );
    }

    public static void updateEntity(User existing, UserRequest request) {
        if (request.name() != null) existing.setName(request.name());
        if (request.email() != null) existing.setEmail(request.email());
        if (request.age() != null) existing.setAge(request.age());
    }
}
