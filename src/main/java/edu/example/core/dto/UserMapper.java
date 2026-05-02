package edu.example.core.dto;


import edu.example.core.entity.User;


public class UserMapper {

    public static User toEntity(UserRequest request) {
        if (request == null) return null;
        return new User(
            request.getName(), request.getEmail(), request.getAge()
        );
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreated_at()
        );
    }

    public static void updateEntity(User existing, UserRequest request) {
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getEmail() != null) existing.setEmail(request.getEmail());
        if (request.getAge() != null) existing.setAge(request.getAge());
    }
}
