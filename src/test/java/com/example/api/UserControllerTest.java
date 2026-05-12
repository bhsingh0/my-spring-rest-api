package com.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User testUser2;

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        // Reset the mock before each test to clear invocation history
        reset(userRepository);
        
        testUser = new User(1L, "John Doe", "john@example.com", "123-456-7890");
        testUser2 = new User(2L, "Jane Smith", "jane@example.com", "098-765-4321");
    }

    // ==================== GET /api/users ====================

    @Test
    @DisplayName("GET /api/users - should return all users with status 200")
    void testGetAllUsers_ReturnsCompleteList() throws Exception {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testUser2));

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[0].phone", is("123-456-7890")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/users - should return empty list when no users exist")
    void testGetAllUsers_WithNoUsers_ReturnsEmptyList() throws Exception {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userRepository, times(1)).findAll();
    }

    // ==================== GET /api/users/{id} ====================

    @Test
    @DisplayName("GET /api/users/{id} - should return user when id is valid")
    void testGetUserById_WithValidId_ReturnsUser() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.phone", is("123-456-7890")));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return 404 when user not found")
    void testGetUserById_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return 404 for non-existent id")
    void testGetUserById_WithNonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/users/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(0L);
    }

    // ==================== POST /api/users ====================

    @Test
    @DisplayName("POST /api/users - should create user and return 201 with valid data")
    void testCreateUser_WithValidData_SavesAndReturns201() throws Exception {
        // Arrange
        User newUser = new User(null, "Alice Johnson", "alice@example.com", "555-123-4567");
        User savedUser = new User(3L, "Alice Johnson", "alice@example.com", "555-123-4567");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Alice Johnson")))
                .andExpect(jsonPath("$.email", is("alice@example.com")))
                .andExpect(jsonPath("$.phone", is("555-123-4567")));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should create user with minimal data (no phone)")
    void testCreateUser_WithoutPhone_SavesSuccessfully() throws Exception {
        // Arrange
        User newUser = new User(null, "Bob Wilson", "bob@example.com", null);
        User savedUser = new User(4L, "Bob Wilson", "bob@example.com", null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("Bob Wilson")))
                .andExpect(jsonPath("$.email", is("bob@example.com")))
                .andExpect(jsonPath("$.phone", nullValue()));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should return 400 with missing name")
    void testCreateUser_WithMissingName_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidUserJson = "{\"email\": \"test@example.com\", \"phone\": \"123-456-7890\"}";

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should return 400 with missing email")
    void testCreateUser_WithMissingEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidUserJson = "{\"name\": \"Test User\", \"phone\": \"123-456-7890\"}";

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should return 400 with empty request body")
    void testCreateUser_WithEmptyBody_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should return 400 with null name")
    void testCreateUser_WithNullName_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidUserJson = "{\"name\": null, \"email\": \"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/users - should return 400 with empty name")
    void testCreateUser_WithEmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidUserJson = "{\"name\": \"\", \"email\": \"test@example.com\"}";

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== PUT /api/users/{id} ====================

    @Test
    @DisplayName("PUT /api/users/{id} - should update user and return 200 with valid id")
    void testUpdateUser_WithValidId_UpdatesSuccessfully() throws Exception {
        // Arrange
        User userUpdates = new User(null, "John Updated", "john.updated@example.com", "999-999-9999");
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com", "999-999-9999");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")))
                .andExpect(jsonPath("$.phone", is("999-999-9999")));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should return 404 when user not found")
    void testUpdateUser_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        User userUpdates = new User(null, "John Updated", "john.updated@example.com", "999-999-9999");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdates)))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should update only specified fields")
    void testUpdateUser_WithPartialData_UpdatesAllFields() throws Exception {
        // Arrange
        User userUpdates = new User(null, "Jane Doe", "jane.doe@example.com", "555-555-5555");
        User updatedUser = new User(1L, "Jane Doe", "jane.doe@example.com", "555-555-5555");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$.phone", is("555-555-5555")));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should return 400 with invalid update data")
    void testUpdateUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        String invalidUpdateJson = "{\"name\": null, \"email\": null}";

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUpdateJson))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== DELETE /api/users/{id} ====================

    @Test
    @DisplayName("DELETE /api/users/{id} - should delete user and return 204 with valid id")
    void testDeleteUser_WithValidId_RemovesUser() throws Exception {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - should return 404 when user not found")
    void testDeleteUser_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/users/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - should return 404 for non-existent user")
    void testDeleteUser_WithNonExistentId_ReturnsNotFound() throws Exception {
        // Arrange
        when(userRepository.existsById(0L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/users/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userRepository, times(1)).existsById(0L);
        verify(userRepository, never()).deleteById(0L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - should successfully delete multiple users sequentially")
    void testDeleteUser_MultipleUsers_DeletesSuccessfully() throws Exception {
        // First deletion
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Second deletion
        when(userRepository.existsById(2L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(2L);

        mockMvc.perform(delete("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository, times(1)).deleteById(2L);
    }
}
