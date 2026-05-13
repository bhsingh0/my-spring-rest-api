package com.example.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 *
 * <p>Base path: {@code /api/users}
 *
 * <p>All {@code id} path variables must be positive ({@code > 0}). Requests with
 * {@code id <= 0} are rejected with {@code 400 Bad Request} before hitting the
 * repository. Duplicate email addresses result in {@code 409 Conflict}.
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns all users.
     *
     * @return {@code 200 OK} with the full list of users (may be empty)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Returns a single user by ID.
     *
     * @param id the user ID (must be &gt; 0)
     * @return {@code 200 OK} with the user, or {@code 404 Not Found} if no user exists with that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable @Positive Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user.
     *
     * @param user the user data; {@code name} and a valid {@code email} are required
     * @return {@code 201 Created} with the saved user, or {@code 400 Bad Request} if
     *         validation fails, or {@code 409 Conflict} if the email is already in use
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Replaces all fields of an existing user.
     *
     * @param id          the ID of the user to update (must be &gt; 0)
     * @param userDetails the replacement user data; {@code name} and a valid {@code email} are required
     * @return {@code 200 OK} with the updated user, {@code 404 Not Found} if no user exists
     *         with that ID, {@code 400 Bad Request} if validation fails, or
     *         {@code 409 Conflict} if the new email is already in use by another user
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @Positive Long id, @Valid @RequestBody User userDetails) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPhone(userDetails.getPhone());
            return ResponseEntity.ok(userRepository.save(user));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete (must be &gt; 0)
     * @return {@code 204 No Content} on success, or {@code 404 Not Found} if no user
     *         exists with that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
