package edu.example.core.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.entity.UserRole;
import edu.example.core.event.UserDeletedEvent;
import edu.example.core.exception.UserNotFoundException;
import edu.example.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private UserRequest validRequest;
    private User userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequest(
            "Valid", "valid@example.ya", 30, "encodedPassword"
        );
        userEntity = new User(
            "Valid", "valid@example.ya", 30, "encodedPassword"
        );
        userEntity.setId(1L);
        userResponse = new UserResponse(
            1L, "Valid", "valid@example.ya", 30,
            List.of(UserRole.USER), null
        );
        lenient().doNothing().when(eventPublisher).publishEvent(any());
        lenient().when(passwordEncoder.encode(anyString()))
            .thenReturn("encodedPassword");
    }

    @Nested
    @DisplayName("create() tests")
    class CreateTests {

        @Test
        @DisplayName("should create user successfully")
        void create_Success() {
            when(userMapper.toEntity(validRequest)).thenReturn(userEntity);
            when(passwordEncoder.encode(validRequest.password()))
                .thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(userEntity);
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            UserResponse response = userService.create(validRequest);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Valid");
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapper).toEntity(validRequest);
            verify(userMapper).toResponse(userEntity);
        }
    }

    @Nested
    @DisplayName("getById() tests")
    class GetByIdTests {

        @Test
        @DisplayName("should return user when found")
        void getById_Found() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);

            UserResponse response = userService.getById(1L);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("Valid");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when not found")
        void getById_NotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.getById(99L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAll() tests")
    class GetAllTests {

        @Test
        @DisplayName("should return list of users")
        void getAll_Success() {
            User user2 = new User(
                "Second", "second@example.ya",
                25, "encodedPassword"
            );
            user2.setId(2L);
            user2.setPassword("encodedPassword");
            user2.setRoles(List.of(UserRole.USER));
            UserResponse response2 = new UserResponse(
                2L, "Second", "second@example.ya", 25,
                List.of(UserRole.USER), null
            );

            when(userRepository.findAll()).thenReturn(List.of(userEntity, user2));
            when(userMapper.toResponse(userEntity)).thenReturn(userResponse);
            when(userMapper.toResponse(user2)).thenReturn(response2);

            List<UserResponse> responses = userService.getAll();

            assertThat(responses).hasSize(2);
            assertThat(responses).extracting(UserResponse::name)
                    .containsExactlyInAnyOrder("Valid", "Second");
        }

        @Test
        @DisplayName("should return empty list when no users")
        void getAll_EmptyList() {
            when(userRepository.findAll()).thenReturn(List.of());
            List<UserResponse> responses = userService.getAll();
            assertThat(responses).isEmpty();
            verify(userMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("update() tests")
    class UpdateTests {

        @Test
        @DisplayName("should update user successfully")
        void update_Success() {
            UserRequest updateRequest = new UserRequest(
                "New", null, 35, null
            );
            User updatedEntity = new User(
                "New", "valid@example.ya", 35, null);
            updatedEntity.setId(1L);
            UserResponse updatedResponse = new UserResponse(
                1L, "New", "valid@example.ya", 35,
                null, null
            );

            when(userRepository.findById(1L))
                .thenReturn(Optional.of(userEntity));
            doNothing().when(userMapper).updateEntity(userEntity, updateRequest);
            when(userRepository.save(userEntity)).thenReturn(updatedEntity);
            when(userMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

            UserResponse response = userService.update(1L, updateRequest);

            assertThat(response.name()).isEqualTo("New");
            assertThat(response.age()).isEqualTo(35);
            verify(userMapper).updateEntity(userEntity, updateRequest);
            verify(userRepository).save(userEntity);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when updating non-existent user")
        void update_UserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.update(99L, validRequest))
                    .isInstanceOf(UserNotFoundException.class);
            verify(userMapper, never()).updateEntity(any(), any());
        }
    }

    @Nested
    @DisplayName("delete() tests")
    class DeleteTests {

        @Test
        @DisplayName("should delete user successfully")
        void delete_Success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
            userService.delete(1L);
            verify(userRepository).deleteById(1L);
            verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
        }

        @Test
        @DisplayName("should throw UserNotFoundException when deleting non-existent user")
        void delete_UserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class);
            verify(userRepository, never()).deleteById(any());
        }
    }
}
