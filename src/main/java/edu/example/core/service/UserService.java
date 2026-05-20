package edu.example.core.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.entity.UserRole;
import edu.example.core.event.UserCreatedEvent;
import edu.example.core.event.UserDeletedEvent;
import edu.example.core.exception.UserNotFoundException;
import edu.example.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponse create(UserRequest request) {
        log.info("Creating user with name: {}", request.name());
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(List.of(UserRole.USER));
        
        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        eventPublisher.publishEvent(new UserCreatedEvent(saved.getEmail()));
        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found with id: {}", id);
                return new UserNotFoundException(id);
            }
        );
        log.debug("Found user: {}", user);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        log.debug("Fetching all users");
        List<UserResponse> responses = userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .collect(Collectors.toList());
        log.debug("Found {} users", responses.size());
        return responses;
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);
        User existing = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found for update, id: {}", id);
                return new UserNotFoundException(id);
                }
            );
        userMapper.updateEntity(existing, request);
        User updated = userRepository.save(existing);
        log.info("User updated: {}", updated);
        return userMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("User not found for delete, id: {}", id);
                return new UserNotFoundException(id);
            });
        String email = user.getEmail();
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
        eventPublisher.publishEvent(new UserDeletedEvent(email));
    }
}
