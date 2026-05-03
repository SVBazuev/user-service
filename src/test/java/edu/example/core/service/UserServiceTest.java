package edu.example.core.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import edu.example.core.dto.DTO;
import edu.example.core.dto.UserMapper;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import edu.example.exception.UserNotFoundException;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Texts")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private UserService userService;

    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = validCreateRequest();
        lenient().when(sessionFactory.getCurrentSession())
            .thenReturn(session);
        lenient().when(session.beginTransaction())
            .thenReturn(transaction);
        lenient().when(transaction.isActive())
            .thenReturn(true);
    }

    @Nested
    @DisplayName("create() tests")
    class CreateTests {

        @Test
        @DisplayName("successful creation")
        void success() throws Exception {
            when(userRepository.save(any(User.class)))
                .thenAnswer(
                    inv -> {
                        User u = inv.getArgument(0);
                        setIdViaReflection(u, 1L);
                        setCreatedAtViaReflection(u, LocalDateTime.now());
                        return u;
                    }
                );
            DTO<UserResponse> result = userService.create(
                DTO.success(validRequest)
            );
            assertThat(result.isSuccess())
                .isTrue();
            assertThat(result.getData().getId())
                .isEqualTo(1L);
        }

        @ParameterizedTest(name = "invalid: name={0}, email={1}, age={2}")
        @CsvSource({
            ", test@test.ya, 25",
            "'', test@test.ya, 25",
            "Name, , 25",
            "Name, bad-email, 25",
            "Name, test@test.ya, -1",
            "Name, test@test.ya, 200"
        })

        void invalidInputs_ReturnError(String name, String email, Integer age) {
            UserRequest request = new UserRequest(name, email, age);
            DTO<UserResponse> result = userService.create(
                DTO.success(request)
            );
            assertThat(result.isSuccess()).isFalse();
            assertThat(result.getCode()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("getById() tests")
    class GetByIdTests {

        @Test
        @DisplayName("should return user when found")
        void getById_Found() throws Exception {
            Long userId = 1L;

            User user = UserMapper.toEntity(validRequest);
            setIdViaReflection(user, userId);
            setCreatedAtViaReflection(user, LocalDateTime.now());

            when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

            validRequest.setId(userId);
            DTO<UserResponse> result = userService.getById(
                DTO.success((validRequest))
            );

            assertThat(result.isSuccess())
                .isTrue();
            assertThat(result.getData().getId())
                .isEqualTo(userId);
            assertThat(result.getData().getName())
                .isEqualTo("Name");
            verify(transaction).commit();
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void getById_NotFound() {
            Long userId = 99L;
            validRequest.setId(userId);
            when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

            DTO<UserResponse> result = userService.getById(
                DTO.success(validRequest)
            );

            assertThat(result.isSuccess())
                .isFalse();
            assertThat(result.getCode())
                .isEqualTo(404);
            assertThat(result.getMessage())
                .contains("не найден");
            verify(transaction).rollback();
        }
    }

    @Nested
    @DisplayName("getAll() tests")
    class GetAllTests {

        @Test
        @DisplayName("should return list of users")
        void getAll_Success() throws Exception {
            User user1 = UserMapper.toEntity(validRequest);
            setIdViaReflection(user1, 1L);
            setCreatedAtViaReflection(user1, LocalDateTime.now());

            User user2 = new User(
                "Some", "some@test.com", 25
            );
            setIdViaReflection(user2, 2L);
            setCreatedAtViaReflection(user2, LocalDateTime.now());

            when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

            DTO<List<UserResponse>> result = userService.getAll();

            assertThat(result.isSuccess())
                .isTrue();
            assertThat(result.getData())
                .hasSize(2);
            assertThat(result.getData())
                .extracting(UserResponse::getName)
                .containsExactlyInAnyOrder("Some", "Name");
            verify(transaction).commit();
        }

        @Test
        @DisplayName("should return empty list when no users")
        void getAll_EmptyList() {
            when(userRepository.findAll())
                .thenReturn(List.of());

            DTO<List<UserResponse>> result = userService.getAll();

            assertThat(result.isSuccess())
                .isTrue();
            assertThat(result.getData())
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("update() tests")
    class UpdateTests {

        @Test
        @DisplayName("should update user successfully")
        void update_Success() throws Exception {
            Long userId = 1L;
            UserRequest updateRequest = new UserRequest();
            updateRequest.setId(userId);
            updateRequest.setName("Updated Name");
            updateRequest.setAge(35);

            User existingUser = new User(
                "Old Name", "old@test.com", 20
            );
            setIdViaReflection(existingUser, userId);
            setCreatedAtViaReflection(existingUser, LocalDateTime.now());

            when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

            DTO<UserResponse> result = userService.update(
                DTO.success(updateRequest)
            );

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getData().getName()).isEqualTo("Updated Name");
            assertThat(result.getData().getAge()).isEqualTo(35);
            verify(userRepository).update(existingUser);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("should return 404 when updating non-existent user")
        void update_UserNotFound() {
            Long userId = 99L;
            UserRequest updateRequest = new UserRequest();
            updateRequest.setId(userId);
            updateRequest.setName("Updated Name");

            when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

            DTO<UserResponse> result = userService.update(
                DTO.success(updateRequest)
            );

            assertThat(result.isSuccess())
                .isFalse();
            assertThat(result.getCode())
                .isEqualTo(404);
            assertThat(result.getMessage())
                .contains("не найден");
            verify(userRepository, never())
                .update(any());
            verify(transaction).rollback();
        }

        @Test
        @DisplayName("should validate email during update")
        void update_InvalidEmail() {
            Long userId = 1L;
            UserRequest updateRequest = new UserRequest();
            updateRequest.setId(userId);
            updateRequest.setEmail("invalid");

            DTO<UserResponse> result = userService.update(
                DTO.success(updateRequest)
            );

            assertThat(result.isSuccess())
                .isFalse();
            assertThat(result.getCode())
                .isEqualTo(400);
            assertThat(result.getMessage())
                .contains("Некорректный email");
        }
    }

    @Nested
    @DisplayName("delete() tests")
    class DeleteTests {

        @Test
        @DisplayName("should delete user successfully")
        void delete_Success() {
            Long userId = 1L;
            validRequest.setId(userId);

            DTO<Void> result = userService.delete(
                DTO.success(validRequest)
            );

            assertThat(result.isSuccess())
                .isTrue();
            assertThat(result.getMessage())
                .isEqualTo("Пользователь удалён");
            verify(userRepository)
                .deleteById(userId);
            verify(transaction).commit();
        }

        @Test
        @DisplayName("should return 404 when deleting non-existent user")
        void delete_UserNotFound() {
            Long userId = 99L;
            validRequest.setId(userId);

            doThrow(new UserNotFoundException(userId))
                .when(userRepository).deleteById(userId);

            DTO<Void> result = userService.delete(
                DTO.success(validRequest)
            );

            assertThat(result.isSuccess())
                .isFalse();
            assertThat(result.getCode())
                .isEqualTo(404);
            assertThat(result.getMessage())
                .contains("не найден");
            verify(transaction).rollback();
        }
    }

    private UserRequest validCreateRequest() {
        return new UserRequest("Name", "test@test.ya", 30);
    }

    private void setIdViaReflection(
    User user, Long id)
    throws
    Exception {
        Field field = User.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, id);
    }

    private void setCreatedAtViaReflection(
    User user, LocalDateTime createdAt)
    throws
    Exception {
        Field field = User.class.getDeclaredField("created_at");
        field.setAccessible(true);
        field.set(user, createdAt);
    }
}
