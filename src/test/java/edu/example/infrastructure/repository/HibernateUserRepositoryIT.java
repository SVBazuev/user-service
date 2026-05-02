package edu.example.infrastructure.repository;

import edu.example.core.entity.User;
import edu.example.core.repository.UserRepository;
import edu.example.exception.UserNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DisplayName("HibernateUserRepository Integration Tests")
class HibernateUserRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserRepository userRepository;

    @BeforeAll
    static void initSessionFactory() {
        Configuration cfg = new Configuration();
        cfg.setProperty(
            "hibernate.connection.url",
            postgres.getJdbcUrl()
        );
        cfg.setProperty(
            "hibernate.connection.username",
            postgres.getUsername()
        );
        cfg.setProperty(
            "hibernate.connection.password",
            postgres.getPassword()
        );
        cfg.setProperty(
            "hibernate.connection.driver_class",
            "org.postgresql.Driver"
        );
        cfg.setProperty(
            "hibernate.dialect",
            "org.hibernate.dialect.PostgreSQLDialect"
        );
        cfg.setProperty(
            "hibernate.hbm2ddl.auto",
            "create-drop"
        );
        cfg.setProperty(
            "hibernate.show_sql",
            "true"
        );
        cfg.setProperty(
            "hibernate.current_session_context_class",
            "thread"
        );
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userRepository = new HibernateUserRepository(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
        sessionFactory.getCurrentSession().close();
    }

    @Test
    @DisplayName("save should persist user and generate id")
    void save_ShouldGenerateId() {
        User user = new User("John", "john@test.com", 30);
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
        assertThat(found.get().getEmail()).isEqualTo("john@test.com");
        assertThat(found.get().getAge()).isEqualTo(30);
    }

    @Test
    @DisplayName("findById should return user when exists")
    void findById_Existing_ReturnsUser() {
        User user = new User("Alice", "alice@test.com", 28);
        userRepository.save(user);
        Long id = user.getId();

        Optional<User> found = userRepository.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("findById should return empty when user not found")
    void findById_NonExisting_ReturnsEmpty() {
        Optional<User> found = userRepository.findById(999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll should return all users")
    void findAll_ShouldReturnList() {
        userRepository.save(new User("User1", "u1@test.com", 20));
        userRepository.save(new User("User2", "u2@test.com", 30));

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users)
            .extracting(User::getName)
            .containsExactlyInAnyOrder("User1", "User2");
    }

    @Test
    @DisplayName("findAll should return empty list when no users")
    void findAll_EmptyList() {
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("update should modify existing user")
    void update_ShouldModify() {
        User user = new User("OldName", "old@test.com", 40);
        userRepository.save(user);
        user.setName("NewName");
        user.setAge(41);

        userRepository.update(user);
        sessionFactory.getCurrentSession().flush();

        sessionFactory.getCurrentSession().clear();

        Optional<User> updated = userRepository.findById(user.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("NewName");
        assertThat(updated.get().getAge()).isEqualTo(41);
        assertThat(updated.get().getEmail()).isEqualTo("old@test.com");
    }

    @Test
    @DisplayName("deleteById should remove user")
    void deleteById_ShouldRemove() {
        User user = new User("ToDelete", "del@test.com", 25);
        userRepository.save(user);
        Long id = user.getId();

        userRepository.deleteById(id);

        Optional<User> found = userRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("deleteById should throw UserNotFoundException when user does not exist")
    void deleteById_NonExisting_ThrowsException() {
        assertThatThrownBy(() -> userRepository.deleteById(888L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    @DisplayName("existsById should return true for existing user")
    void existsById_Existing_ReturnsTrue() {
        User user = new User("Exist", "exist@test.com", 18);
        userRepository.save(user);
        boolean exists = userRepository.existsById(user.getId());
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById should return false for non-existing user")
    void existsById_NonExisting_ReturnsFalse() {
        boolean exists = userRepository.existsById(777L);
        assertThat(exists).isFalse();
    }
}
