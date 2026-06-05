package edu.example.api;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import edu.example.core.dto.OnCreate;
import edu.example.core.dto.OnUpdate;
import edu.example.core.dto.UserRequest;
import edu.example.core.dto.UserResponse;
import edu.example.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Get all users",
        description = "Returns a list of all registered users"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @GetMapping
    public CollectionModel<EntityModel<UserResponse>> getAllUsers() {
        List<EntityModel<UserResponse>> users = userService.getAll().stream()
            .map(user -> EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.id()))
                    .withSelfRel())
            )
            .collect(Collectors.toList());

        return CollectionModel.of(users,
            linkTo(methodOn(UserController.class).getAllUsers())
                .withSelfRel(),
            linkTo(methodOn(UserController.class).createUser(null))
                .withRel("create")
        );
    }

    @Operation(
        summary = "Get user by ID",
        description = "Returns a single user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getUserById(
    @Parameter(description = "User ID", example = "1")
    @PathVariable Long id) {
        UserResponse user = userService.getById(id);
        return EntityModel.of(
            user,
            linkTo(methodOn(UserController.class).getUserById(id))
                .withSelfRel(),
            linkTo(methodOn(UserController.class).getAllUsers())
                .withRel("all-users")
        );
    }

    @Operation(
        summary = "Create a new user",
        description = "Admin only. Creates a user and returns its data with links."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
        }
    )
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> createUser(
    @Validated({Default.class, OnCreate.class}) @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        EntityModel<UserResponse> model = EntityModel.of(created,
            linkTo(methodOn(UserController.class).getUserById(created.id()))
                .withSelfRel(),
            linkTo(methodOn(UserController.class).getAllUsers())
                .withRel("all-users"));
        return ResponseEntity
            .created(
                linkTo(methodOn(UserController.class).getUserById(created.id()))
                    .toUri()
            )
            .body(model);
    }

    @Operation(
        summary = "Update an existing user",
        description = "Admin only. Updates user data."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> updateUser(
    @Parameter(description = "User ID", example = "1")
    @PathVariable Long id,
    @Validated({Default.class, OnUpdate.class}) @RequestBody UserRequest request) {
        UserResponse updated = userService.update(id, request);
        EntityModel<UserResponse> model = EntityModel.of(updated,
            linkTo(methodOn(UserController.class).getUserById(id))
                .withSelfRel(),
            linkTo(methodOn(UserController.class).getAllUsers())
                .withRel("all-users"));
        return ResponseEntity.ok(model);
    }

    @Operation(
        summary = "Delete a user",
        description = "Admin only. Deletes a user by ID."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
    @Parameter(description = "User ID", example = "1")
    @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
