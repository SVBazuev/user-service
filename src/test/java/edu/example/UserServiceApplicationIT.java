package edu.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserServiceApplicationIT {

    @Container
    static PostgreSQLContainer<?> postgres = (
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
    );

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAndGetUser_ShouldWork() {
        UserRequest newUser = new UserRequest(
            "IT Test", "it@test.ya", 25
        );
        ResponseEntity<UserResponse> createResponse = (
            restTemplate.postForEntity(
                "/api/users", newUser, UserResponse.class
            )
        );
        assertThat(createResponse.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
        Long userId = createResponse.getBody().id();

        ResponseEntity<UserResponse> getResponse = (
            restTemplate.getForEntity(
                "/api/users/" + userId, UserResponse.class
            )
        );
        assertThat(getResponse.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name())
            .isEqualTo("IT Test");
    }

    @Test
    void updateAndDeleteUser_ShouldWork() {
        // Создали
        UserRequest newUser = new UserRequest(
            "Update", "update@test.ya", 30
        );
        ResponseEntity<UserResponse> create = restTemplate.postForEntity(
                "/api/users", newUser, UserResponse.class
        );
        assertThat(create.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);
        Long id = create.getBody().id();

        // Обновили
        UserRequest updateReq = new UserRequest(
            "Updated", "updated@test.ya", 31
        );
        restTemplate.put("/api/users/" + id, updateReq);

        // Проверили
        ResponseEntity<UserResponse> getAfterUpdate = restTemplate.getForEntity(
                "/api/users/" + id, UserResponse.class
        );
        assertThat(getAfterUpdate.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(getAfterUpdate.getBody().name())
            .isEqualTo("Updated");
        assertThat(getAfterUpdate.getBody().email())
            .isEqualTo("updated@test.ya");
        assertThat(getAfterUpdate.getBody().age())
            .isEqualTo(31);

        // Удалили
        restTemplate.delete("/api/users/" + id);

        // Проверили через GET
        ResponseEntity<Void> getAfterDelete = restTemplate.exchange(
                "/api/users/" + id, HttpMethod.GET, null, Void.class
        );
        assertThat(getAfterDelete.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
