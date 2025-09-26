package com.innowise.userservice.controller;

import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing users in the system.
 * Provides endpoints for CRUD operations on users.
 *
 * @see UserService
 * @see UserRequestDto
 * @see UserResponseDto
 */
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Retrieves a user by unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the user data
     * @throws ResourceNotFoundException if the user with given ID does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Long id) {
        UserResponseDto retrievedUser = userService.getUserById(id);

        return ResponseEntity.ok(retrievedUser);
    }

    /**
     * Retrieves users based on provided IDs or all users if no IDs are specified.
     * Returns empty list if no users match the criteria.
     *
     * @param ids optional list of user IDs to filter by
     * @return list of users
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers(@RequestParam(required = false) List<Long> ids) {
        List<UserResponseDto> retrievedUsers;

        if (ids != null && !ids.isEmpty()) {
            retrievedUsers = userService.getUsersByIds(ids);
        } else {
            retrievedUsers = userService.getAllUsers();
        }

        return ResponseEntity.ok(retrievedUsers);
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address, must be valid and not blank
     * @return the user data
     * @throws ResourceNotFoundException if the user with given email does not exist
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam @NotBlank @Email String email) {
        UserResponseDto retrievedUser = userService.getUserByEmail(email);

        return ResponseEntity.ok(retrievedUser);
    }

    /**
     * Creates a new user with provided data.
     *
     * @param userRequestDto the user data to create
     * @return the created user data
     * @throws EmailAlreadyExistsException if email is already registered
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates an existing user's information. Automatically updates holder names in associated cards
     * data if user's name or surname changes.
     *
     * @param id the unique identifier of the user to update
     * @param userRequestDto the user data to update
     * @return the updated user data
     * @throws ResourceNotFoundException if the user with given ID does not exist
     * @throws EmailAlreadyExistsException if new email is already taken by another user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("id") Long id,
                                                      @RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user by unique identifier. Also removes all associated cards.
     *
     * @param id the unique identifier of the user to delete
     * @return empty response
     * @throws ResourceNotFoundException if the user with given ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}