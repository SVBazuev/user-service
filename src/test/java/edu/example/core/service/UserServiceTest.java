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

import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.exception.UserNotFoundException;
import edu.example.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequest(
            "Valid", "valid@example.ya", 30
        );
    }

    @Nested
    @DisplayName("create() tests")
    class CreateTests {

        @Test
        @DisplayName("should create user successfully")
        void create_Success() {
            User savedUser = UserMapper.toEntity(validRequest);
            savedUser.setId(1L);
            when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

            UserResponse response = userService.create(validRequest);

            assertThat(response.id())
                .isEqualTo(1L);
            assertThat(response.name())
                .isEqualTo("Valid");
            verify(userRepository, times(1))
                .save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getById() tests")
    class GetByIdTests {

        @Test
        @DisplayName("should return user when found")
        void getById_Found() {
            User user = new User(
                "First", "first@example.ya", 30
            );
            user.setId(1L);
            when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

            UserResponse response = userService.getById(1L);

            assertThat(response.id())
                .isEqualTo(1L);
            assertThat(response.name())
                .isEqualTo("First");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when not found")
        void getById_NotFound() {
            when(userRepository.findById(99L))
                .thenReturn(Optional.empty());
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
            User user1 = new User(
                "First", "first@example.ya", 30
            );
            user1.setId(1L);

            User user2 = new User(
                "Second", "second@example.ya", 25);
            user2.setId(2L);

            when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

            List<UserResponse> responses = userService.getAll();

            assertThat(responses).hasSize(2);
            assertThat(responses)
                .extracting(UserResponse::name)
                .containsExactlyInAnyOrder("First", "Second");
        }

        @Test
        @DisplayName("should return empty list when no users")
        void getAll_EmptyList() {
            when(userRepository.findAll())
                .thenReturn(List.of());
            List<UserResponse> responses = userService.getAll();
            assertThat(responses)
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("update() tests")
    class UpdateTests {

        @Test
        @DisplayName("should update user successfully")
        void update_Success() {
            User existingUser = new User(
                "Old", "old@example.ya", 20
            );
            existingUser.setId(1L);
            when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

            User updatedUser = new User(
                "New", "old@example.ya", 35
            );
            updatedUser.setId(1L);
            when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

            UserRequest updateRequest = new UserRequest(
                "New", null, 35
            );
            UserResponse response = userService.update(1L, updateRequest);

            assertThat(response.name())
                .isEqualTo("New");
            assertThat(response.age())
                .isEqualTo(35);
            verify(userRepository)
                .save(existingUser);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when updating non-existent user")
        void update_UserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.update(99L, validRequest))
                .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete() tests")
    class DeleteTests {

        @Test
        @DisplayName("should delete user successfully")
        void delete_Success() {
            when(userRepository.existsById(1L))
                .thenReturn(true);

            userService.delete(1L);
            verify(userRepository)
                .deleteById(1L);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when deleting non-existent user")
        void delete_UserNotFound() {
            when(userRepository.existsById(99L))
                .thenReturn(false);
            assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(UserNotFoundException.class);
            verify(userRepository, never())
                .deleteById(any());
        }
    }
}
