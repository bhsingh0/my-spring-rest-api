# Spring Boot User REST API - Testing Guide

## Table of Contents

1. [Testing Overview](#testing-overview)
2. [Unit Testing Best Practices](#unit-testing-best-practices)
3. [How to Run Tests Locally](#how-to-run-tests-locally)
4. [Writing Test Cases](#writing-test-cases)
5. [Test Coverage](#test-coverage)
6. [Example Test Cases from UserControllerTest.java](#example-test-cases-from-usercontrollertestjava)
7. [CI/CD Testing](#cicd-testing)
8. [Troubleshooting Test Failures](#troubleshooting-test-failures)

---

## Testing Overview

### Why Testing Matters

- **Quality Assurance**: Catch bugs before production
- **Regression Prevention**: Ensure new changes don't break existing functionality
- **Documentation**: Tests serve as living documentation of expected behavior
- **Confidence**: Refactor code with confidence
- **Cost Savings**: Fix bugs early, not in production

### Testing Pyramid

```
        /\
       /  \        Unit Tests (50%)
      /    \       Fast, isolated, many
     /______\
    /        \     Integration Tests (30%)
   /          \    Multiple components, moderate
  /____________\
 /              \ E2E Tests (20%)
/______________\  Full system, slow, few
```

### Our Testing Approach

- **Focus**: Unit and integration testing
- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **HTTP Testing**: MockMvc
- **Coverage Target**: 80%+

### Test Types in This Project

| Type | Purpose | Example |
|------|---------|---------|
| **Unit Tests** | Test individual methods | Controller endpoint logic |
| **Integration Tests** | Test component interactions | Controller + Repository |
| **Contract Tests** | Verify API contracts | Request/response formats |

---

## Unit Testing Best Practices

### 1. Test Naming Convention

Tests should have descriptive names following this pattern:

```
test{MethodName}_{Scenario}_{ExpectedResult}
```

**Examples:**
- ✅ `testCreateUser_WithValidData_SavesAndReturns201`
- ✅ `testGetUserById_WithInvalidId_ReturnsNotFound`
- ✅ `testUpdateUser_WithNullName_ReturnsBadRequest`
- ❌ `testCreate` (too vague)
- ❌ `testUserCreation` (doesn't specify scenario)

### 2. AAA Pattern (Arrange-Act-Assert)

Every test should follow this structure:

```java
@Test
@DisplayName("Descriptive test name")
void testMethodName_Scenario_ExpectedResult() throws Exception {
    // ARRANGE: Set up test data and mock expectations
    User testUser = new User(1L, "John Doe", "john@example.com", "123-456-7890");
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    // ACT: Perform the action being tested
    mockMvc.perform(get("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))

    // ASSERT: Verify the results
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("John Doe")));

    // VERIFY: Check mock was called correctly
    verify(userRepository, times(1)).findById(1L);
}
```

### 3. One Assertion Focus

Each test should verify one specific behavior:

```java
// ✅ GOOD: Test one thing
@Test
void testGetUser_WithValidId_ReturnsUserData() throws Exception {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk());
}

// ❌ BAD: Testing multiple things
@Test
void testGetUser() throws Exception {
    mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.phone").exists());
}
```

### 4. Test Independence

Tests must not depend on each other:

```java
// ✅ GOOD: Each test sets up its own data
@BeforeEach
void setUp() {
    reset(userRepository); // Clear mock state
    testUser = new User(1L, "John Doe", "john@example.com", "123-456-7890");
}

// ❌ BAD: Tests depend on execution order
private static User sharedUser;

@Test
void testCreateUser() { /* ... */ sharedUser = user; }

@Test
void testUpdateUser() { /* uses sharedUser from previous test */ }
```

### 5. Mock External Dependencies

Use mocks for repositories, services, and external APIs:

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @MockBean  // Mock the repository
    private UserRepository userRepository;

    @Test
    void testGetUser_WithValidId_ReturnsUser() {
        // Arrange: Tell the mock what to return
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act & Assert: Test the controller
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
        
        // Verify the mock was called
        verify(userRepository).findById(1L);
    }
}
```

### 6. Test Edge Cases and Error Conditions

```java
// ✅ Happy path
@Test
void testCreateUser_WithValidData_SavesSuccessfully() { /* ... */ }

// ✅ Error case: Validation fails
@Test
void testCreateUser_WithMissingEmail_ReturnsBadRequest() { /* ... */ }

// ✅ Edge case: Boundary value
@Test
void testCreateUser_WithEmptyName_ReturnsBadRequest() { /* ... */ }

// ✅ Edge case: Null value
@Test
void testCreateUser_WithNullName_ReturnsBadRequest() { /* ... */ }
```

---

## How to Run Tests Locally

### Prerequisites

```bash
# Verify Java is installed
java -version

# Verify Maven is installed
mvn -version

# Should show Maven 3.8+ and Java 17+
```

### Run All Tests

```bash
# Clean and run all tests
mvn clean test

# Output:
# [INFO] Running com.example.api.UserControllerTest
# [INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

**Explanation:**
- `clean`: Removes previous build artifacts
- `test`: Compiles test code and runs all tests

**What gets tested:**
- All files matching `*Test.java` pattern
- All files in `src/test/java/` directory

### Run Specific Test Class

```bash
# Run only UserControllerTest
mvn test -Dtest=UserControllerTest

# Run tests matching pattern
mvn test -Dtest=User*Test

# Run multiple specific classes
mvn test -Dtest=UserControllerTest,UserRepositoryTest
```

**Output Example:**
```
[INFO] Running com.example.api.UserControllerTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Run Specific Test Method

```bash
# Run single test method
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList

# Run multiple specific methods
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList \
         -Dtest=UserControllerTest#testCreateUser_WithValidData_SavesAndReturns201
```

**Output Example:**
```
[INFO] Running com.example.api.UserControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Run Tests with Coverage

```bash
# Generate coverage report
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

**Coverage Metrics:**
- Line Coverage: How many lines executed
- Branch Coverage: How many if/else paths taken
- Method Coverage: How many methods called

### Run Tests in Watch Mode

```bash
# Install Maven watch plugin (optional)
mvn clean test -Dwatch

# Or use external tool like fswatch
fswatch -o src/ | xargs -n1 bash -c 'mvn test'
```

### Run Tests with Specific Log Level

```bash
# Verbose output
mvn test -X

# Show test execution time
mvn test -Dverbose

# Quiet mode
mvn test -q
```

### Verify All Tests Pass Before Commit

```bash
# Run all tests before committing
mvn clean verify

# Or create a git hook (optional)
echo '#!/bin/bash' > .git/hooks/pre-commit
echo 'mvn clean test' >> .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

---

## Writing Test Cases

### Setting Up a Test Class

```java
package com.example.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
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

    private User testUser;

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(userRepository); // Clear mock state
        testUser = new User(1L, "John Doe", "john@example.com", "123-456-7890");
    }

    // Test methods go here
}
```

### Mocking with @MockBean

#### Basic Mocking

```java
// Tell mock to return a specific value
when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

// Call the method
mockMvc.perform(get("/api/users/1"));

// Verify the mock was called
verify(userRepository).findById(1L);
```

#### Mocking with ArgumentMatchers

```java
// Accept any Long argument
when(userRepository.findById(any(Long.class)))
    .thenReturn(Optional.of(testUser));

// Accept specific value
when(userRepository.findById(eq(1L)))
    .thenReturn(Optional.of(testUser));
```

#### Mocking Multiple Calls

```java
// First call returns user, second returns empty
when(userRepository.findById(1L))
    .thenReturn(Optional.of(testUser))
    .thenReturn(Optional.empty());

// Or specify multiple calls
when(userRepository.save(any(User.class)))
    .thenReturn(savedUser1)
    .thenReturn(savedUser2);
```

#### Verify Mock Invocations

```java
// Verify called once
verify(userRepository, times(1)).findById(1L);

// Verify never called
verify(userRepository, never()).deleteById(1L);

// Verify called 5 times
verify(userRepository, times(5)).save(any(User.class));

// Verify called at least once
verify(userRepository, atLeastOnce()).findAll();
```

### Using MockMvc for HTTP Testing

#### GET Request

```java
@Test
void testGetUser_WithValidId_ReturnsUser() throws Exception {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("John Doe")))
            .andExpect(jsonPath("$.email", is("john@example.com")));
}
```

#### POST Request with Request Body

```java
@Test
void testCreateUser_WithValidData_SavesAndReturns201() throws Exception {
    User newUser = new User(null, "Alice", "alice@example.com", null);
    User savedUser = new User(3L, "Alice", "alice@example.com", null);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("Alice")));
}
```

#### PUT Request

```java
@Test
void testUpdateUser_WithValidId_UpdatesSuccessfully() throws Exception {
    User updates = new User(null, "Updated", "updated@example.com", "999-999-9999");
    User updated = new User(1L, "Updated", "updated@example.com", "999-999-9999");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(updated);

    mockMvc.perform(put("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updates)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated")));
}
```

#### DELETE Request

```java
@Test
void testDeleteUser_WithValidId_RemovesUser() throws Exception {
    when(userRepository.existsById(1L)).thenReturn(true);
    doNothing().when(userRepository).deleteById(1L);

    mockMvc.perform(delete("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

    verify(userRepository, times(1)).deleteById(1L);
}
```

#### Testing Error Responses

```java
@Test
void testCreateUser_WithMissingEmail_ReturnsBadRequest() throws Exception {
    String invalidJson = "{\"name\": \"Test User\"}";

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().isBadRequest());

    verify(userRepository, never()).save(any(User.class));
}
```

### Common MockMvc Assertions

```java
// Status assertions
.andExpect(status().isOk())           // 200
.andExpect(status().isCreated())      // 201
.andExpect(status().isNoContent())    // 204
.andExpect(status().isBadRequest())   // 400
.andExpect(status().isNotFound())     // 404
.andExpect(status().isConflict())     // 409

// JSON Path assertions
.andExpect(jsonPath("$.id", is(1)))                    // Exact match
.andExpect(jsonPath("$.name").exists())                // Field exists
.andExpect(jsonPath("$.phone").isEmpty())              // Empty value
.andExpect(jsonPath("$", hasSize(2)))                  // Array size
.andExpect(jsonPath("$[0].name", is("John")))          // Array element
.andExpect(jsonPath("$[*].email").exists())            // Any element

// Content assertions
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(content().json("expected_json_string"))
```

---

## Test Coverage

### What to Test

#### 1. Happy Path (Normal Scenario)

```java
@Test
@DisplayName("GET /api/users - should return all users")
void testGetAllUsers_ReturnsCompleteList() throws Exception {
    when(userRepository.findAll()).thenReturn(Arrays.asList(testUser1, testUser2));

    mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
}
```

#### 2. Validation Errors

```java
@Test
@DisplayName("POST /api/users - missing email returns 400")
void testCreateUser_WithMissingEmail_ReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"John\"}"))
            .andExpect(status().isBadRequest());
}
```

#### 3. Not Found Errors

```java
@Test
@DisplayName("GET /api/users/999 - returns 404")
void testGetUser_WithInvalidId_ReturnsNotFound() throws Exception {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
}
```

#### 4. Edge Cases

```java
@Test
@DisplayName("Empty user list returns 200 with empty array")
void testGetAllUsers_WithNoUsers_ReturnsEmptyList() throws Exception {
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
}

@Test
@DisplayName("Create user without optional phone field")
void testCreateUser_WithoutPhone_SavesSuccessfully() throws Exception {
    User newUser = new User(null, "Jane", "jane@example.com", null);
    
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.phone").isEmpty());
}
```

### Coverage Goals

```
Target Coverage: 80%+

Breakdown by Component:
- Controllers: 100% (all endpoints)
- Services: 80% (business logic)
- Repositories: 70% (queries)
- Entities: 60% (getters/setters)

Current Project Coverage:
- UserController: 100%
- User Entity: 100%
```

### Generate Coverage Report

```bash
# Generate JaCoCo coverage report
mvn clean test jacoco:report

# Open report
open target/site/jacoco/index.html

# Check coverage from command line
mvn jacoco:report && cat target/site/jacoco/index.html | grep -i "Total"
```

---

## Example Test Cases from UserControllerTest.java

### Complete UserControllerTest Structure

```
UserControllerTest
├── GET /api/users (2 tests)
│   ├── testGetAllUsers_ReturnsCompleteList
│   └── testGetAllUsers_WithNoUsers_ReturnsEmptyList
├── GET /api/users/{id} (3 tests)
│   ├── testGetUserById_WithValidId_ReturnsUser
│   ├── testGetUserById_WithInvalidId_ReturnsNotFound
│   └── testGetUserById_WithNonExistentId_ReturnsNotFound
├── POST /api/users (6 tests)
│   ├── testCreateUser_WithValidData_SavesAndReturns201
│   ├── testCreateUser_WithoutPhone_SavesSuccessfully
│   ├── testCreateUser_WithMissingName_ReturnsBadRequest
│   ├── testCreateUser_WithMissingEmail_ReturnsBadRequest
│   ├── testCreateUser_WithEmptyBody_ReturnsBadRequest
│   └── testCreateUser_WithNullName_ReturnsBadRequest
├── PUT /api/users/{id} (4 tests)
│   ├── testUpdateUser_WithValidId_UpdatesSuccessfully
│   ├── testUpdateUser_WithInvalidId_ReturnsNotFound
│   ├── testUpdateUser_WithPartialData_UpdatesAllFields
│   └── testUpdateUser_WithInvalidData_ReturnsBadRequest
└── DELETE /api/users/{id} (5 tests)
    ├── testDeleteUser_WithValidId_RemovesUser
    ├── testDeleteUser_WithInvalidId_ReturnsNotFound
    ├── testDeleteUser_WithNonExistentId_ReturnsNotFound
    └── testDeleteUser_MultipleUsers_DeletesSuccessfully

Total: 20 tests covering all CRUD operations
```

### Sample Test: GET All Users

```java
@Test
@DisplayName("GET /api/users - should return all users with status 200")
void testGetAllUsers_ReturnsCompleteList() throws Exception {
    // ARRANGE
    when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, testUser2));

    // ACT & ASSERT
    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("John Doe")))
            .andExpect(jsonPath("$[0].email", is("john@example.com")))
            .andExpect(jsonPath("$[0].phone", is("123-456-7890")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Jane Smith")));

    // VERIFY
    verify(userRepository, times(1)).findAll();
}
```

### Sample Test: Create User with Validation

```java
@Test
@DisplayName("POST /api/users - should return 400 with missing name")
void testCreateUser_WithMissingName_ReturnsBadRequest() throws Exception {
    // ARRANGE
    String invalidJson = "{\"email\": \"test@example.com\", \"phone\": \"123-456-7890\"}";

    // ACT & ASSERT
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().isBadRequest());

    // VERIFY
    verify(userRepository, never()).save(any(User.class));
}
```

### Sample Test: Delete User

```java
@Test
@DisplayName("DELETE /api/users/{id} - should delete user and return 204")
void testDeleteUser_WithValidId_RemovesUser() throws Exception {
    // ARRANGE
    when(userRepository.existsById(1L)).thenReturn(true);
    doNothing().when(userRepository).deleteById(1L);

    // ACT & ASSERT
    mockMvc.perform(delete("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

    // VERIFY
    verify(userRepository, times(1)).existsById(1L);
    verify(userRepository, times(1)).deleteById(1L);
}
```

---

## CI/CD Testing

### GitHub Actions Workflow

The project includes automated testing in `.github/workflows/tests.yml`:

```yaml
name: Tests
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [17]

    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: ${{ matrix.java-version }}
      - run: mvn clean install
      - run: mvn test
```

### Viewing Test Results

1. **In GitHub Web UI**:
   - Go to Actions tab
   - Click on the workflow run
   - Scroll to "Run tests" section
   - See pass/fail status

2. **Local Simulation**:
   ```bash
   # Simulate CI environment
   mvn clean install -DskipTests
   mvn test
   ```

### Test Requirements for PR

Before merging a PR:

- ✅ All tests must pass
- ✅ No new test failures
- ✅ Coverage should not decrease
- ✅ Build should succeed

---

## Troubleshooting Test Failures

### Common Issues and Solutions

#### 1. **Test Fails: "Cannot find symbol: class MockBean"**

```
Error: Cannot find symbol: class MockBean
```

**Cause**: Wrong import or missing dependency

**Solution**:
```java
// WRONG
import org.springframework.boot.test.mock.MockBean;

// CORRECT
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

// Or use proper setup
@WebMvcTest(UserController.class)
```

#### 2. **Test Fails: "Port 8080 already in use"**

```
Error: Port 8080 is already in use
```

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in test
# application.properties: server.port=8081
```

#### 3. **Mock Not Working: "Expected 1 time but was called 2 times"**

**Cause**: Mock state not cleared between tests

**Solution**:
```java
@BeforeEach
void setUp() {
    reset(userRepository);  // Clear mock state
    // Set up test data
}
```

#### 4. **Test Fails: "Wanted 1 time but was called 0 times"**

**Cause**: Mock setup incorrect or method not called

**Solution**:
```java
// Make sure to set up mock before performing action
when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

// Then perform the action
mockMvc.perform(get("/api/users/1"));
```

#### 5. **JSONPath Assertion Fails: "No value at JSON path"**

```
Error: No value at JSON path '$[0].name'
```

**Solution**:
```java
// Check response structure first
mockMvc.perform(get("/api/users"))
        .andDo(print())  // Print response
        .andExpect(jsonPath("$").isArray());

// Then use correct path
.andExpect(jsonPath("$[0].name").exists());
```

#### 6. **Test Timeout**

```
Error: Test execution timeout after 30 seconds
```

**Solution**:
```java
@Test(timeout = 5000)  // 5 second timeout
void testFast() { /* ... */ }

// For async operations
@Test(timeout = 10000)
void testAsync() { /* ... */ }
```

### Debug Techniques

#### Print Response

```java
mockMvc.perform(get("/api/users/1"))
        .andDo(print())  // Print request/response
        .andExpect(status().isOk());
```

#### Debug Mocks

```java
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(captor.capture());
User savedUser = captor.getValue();
System.out.println("Saved: " + savedUser.getName());
```

#### Run with Debug Output

```bash
# Verbose output
mvn test -X

# Show all test names
mvn test -Dverbose
```

#### Single Test Debugging

```bash
# Run one test to debug
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList -X
```

### Performance Tips

```bash
# Run tests in parallel (faster)
mvn test -T 1C  # 1 thread per CPU core

# Skip compilation
mvn test -DskipCompile

# Skip integration tests
mvn test -DskipIntegrationTests

# Fail fast on first failure
mvn test -ff  # Fail-fast
```

---

## Best Practices Checklist

- [ ] Use AAA pattern (Arrange-Act-Assert)
- [ ] Test names describe what is being tested
- [ ] Tests are independent and isolated
- [ ] Mock external dependencies
- [ ] Reset mocks between tests
- [ ] Test happy path, errors, and edge cases
- [ ] Verify mock interactions
- [ ] Run all tests before commit
- [ ] Maintain 80%+ coverage
- [ ] Update tests when requirements change
- [ ] Document complex test setup

---

## Quick Reference: Common Commands

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=UserControllerTest

# Run specific test method
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList

# Generate coverage report
mvn clean test jacoco:report

# Run with verbose output
mvn test -X

# Skip tests during build
mvn install -DskipTests

# View test report
open target/site/jacoco/index.html

# Kill port in use
lsof -i :8080 && kill -9 <PID>
```

---

## Resources

- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **Spring Boot Testing**: https://spring.io/guides/gs/testing-web/
- **MockMvc**: https://spring.io/guides/gs/testing-web/
- **AssertJ**: https://assertj.github.io/assertj-core-features-highlight.html

---

**Last Updated**: May 13, 2026
**Version**: 1.0
