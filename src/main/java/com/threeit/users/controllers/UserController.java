package com.threeit.users.controllers;

import com.threeit.users.dto.UserCreateDTO;
import com.threeit.users.dto.UserCreatedDTO;
import com.threeit.users.dto.UserDTO;
import com.threeit.users.dto.UserLoginDTO;
import com.threeit.users.entities.User;
import com.threeit.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves a list of all available users.
     * The method leverages userService to fetch the user data.
     *
     * @return a ResponseEntity containing a list of UserDTO objects representing all users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all available users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to be retrieved
     * @return a {@link ResponseEntity} containing the user if found; otherwise, a 404 error response
     */
    @Operation(summary = "Get a user by ID", description = "Retrieves a single user based on their unique ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the user")
    @ApiResponse(responseCode = "404", description = "User with the specified ID not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"User not found\"}")))
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Creates a new user based on the provided user details and returns the newly created user's information.
     *
     * @param userCreateDTO the data required to create a new user
     * @return a ResponseEntity containing the details of the created user, including their ID, creation timestamp, and token
     */
    @Operation(summary = "Create a new user", description = "Creates a new user based on provided details and returns their information")
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid user input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Bad Request\" , \"fieldErrors\": { \"field\": \"error\"}}")))
    @PostMapping
    private ResponseEntity<UserCreatedDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userCreateDTO));
    }


    /**
     * Authenticates a user based on their email and password.
     *
     * @param userLoginDTO The DTO containing user's email and password for login.
     * @return A ResponseEntity containing a Map with a token if the authentication is successful.
     */
    @Operation(summary = "User login", description = "Authenticates a user based on their email and password")
    @ApiResponse(responseCode = "200", description = "User successfully authenticated")
    @ApiResponse(responseCode = "404", description = "Not Found if the user with email and/or password not matches", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Not found\" , \n \"message\": \"Invalid email or password\"}")))
    @PostMapping("/login")
    private ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(Map.of("token", userService.login(userLoginDTO).getToken()));
    }

}
