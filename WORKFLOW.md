# Spring Boot REST API - Development Workflow Guide

Welcome to the my-spring-rest-api project! This guide provides comprehensive instructions for setting up your development environment, contributing code, and following our development processes.

## Table of Contents

1. [Project Setup Instructions](#project-setup-instructions)
2. [Local Development Workflow](#local-development-workflow)
3. [Testing Guide](#testing-guide)
4. [Git Workflow](#git-workflow)
5. [Code Review Process](#code-review-process)
6. [GitHub Actions Overview](#github-actions-overview)
7. [Troubleshooting](#troubleshooting)

---

## Project Setup Instructions

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+**: Download from [java.com](https://www.java.com/en/download/)
- **Maven 3.8+**: Download from [maven.apache.org](https://maven.apache.org/download.cgi)
- **Git 2.0+**: Download from [git-scm.com](https://git-scm.com/)
- **GitHub CLI (optional but recommended)**: Download from [cli.github.com](https://cli.github.com/)

Verify installations:

```bash
java -version        # Should show Java 17 or higher
mvn -version         # Should show Maven 3.8+
git --version        # Should show Git 2.0+
gh --version         # GitHub CLI (optional)
```

### Clone the Repository

```bash
# Clone the repository
git clone https://github.com/bhsingh0/my-spring-rest-api.git

# Navigate to project directory
cd my-spring-rest-api

# Verify project structure
ls -la
```

### Build the Project

```bash
# Clean and install dependencies
mvn clean install

# This will:
# - Clean previous build artifacts
# - Resolve all Maven dependencies
# - Compile the code
# - Run tests
# - Package the application
```

### Project Structure

```
my-spring-rest-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/api/
│   │   │   ├── Application.java          # Spring Boot entry point
│   │   │   ├── User.java                 # User entity with validation
│   │   │   ├── UserController.java       # REST controller
│   │   │   └── UserRepository.java       # Data access layer
│   │   └── resources/
│   │       └── application.properties    # Application configuration
│   └── test/
│       └── java/com/example/api/
│           └── UserControllerTest.java   # 20 comprehensive tests
├── pom.xml                               # Maven configuration
├── CLAUDE.md                             # Claude AI coding guidelines
├── WORKFLOW.md                           # This file
└── README.md                             # Project overview
```

---

## Local Development Workflow

### Starting the Application

#### Method 1: Using Maven

```bash
# Run the Spring Boot application
mvn spring-boot:run

# Output should show:
# Started Application in X.XXX seconds
# Tomcat started on port(s): 8080
```

#### Method 2: Building and Running JAR

```bash
# Build the application
mvn clean package

# Run the generated JAR
java -jar target/my-spring-app-1.0.0.jar

# Access the API at http://localhost:8080
```

### Testing API Endpoints

Once the application is running, you can test the REST API:

```bash
# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","phone":"123-456-7890"}'

# Update a user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","phone":"098-765-4321"}'

# Delete a user
curl -X DELETE http://localhost:8080/api/users/1
```

### IDE Setup

#### IntelliJ IDEA / VS Code

1. Open the project folder in your IDE
2. IDE should automatically detect Maven configuration
3. Dependencies will be downloaded automatically
4. Right-click on `Application.java` and select "Run"

#### VS Code Specific

1. Install these extensions:
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (Pivotal)
   - Maven for Java (Microsoft)

2. Press `Ctrl+Shift+D` (Debug) and select "Java" to run the application

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration (H2 for development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# H2 Console (for development/testing)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Access H2 Console at: `http://localhost:8080/h2-console`

---

## Testing Guide

### Running Tests Locally

#### Run All Tests

```bash
# Run all tests
mvn clean test

# Output should show all tests passing
```

#### Run Specific Test Class

```bash
# Run only UserControllerTest
mvn test -Dtest=UserControllerTest

# Expected output: Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
```

#### Run Specific Test Method

```bash
# Run a single test method
mvn test -Dtest=UserControllerTest#testGetAllUsers_ReturnsCompleteList
```

### Test Coverage Report

```bash
# Generate test coverage report with JaCoCo
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

### Understanding Test Structure

All tests are located in `src/test/java/com/example/api/UserControllerTest.java`

**Test Categories:**

1. **GET /api/users (2 tests)**
   - `testGetAllUsers_ReturnsCompleteList` - Verify list endpoint returns all users
   - `testGetAllUsers_WithNoUsers_ReturnsEmptyList` - Verify empty list handling

2. **GET /api/users/{id} (3 tests)**
   - `testGetUserById_WithValidId_ReturnsUser` - Successful retrieval
   - `testGetUserById_WithInvalidId_ReturnsNotFound` - 404 handling
   - `testGetUserById_WithNonExistentId_ReturnsNotFound` - Edge case

3. **POST /api/users (6 tests)**
   - `testCreateUser_WithValidData_SavesAndReturns201` - Happy path
   - `testCreateUser_WithoutPhone_SavesSuccessfully` - Optional field
   - `testCreateUser_WithMissingName_ReturnsBadRequest` - Validation
   - `testCreateUser_WithMissingEmail_ReturnsBadRequest` - Validation
   - `testCreateUser_WithEmptyBody_ReturnsBadRequest` - Empty payload
   - `testCreateUser_WithNullName_ReturnsBadRequest` - Null values

4. **PUT /api/users/{id} (4 tests)**
   - `testUpdateUser_WithValidId_UpdatesSuccessfully` - Happy path
   - `testUpdateUser_WithInvalidId_ReturnsNotFound` - 404 handling
   - `testUpdateUser_WithPartialData_UpdatesAllFields` - Partial update
   - `testUpdateUser_WithInvalidData_ReturnsBadRequest` - Validation

5. **DELETE /api/users/{id} (5 tests)**
   - `testDeleteUser_WithValidId_RemovesUser` - Successful deletion
   - `testDeleteUser_WithInvalidId_ReturnsNotFound` - 404 handling
   - `testDeleteUser_WithNonExistentId_ReturnsNotFound` - Edge case
   - `testDeleteUser_MultipleUsers_DeletesSuccessfully` - Sequential deletion

### Test Framework Details

- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **HTTP Testing**: MockMvc
- **Assertion Library**: Hamcrest

### Best Practices for Writing Tests

When adding new tests, follow these patterns:

```java
@Test
@DisplayName("Descriptive test name - Action_Scenario_ExpectedResult")
void testMethodName_Scenario_ExpectedBehavior() throws Exception {
    // ARRANGE: Set up test data and mock expectations
    User testUser = new User(1L, "John Doe", "john@example.com", "123-456-7890");
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    // ACT: Perform the action
    mockMvc.perform(get("/api/users/1")
            .contentType(MediaType.APPLICATION_JSON))
    
    // ASSERT: Verify the results
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("John Doe")));

    // VERIFY: Check mock interactions
    verify(userRepository, times(1)).findById(1L);
}
```

---

## Git Workflow

### Branching Strategy

We follow a Git Flow branching model:

- **`main`** - Production-ready code, always stable
- **`develop`** - Integration branch for features (if applicable)
- **`feature/*`** - Feature branches for new functionality
- **`bugfix/*`** - Bug fix branches
- **`hotfix/*`** - Emergency production fixes

### Creating a Feature Branch

```bash
# Update main branch
git checkout main
git pull origin main

# Create and checkout a feature branch
git checkout -b feature/add-user-tests

# Or: git checkout -b feature/your-feature-name
```

### Committing Code

#### Writing Good Commit Messages

```bash
# Format: <type>: <subject>
# 
# <body>
# 
# Examples:
git commit -m "feat: Add comprehensive UserController tests

- Created UserControllerTest.java with 20 test cases
- Added input validation to User entity
- Added @Valid annotations to controller methods"

# Commit types:
# feat:     New feature
# fix:      Bug fix
# docs:     Documentation changes
# style:    Code style changes (formatting, semicolons, etc.)
# refactor: Code refactoring without feature changes
# test:     Adding or updating tests
# chore:    Build tools, dependencies, etc.
```

#### Typical Workflow

```bash
# 1. Check current status
git status

# 2. Stage changes
git add src/main/java/com/example/api/User.java
git add src/test/java/com/example/api/UserControllerTest.java

# 3. Verify staged changes
git diff --cached

# 4. Commit with descriptive message
git commit -m "feat: Add input validation to User entity

- Add @NotBlank validation to name and email fields
- Add @Email validation to email field
- Update corresponding test cases"

# 5. Push to remote
git push origin feature/add-user-tests
```

### Syncing with Main Branch

```bash
# Fetch latest changes from remote
git fetch origin

# Rebase your branch on main (preferred)
git rebase origin/main

# Or merge main into your branch (alternative)
git merge origin/main
```

### Cleaning Up

```bash
# Delete local branch after PR is merged
git branch -d feature/add-user-tests

# Delete remote branch after PR is merged
git push origin --delete feature/add-user-tests

# Clean up stale remote branches
git fetch --prune origin
```

---

## Code Review Process

### Before Requesting a Code Review

1. **Ensure all tests pass locally**
   ```bash
   mvn clean test
   ```

2. **Run your specific tests**
   ```bash
   mvn test -Dtest=YourTestClass
   ```

3. **Check code style**
   - Follow Java naming conventions
   - Keep methods focused and small
   - Add meaningful comments for complex logic

4. **Verify your commits**
   ```bash
   git log --oneline -5
   ```

### Creating a Pull Request

#### Using GitHub Web UI

1. Push your feature branch: `git push origin feature/your-feature`
2. Go to: https://github.com/bhsingh0/my-spring-rest-api
3. Click "Compare & pull request"
4. Fill in the PR template:

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

## Related Issues
Closes #123 (if applicable)
```

#### Using GitHub CLI

```bash
gh pr create --base main --head feature/your-feature \
  --title "Clear title of the change" \
  --body "Detailed description of changes"
```

### During Code Review

- **Be responsive** to review comments
- **Don't take feedback personally** - it's about improving code quality
- **Ask questions** if feedback is unclear
- **Request re-review** after making changes

### After Approval

```bash
# Squash and merge (preferred for clean history)
# - Done through GitHub web interface
# - Or locally with: git merge --squash feature/your-feature

# Delete the feature branch
git branch -d feature/your-feature
git push origin --delete feature/your-feature
```

---

## GitHub Actions Overview

### Automated Workflows

Our repository includes GitHub Actions for automated testing and code review.

#### 1. **Tests Workflow** (`.github/workflows/tests.yml`)

- **Trigger**: On every push and pull request
- **Steps**:
  1. Checkout code
  2. Setup Java 17
  3. Build with Maven
  4. Run all tests
  5. Upload test results

**View Results**: Go to Actions tab → Tests workflow → latest run

#### 2. **Claude Code Review Workflow** (`.github/workflows/claude-review.yml`)

- **Trigger**: On pull requests
- **Performs**:
  1. Automated code style review
  2. Spring Boot best practices check
  3. Test coverage analysis
  4. Security review

**View Results**: Check the "Checks" tab on your PR

### Monitoring Workflow Status

```bash
# View workflow status locally
gh run list --workflow=tests.yml

# View specific run details
gh run view <run-id>

# Watch workflow in real-time
gh run watch
```

### Fixing Failed Workflows

If tests fail in GitHub Actions:

1. **Check the error message** in the Actions tab
2. **Reproduce locally**:
   ```bash
   mvn clean install
   mvn test
   ```
3. **Fix the issue** in your code
4. **Commit and push** the fix
5. **Verify** the workflow passes

---

## Troubleshooting

### Common Issues and Solutions

#### 1. **Tests Failing Locally**

```bash
# Clean and rebuild
mvn clean install

# Run specific failing test
mvn test -Dtest=UserControllerTest#testName

# Run with verbose output
mvn test -X

# Check dependencies
mvn dependency:tree
```

**Common Causes:**
- Outdated dependencies
- Java version mismatch
- Port already in use (change in `application.properties`)

#### 2. **Maven Build Errors**

```bash
# Clear local Maven cache
rm -rf ~/.m2/repository

# Reinstall dependencies
mvn clean install

# Update snapshots
mvn clean install -U
```

#### 3. **Git Conflicts**

```bash
# Check status
git status

# View conflicts
git diff

# Resolve conflicts manually in your IDE or editor

# After resolving
git add .
git commit -m "Resolve merge conflicts"
git push origin feature/branch-name
```

#### 4. **Port Already in Use**

```bash
# Change port in application.properties
server.port=8081

# Or kill process using port 8080
lsof -i :8080
kill -9 <PID>
```

#### 5. **Cannot Push to Remote**

```bash
# Ensure you have the latest changes
git fetch origin
git rebase origin/main

# Try pushing again
git push origin feature/branch-name

# If still issues, force push (use with caution!)
git push origin feature/branch-name --force-with-lease
```

#### 6. **Authentication Issues**

```bash
# For HTTPS
# - Use GitHub Personal Access Token instead of password
# - Or add SSH key

# For SSH
# - Generate SSH key: ssh-keygen -t ed25519
# - Add to GitHub: Settings → SSH and GPG keys
# - Test connection: ssh -T git@github.com

# Configure Git
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### Performance Tips

```bash
# Skip tests during build (use cautiously!)
mvn clean install -DskipTests

# Build only main code (no tests)
mvn clean compile

# Run only fast tests
mvn test -Dgroups=fast

# Use parallel test execution
mvn test -T 1C  # 1 thread per CPU core
```

### Useful Commands Reference

```bash
# View recent commits
git log --oneline -10

# View commit details
git show <commit-hash>

# See what's different from main
git diff main

# Stash uncommitted changes
git stash

# View stashed changes
git stash list

# Apply stashed changes
git stash pop

# Undo last commit (keep changes)
git reset --soft HEAD~1

# View all branches
git branch -a

# Search for commits
git log --grep="keyword" --oneline
```

---

## Getting Help

### Resources

- **Spring Boot Documentation**: https://spring.io/projects/spring-boot
- **Maven Documentation**: https://maven.apache.org/guides/
- **Git Documentation**: https://git-scm.com/doc
- **GitHub Docs**: https://docs.github.com

### Questions or Issues?

1. Check this documentation and other docs in the repo
2. Review closed issues on GitHub
3. Create a new GitHub issue with details
4. Reach out to the team

---

## Checklist for New Developers

- [ ] Java 17+ installed and verified
- [ ] Maven installed and verified
- [ ] Git configured with name and email
- [ ] Repository cloned locally
- [ ] Project builds successfully (`mvn clean install`)
- [ ] All tests pass (`mvn test`)
- [ ] Application runs locally (`mvn spring-boot:run`)
- [ ] Can access H2 Console at `http://localhost:8080/h2-console`
- [ ] Can test API endpoints with curl
- [ ] Created a feature branch
- [ ] Made a test commit and pushed
- [ ] Familiar with this workflow

---

**Last Updated**: May 13, 2026
**Version**: 1.0
