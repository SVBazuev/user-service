package edu.example.api;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.exception.UserNotFoundException;
import edu.example.core.service.UserService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController REST API Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @DisplayName("should return list of users")
        void getAllUsers_ReturnsList() throws Exception {
            UserResponse user1 = new UserResponse(
                1L, "First", "first@test.ya", 25, null
            );
            UserResponse user2 = new UserResponse(
                2L, "Second", "second@test.ya", 30, null
            );
            when(userService.getAll())
                .thenReturn(List.of(user1, user2));

            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("First"))
                .andExpect(jsonPath("$[1].name").value("Second"));
        }

        @Test
        @DisplayName("should return empty list when no users")
        void getAllUsers_ReturnsEmptyList() throws Exception {
            when(userService.getAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("should return user when found")
        void getUserById_Found() throws Exception {
            UserResponse response = new UserResponse(
                1L, "First", "first@test.ya", 25, null
            );
            when(userService.getById(1L)).thenReturn(response);

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("First"));
        }

        @Test
        @DisplayName("should return 404 when user not found")
        void getUserById_NotFound() throws Exception {
            when(userService.getById(99L))
                .thenThrow(new UserNotFoundException(99L));

            mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID 99 не найден"));
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateUser {

        @Test
        @DisplayName("should create user and return 201")
        void createUser_ValidInput() throws Exception {
            UserRequest request = new UserRequest(
                "First", "first@test.ya", 25
            );
            UserResponse response = new UserResponse(
                1L, "First", "first@test.ya", 25, null
            );
            when(userService.create(any(UserRequest.class)))
                .thenReturn(response);

            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("First"));
        }

        @Test
        @DisplayName("should return 400 for invalid input (empty name)")
        void createUser_InvalidName() throws Exception {
            UserRequest invalidRequest = new UserRequest(
                "", "no-name@test.ya", 25
            );

            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                    .value("Имя не может быть пустым"));
        }

        @Test
        @DisplayName("should return 400 for invalid email format")
        void createUser_InvalidEmail() throws Exception {
            UserRequest invalidRequest = new UserRequest(
                "First", "not-an-email", 25);

            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email")
                    .value("Некорректный email"));
        }

        @Test
        @DisplayName("should return 400 for age out of range")
        void createUser_InvalidAge() throws Exception {
            UserRequest invalidRequest = new UserRequest(
                "Immortal", "immortal@test.ya", 200
            );

            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age")
                    .value("Возраст должен быть <= 150"));
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("should update user and return 200")
        void updateUser_Success() throws Exception {
            UserRequest updateRequest = new UserRequest(
                "Updated", "updated@test.ya", 35
            );
            UserResponse response = new UserResponse(
                1L, "Updated", "updated@test.ya", 35, null
            );
            when(userService.update(eq(1L), any(UserRequest.class)))
                .thenReturn(response);

            mockMvc.perform(put("/api/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                    .value("Updated"));
        }

        @Test
        @DisplayName("should return 404 when updating non-existent user")
        void updateUser_NotFound() throws Exception {
            UserRequest updateRequest = new UserRequest(
                "Third", "third@test.ya", 30
            );
            when(userService.update(eq(99L), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException(99L));

            mockMvc.perform(put("/api/users/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID 99 не найден"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("should delete user and return 204")
        void deleteUser_Success() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when deleting non-existent user")
        void deleteUser_NotFound() throws Exception {
            doThrow(new UserNotFoundException(99L))
                .when(userService).delete(99L);

            mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Пользователь с ID 99 не найден"));
        }
    }
}
