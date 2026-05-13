# Spring Boot User REST API

A comprehensive, production-ready REST API for managing user data built with Spring Boot 3.2.0, featuring complete CRUD operations, input validation, and comprehensive test coverage.

**Status**: Active Development | **Version**: 1.0.0 | **Last Updated**: May 13, 2026

---

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Quick Start Guide](#quick-start-guide)
- [Project Structure](#project-structure)
- [API Endpoints Reference](#api-endpoints-reference)
- [Testing Locally - Step by Step](#testing-locally---step-by-step)
- [Testing the User Endpoints](#testing-the-user-endpoints)
- [Development Workflow](#development-workflow)
- [Automated Testing Workflow](#automated-testing-workflow)
- [Troubleshooting & FAQ](#troubleshooting--faq)
- [Documentation](#documentation)
- [Contributing](#contributing)

---

## Project Overview

### What is this project?

This is a fully functional Spring Boot REST API that provides complete user management capabilities. The API follows RESTful principles and best practices, with comprehensive test coverage, input validation, and proper error handling. It's designed to serve as a reference implementation for Spring Boot REST API development and testing.

### Key Features

✅ **Complete CRUD Operations**
- List all users
- Retrieve single user by ID
- Create new users with validation
- Update existing users
- Delete users

✅ **Input Validation**
- Email validation (@Email)
- Name validation (@NotBlank)
- Automatic error responses

✅ **Comprehensive Testing**
- 20 unit tests with 100% pass rate
- Tests cover all endpoints and error cases
- MockMvc for HTTP layer testing
- Mockito for repository mocking

✅ **Error Handling**
- Consistent error response format
- Proper HTTP status codes
- Field-level validation errors

✅ **Production Ready**
- Spring Boot 3.2.0
- Java 17 compatibility
- H2 database (in-memory)
- Maven build automation

✅ **Developer Friendly**
- Comprehensive documentation
- Testing guidelines
- Development workflow guide
- Git best practices

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.0 |
| **Language** | Java | 17+ |
| **Build Tool** | Maven | 3.8+ |
| **Testing Framework** | JUnit 5 (Jupiter) | Included in Spring Boot |
| **Mocking Framework** | Mockito | Included in Spring Boot |
| **HTTP Testing** | MockMvc | Spring Test |
| **Database** | H2 | In-memory (dev/test) |
| **Validation** | Jakarta Validation | 3.0+ |
| **Lombok** | Code Generation | 1.18+ |

### Maven Dependencies

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## Quick Start Guide

### Prerequisites

Before you begin, ensure you have installed:

```bash
# Java 17+
java -version
# Expected: java version "17.0.x" or higher

# Maven 3.8+
mvn -version
# Expected: Maven 3.8.x or higher

# Git 2.0+
git --version
# Expected: git version 2.x or higher
```

### Installation

#### 1. Clone the Repository

```bash
# Clone the repository
git clone https://github.com/bhsingh0/my-spring-rest-api.git

# Navigate to project directory
cd my-spring-rest-api

# Verify directory structure
ls -la
```

#### 2. Install Dependencies

```bash
# Clean and install dependencies
mvn clean install

# This command will:
# - Remove previous build artifacts
# - Download all dependencies
# - Compile source code
# - Run tests
# - Package the application
```

#### 3. Run the Application

**Option 1: Using Maven Plugin**

```bash
# Start the application with hot reload
mvn spring-boot:run

# Output should show:
# Started Application in X.XXX seconds
# Tomcat started on port(s): 8080
```

**Option 2: Building and Running JAR**

```bash
# Build the application
mvn clean package

# Run the generated JAR file
java -jar target/my-spring-app-1.0.0.jar

# Application will start on port 8080
```

#### 4. Verify Installation

```bash
# Test the API is running
curl http://localhost:8080/api/users

# Expected response: [] (empty user list)
```

---

## Project Structure

```
my-spring-rest-api/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/api/
│   │   │   ├── Application.java              # Spring Boot entry point
│   │   │   ├── User.java                     # User entity with validation
│   │   │   ├── UserController.java           # REST controller with endpoints
│   │   │   ├── UserRepository.java           # JPA repository interface
│   │   │   └── UserService.java              # Business logic (optional)
│   │   │
│   │   └── resources/
│   │       └── application.properties        # Application configuration
│   │
│   └── test/
│       └── java/com/example/api/
│           ├── UserControllerTest.java       # 20 comprehensive unit tests
│           └── UserServiceTest.java          # Service layer tests (optional)
│
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── WORKFLOW.md                                # Development workflow guide
├── API_DOCUMENTATION.md                       # API reference documentation
├── CLAUDE.md                                  # Claude AI coding guidelines
│
├── .github/
│   └── workflows/
│       ├── tests.yml                          # GitHub Actions for tests
│       └── claude-review.yml                  # Claude code review automation
│
└── target/                                    # Build output (generated)
    ├── classes/                               # Compiled classes
    ├── test-classes/                          # Compiled tests
    └── my-spring-app-1.0.0.jar               # Packaged application
```

### Main Components

**Application.java**
- Spring Boot entry point with `@SpringBootApplication`
- Auto-configures Spring Boot components

**User.java**
- JPA entity representing a user in the database
- Fields: id, name, email, phone
- Validation annotations for input validation
- Uses Lombok @Data for getter/setter generation

**UserController.java**
- REST controller mapping HTTP requests to handler methods
- Endpoints for CRUD operations
- Request/response handling
- Error handling

**UserRepository.java**
- Spring Data JPA repository
- Extends `JpaRepository<User, Long>`
- Provides database operations

**UserControllerTest.java**
- Comprehensive unit test suite
- 20 tests covering all endpoints
- Uses @WebMvcTest and MockMvc
- 100% endpoint coverage

---

## API Endpoints Reference

### Complete Endpoint Summary

| Method | Endpoint | Purpose | Status Code |
|--------|----------|---------|-------------|
| GET | `/api/users` | List all users | 200, 500 |
| GET | `/api/users/{id}` | Get single user | 200, 404, 400 |
| POST | `/api/users` | Create user | 201, 400, 409 |
| PUT | `/api/users/{id}` | Update user | 200, 400, 404 |
| DELETE | `/api/users/{id}` | Delete user | 204, 400, 404 |

### User Object Schema

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "123-456-7890"
}
```

| Field | Type | Required | Unique | Notes |
|-------|------|----------|--------|-------|
| id | integer | Auto | Yes | Auto-generated primary key |
| name | string | Yes | No | 1-255 characters, cannot be blank |
| email | string | Yes | Yes | Valid email format, unique in DB |
| phone | string | No | No | Optional field, can be null |

### Endpoint Specifications

#### GET /api/users - List All Users

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "123-456-7890"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phone": "098-765-4321"
  }
]
```

---

#### GET /api/users/{id} - Get Single User

```bash
# Get user with ID 1
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "123-456-7890"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 999 not found"
}
```

---

#### POST /api/users - Create User

```bash
# Create with all fields
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "phone": "555-123-4567"
  }'

# Create without phone
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Wilson",
    "email": "bob@example.com"
  }'
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "phone": "555-123-4567"
}
```

**Response (400 Bad Request - Missing Email):**
```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email is required"
  }
}
```

---

#### PUT /api/users/{id} - Update User

```bash
# Update user with ID 1
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com",
    "phone": "999-999-9999"
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Updated",
  "email": "john.updated@example.com",
  "phone": "999-999-9999"
}
```

---

#### DELETE /api/users/{id} - Delete User

```bash
# Delete user with ID 1
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"
```

**Response (204 No Content):**
```
(Empty response body)
Status: 204
```

---

## Testing Locally - Step by Step

### Running All Tests

```bash
# Run all tests with clean build
mvn clean test

# Expected output:
# [INFO] Running com.example.api.UserControllerTest
# [INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

### Running Specific Test Class

```bash
# Run only UserControllerTest
mvn test -Dtest=UserControllerTest

# Run tests matching pattern
mvn test -Dtest=UserController*
```

### Running Specific Test Method

```bash
# Run single test method
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList

# Run multiple specific tests
mvn test -Dtest=UserControllerTest#testCreateUser* 
```

### Understanding Test Output

```
[INFO] Running com.example.api.UserControllerTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.698 s -- in com.example.api.UserControllerTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

**Key Metrics:**
- **Tests run**: Total number of test methods executed
- **Failures**: Tests that failed assertions
- **Errors**: Tests that threw exceptions
- **Skipped**: Tests that were not executed (marked with @Disabled)
- **Time elapsed**: Total test execution time

### Test Coverage Report

```bash
# Generate JaCoCo coverage report
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

---

## Testing the User Endpoints

### Test Workflow - Full CRUD Cycle

#### 1. Start the Application

```bash
# Terminal 1: Start the application
mvn spring-boot:run

# Wait for: "Started Application in X.XXX seconds"
# Application is ready at: http://localhost:8080
```

#### 2. Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phone": "123-456-7890"
  }' | jq

# Expected response (User ID 1 or next available):
# {
#   "id": 1,
#   "name": "Test User",
#   "email": "test@example.com",
#   "phone": "123-456-7890"
# }
```

#### 3. Get All Users

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json" | jq

# Expected: Array with the created user
```

#### 4. Get Specific User

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" | jq

# Expected: Single user object
```

#### 5. Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated User",
    "email": "updated@example.com",
    "phone": "999-999-9999"
  }' | jq

# Expected: Updated user object with new values
```

#### 6. Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"

# Expected output:
# (No response body)
# Status: 204
```

#### 7. Verify Deletion

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -w "\nStatus: %{http_code}\n"

# Expected output:
# {"timestamp":"...","status":404,"error":"Not Found","message":"User with id 1 not found"}
# Status: 404
```

### Testing Error Cases

#### Missing Required Field

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe"
  }' | jq

# Expected: 400 Bad Request with validation error
```

#### Invalid Email Format

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "invalid-email"
  }' | jq

# Expected: 400 Bad Request with email validation error
```

#### Non-existent User

```bash
curl -X GET http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"

# Expected: 404 Not Found
```

---

## Development Workflow

### Local Development Process

#### 1. Create Feature Branch

```bash
# Ensure you're on main branch
git checkout main
git pull origin main

# Create feature branch
git checkout -b feature/your-feature-name

# Examples:
git checkout -b feature/add-user-validation
git checkout -b feature/improve-error-handling
```

#### 2. Make Changes

```bash
# Edit files in your IDE
# Follow the guidelines in CLAUDE.md

# Run tests frequently
mvn clean test
```

#### 3. Commit Changes

```bash
# Check what changed
git status

# Stage files
git add src/main/java/com/example/api/User.java
git add src/test/java/com/example/api/UserControllerTest.java

# Verify staged changes
git diff --cached

# Commit with descriptive message
git commit -m "feat: Add email validation to User entity

- Add @Email annotation to email field
- Add custom validation message
- Update UserControllerTest with new validation tests
- All 22 tests passing"
```

#### 4. Verify Tests Pass

```bash
# Run all tests
mvn clean test

# Ensure all tests pass before pushing
```

#### 5. Push to Remote

```bash
# Push feature branch
git push origin feature/your-feature-name
```

### Git Workflow Best Practices

**Commit Message Format:**
```
<type>: <subject>

<body>

Examples of types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- test: Test additions/updates
- refactor: Code refactoring
- chore: Build, dependencies
```

**Example Commit:**
```bash
git commit -m "feat: Add user validation and comprehensive tests

- Add @NotBlank validation to name field
- Add @Email validation to email field
- Create UserControllerTest with 20 test cases
- All tests pass: mvn clean test
- Closes #123"
```

### Creating Pull Requests

#### Using GitHub Web UI

1. Push your branch: `git push origin feature/your-feature`
2. Visit: https://github.com/bhsingh0/my-spring-rest-api
3. Click "Compare & pull request"
4. Fill PR template:

```markdown
## Description
Brief description of what this PR does

## Changes
- Change 1
- Change 2
- Change 3

## Testing
- [ ] All tests pass: `mvn clean test`
- [ ] Added new tests
- [ ] Manual testing completed

## Checklist
- [x] Code follows style guidelines
- [x] All tests pass
- [x] Documentation updated
- [ ] Breaking changes documented
```

#### Using GitHub CLI

```bash
gh pr create --base main --head feature/your-feature \
  --title "Add email validation to User entity" \
  --body "Adds validation annotations and comprehensive tests"
```

### Code Review Process

**During Review:**
- ✅ Respond to feedback promptly
- ✅ Ask questions if unclear
- ✅ Make requested changes
- ✅ Request re-review

**After Approval:**
- ✅ Ensure all comments resolved
- ✅ All checks pass (tests, reviews)
- ✅ Merge to main branch
- ✅ Delete feature branch

```bash
# Delete local branch after merge
git branch -d feature/your-feature

# Delete remote branch
git push origin --delete feature/your-feature
```

---

## Automated Testing Workflow

### GitHub Actions Integration

The project uses GitHub Actions for automated testing and code review.

#### 1. Tests Workflow

**Trigger**: Every push and pull request

**Steps**:
1. Checkout code
2. Setup Java 17
3. Build with Maven
4. Run all tests
5. Report results

**View**: Go to "Actions" tab → "Tests" workflow

#### 2. Claude Code Review Workflow

**Trigger**: Pull requests

**Performs**:
- Automated code style review
- Spring Boot best practices check
- Test coverage analysis
- Security recommendations

**View**: Check "Checks" tab on your PR

### Monitoring Workflow Status

```bash
# List recent workflow runs
gh run list --workflow=tests.yml

# View specific run
gh run view <run-id>

# Watch workflow live
gh run watch
```

### Fixing Failed Workflows

```bash
# 1. Check error in GitHub Actions tab
# 2. Reproduce locally
mvn clean test

# 3. Fix the issue
# 4. Commit fix
git add .
git commit -m "fix: Address test failures"

# 5. Push changes
git push origin feature/your-feature

# 6. Verify workflow passes
```

---

## Troubleshooting & FAQ

### Common Issues and Solutions

#### Issue: Tests Fail Locally

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Run with verbose output
mvn test -X

# Check Java version
java -version  # Should be 17+

# Check Maven version
mvn -version   # Should be 3.8+
```

#### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
# server.port=8081
```

#### Issue: Maven Dependency Issues

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Reinstall dependencies
mvn clean install -U

# Check dependency tree
mvn dependency:tree
```

#### Issue: Git Conflicts

**Solution:**
```bash
# Update from main
git fetch origin
git rebase origin/main

# Or merge main
git merge origin/main

# Resolve conflicts in IDE
# Then commit merge
git add .
git commit -m "Resolve merge conflicts with main"
```

#### Issue: Cannot Connect to Database

**Solution:**
```bash
# Check application.properties
cat src/main/resources/application.properties

# H2 Console for debugging
# Visit: http://localhost:8080/h2-console

# Verify database URL:
# spring.datasource.url=jdbc:h2:mem:testdb
```

### FAQ

**Q: How do I run only one test?**
```bash
mvn test -Dtest=UserControllerTest#testCreateUser_WithValidData_SavesAndReturns201
```

**Q: How do I skip tests during build?**
```bash
mvn clean install -DskipTests
```

**Q: How do I generate test coverage report?**
```bash
mvn clean test jacoco:report
# Open: target/site/jacoco/index.html
```

**Q: What's the database for this project?**
H2 in-memory database (specified in application.properties)
For production, configure a real database (PostgreSQL, MySQL, etc.)

**Q: Can I use this project as a template?**
Yes! This project is designed as a reference implementation. Feel free to fork, clone, and modify.

**Q: How do I add a new endpoint?**
1. Add method to UserController
2. Add test cases to UserControllerTest
3. Run tests: `mvn clean test`
4. Ensure all tests pass
5. Create PR for review

**Q: How do I change the database?**
Edit `pom.xml` to add your database dependency
Update `application.properties` with connection details
Run `mvn clean install`

---

## Documentation

### Available Documentation

| Document | Purpose |
|----------|---------|
| **README.md** | Project overview and quick start (this file) |
| **API_DOCUMENTATION.md** | Complete API reference with examples |
| **WORKFLOW.md** | Development workflow and best practices |
| **CLAUDE.md** | Code style guidelines for Claude AI |

### Quick Links

- 📖 **Full API Documentation**: See [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- 🔄 **Development Workflow**: See [WORKFLOW.md](WORKFLOW.md)
- 💻 **Coding Guidelines**: See [CLAUDE.md](CLAUDE.md)

---

## Contributing

### How to Contribute

1. **Report Issues**: Create GitHub issue with details
2. **Propose Features**: Discuss in issues before implementing
3. **Submit PR**: Follow workflow, ensure tests pass
4. **Code Review**: Respond to feedback, make improvements

### Pull Request Guidelines

- ✅ All tests pass locally
- ✅ New features have test coverage
- ✅ Commit messages are descriptive
- ✅ No breaking changes without discussion
- ✅ Documentation updated if needed

### Development Standards

Refer to:
- [CLAUDE.md](CLAUDE.md) - Coding standards
- [WORKFLOW.md](WORKFLOW.md) - Development process
- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - API standards

---

## Project Maintenance

### Current Status

- **Latest Version**: 1.0.0
- **Last Updated**: May 13, 2026
- **Test Coverage**: 20 tests, 100% pass rate
- **Endpoints Tested**: 5/5 (100%)
- **Java Version**: 17+
- **Spring Boot**: 3.2.0

### Future Enhancements

- [ ] JWT authentication
- [ ] Role-based access control
- [ ] Advanced search and filtering
- [ ] Pagination support
- [ ] API rate limiting
- [ ] Docker containerization
- [ ] API versioning
- [ ] WebSocket support for real-time updates

---

## Support & Questions

### Getting Help

1. 📖 Check this README and other documentation
2. 🔍 Search existing GitHub issues
3. ❓ Create a new GitHub issue with details
4. 💬 Review test cases for usage examples

### Contact

- **Repository**: https://github.com/bhsingh0/my-spring-rest-api
- **Issues**: https://github.com/bhsingh0/my-spring-rest-api/issues
- **Wiki**: https://github.com/bhsingh0/my-spring-rest-api/wiki

---

## License

[Add your license here]

---

## Acknowledgments

- Spring Boot team for excellent framework
- Java community for best practices
- Contributors and reviewers

---

**Made with ❤️ by the Development Team**

Last Updated: May 13, 2026 | Version 1.0.0
