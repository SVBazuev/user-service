package edu.example;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import edu.example.config.TestConfig;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Testcontainers
class UserServiceApplicationIT {

    @Container
    @SuppressWarnings("resource")
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
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = "admin@demo.ya" + ":" + "admin123";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void createAndGetUser_ShouldWork() {
        UserRequest newUser = new UserRequest(
            "IT Test", "it@test.ya", 25, "password123"
        );
        HttpEntity<UserRequest> entity = new HttpEntity<>(newUser, authHeaders());

        ResponseEntity<UserResponse> createResponse = restTemplate.exchange(
            "/api/users", HttpMethod.POST, entity, UserResponse.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long userId = createResponse.getBody().id();

        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<UserResponse> getResponse = restTemplate.exchange(
            "/api/users/" + userId, HttpMethod.GET, getEntity, UserResponse.class
        );
        assertThat(getResponse.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().name())
            .isEqualTo("IT Test");
    }

    @Test
    void updateAndDeleteUser_ShouldWork() {
        UserRequest newUser = new UserRequest(
            "Update", "update@test.ya", 30, "password123"
        );
        HttpEntity<UserRequest> createEntity = new HttpEntity<>(newUser, authHeaders());
        ResponseEntity<UserResponse> create = restTemplate.exchange(
            "/api/users", HttpMethod.POST, createEntity, UserResponse.class
        );
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = create.getBody().id();

        UserRequest updateReq = new UserRequest("Updated", "updated@test.ya", 31, "password123");
        HttpEntity<UserRequest> updateEntity = new HttpEntity<>(updateReq, authHeaders());
        restTemplate.exchange(
            "/api/users/" + id, HttpMethod.PUT, updateEntity, UserResponse.class
        );

        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<UserResponse> getAfterUpdate = restTemplate.exchange(
            "/api/users/" + id, HttpMethod.GET, getEntity, UserResponse.class
        );
        assertThat(getAfterUpdate.getStatusCode())
            .isEqualTo(HttpStatus.OK);
        assertThat(getAfterUpdate.getBody().name())
            .isEqualTo("Updated");
        assertThat(getAfterUpdate.getBody().email())
            .isEqualTo("updated@test.ya");

        restTemplate.exchange(
            "/api/users/" + id, HttpMethod.DELETE, getEntity, Void.class
        );

        ResponseEntity<Void> getAfterDelete = restTemplate.exchange(
            "/api/users/" + id, HttpMethod.GET, getEntity, Void.class
        );
        assertThat(getAfterDelete.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
