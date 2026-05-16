package edu.example.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import edu.example.core.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = (
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save should persist user and generate id")
    void save_ShouldGenerateId() {
        User user = new User("First", "first@test.ya", 30);
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName())
            .isEqualTo("First");
        assertThat(found.get().getEmail())
            .isEqualTo("first@test.ya");
        assertThat(found.get().getAge())
            .isEqualTo(30);
    }

    @Test
    @DisplayName("findById should return user when exists")
    void findById_Existing_ReturnsUser() {
        User user = new User("Second", "second@test.ya", 28);
        userRepository.save(user);
        Long id = user.getId();

        Optional<User> found = userRepository.findById(id);

        assertThat(found)
            .isPresent();
        assertThat(found.get().getName())
            .isEqualTo("Second");
    }

    @Test
    @DisplayName("findById should return empty when user not found")
    void findById_NonExisting_ReturnsEmpty() {
        Optional<User> found = userRepository.findById(99L);
        assertThat(found)
            .isEmpty();
    }

    @Test
    @DisplayName("findAll should return all users")
    void findAll_ShouldReturnList() {
        userRepository.save(
            new User("First", "first@test.ya", 20)
        );
        userRepository.save(
            new User("Second", "second@test.ya", 30)
        );

        List<User> users = userRepository.findAll();

        assertThat(users)
            .hasSize(2);
        assertThat(users)
            .extracting(User::getName)
            .containsExactlyInAnyOrder("First", "Second");
    }

    @Test
    @DisplayName("findAll should return empty list when no users")
    void findAll_EmptyList() {
        userRepository.deleteAll();
        List<User> users = userRepository.findAll();
        assertThat(users)
            .isEmpty();
    }

    @Test
    @DisplayName("update should modify existing user")
    void update_ShouldModify() {
        User user = new User("Old", "old@test.ya", 40);
        userRepository.save(user);
        user.setName("New");
        user.setAge(41);

        User updated = userRepository.save(user);

        assertThat(updated.getName())
            .isEqualTo("New");
        assertThat(updated.getAge())
            .isEqualTo(41);

        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found)
            .isPresent();
        assertThat(found.get().getName())
            .isEqualTo("New");
    }

    @Test
    @DisplayName("deleteById should remove user")
    void deleteById_ShouldRemove() {
        User user = new User("ToDelete", "del@test.ya", 25);
        userRepository.save(user);
        Long id = user.getId();

        userRepository.deleteById(id);

        Optional<User> found = userRepository.findById(id);
        assertThat(found)
            .isEmpty();
    }

    @Test
    @DisplayName("existsById should return true for existing user")
    void existsById_Existing_ReturnsTrue() {
        User user = new User("Exist", "exist@test.ya", 18);
        userRepository.save(user);
        boolean exists = userRepository.existsById(user.getId());
        assertThat(exists)
            .isTrue();
    }

    @Test
    @DisplayName("existsById should return false for non-existing user")
    void existsById_NonExisting_ReturnsFalse() {
        boolean exists = userRepository.existsById(88L);
        assertThat(exists)
            .isFalse();
    }
}
