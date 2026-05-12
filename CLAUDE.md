# Claude Code Guidelines for Spring Boot REST API

## Project Setup
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Java Version**: 17+
- **Testing Framework**: JUnit 5 + Mockito
- **Database**: H2 (dev/test)

## Code Standards

### Java Naming Conventions
- Classes: PascalCase (e.g., `UserController`)
- Methods: camelCase (e.g., `getUserById`)
- Constants: UPPER_SNAKE_CASE
- Variables: camelCase
- Packages: lowercase (e.g., `com.example.api`)

### Spring Boot Patterns
- Controllers: Use `@RestController`, `@RequestMapping`
- Services: Business logic layer
- Repositories: Extend `JpaRepository`
- DTOs: Separate request/response objects from entities

### API Conventions
- Base path: `/api/{resource}`
- GET: Retrieve data
- POST: Create new resource (returns 201)
- PUT: Update entire resource
- DELETE: Remove resource (returns 204)

## Testing Standards

### Test File Structure
- Test classes: `{ClassName}Test`
- Location: `src/test/java/com/example/api/`
- Test naming: `test{MethodName}_{Scenario}_{ExpectedResult}`

### Test Examples
- testGetUserById_WithValidId_ReturnsUser
- testGetUserById_WithInvalidId_ReturnsNotFound
- testCreateUser_WithValidData_SavesAndReturns201
- testCreateUser_WithMissingEmail_ReturnsBadRequest
- testUpdateUser_WithValidId_UpdatesSuccessfully
- testDeleteUser_WithValidId_RemovesUser
- testGetAllUsers_ReturnsCompleteList

### Test Coverage Requirements
For EVERY endpoint, create tests for:
1. Happy Path - Valid input, success response
2. Invalid Input - Missing/wrong data, returns 400
3. Not Found - ID doesn't exist, returns 404
4. Edge Cases - Boundary values, null values
5. Duplicate Data - Unique constraint violations
6. HTTP Status Codes - Verify correct status

### Test Framework
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller unit tests
- Use `@MockBean` to mock repositories
- Use `Mockito` for stubbing

## Code Review Standards

### What Claude Reviews
1. **Style & Conventions** - Naming, formatting, imports
2. **Spring Boot Practices** - Controllers, Services, Repositories
3. **Database & JPA** - Entities, relationships, queries
4. **Testing** - Coverage, proper mocking, all tests pass
5. **Error Handling** - Exceptions caught, meaningful messages
6. **Security** - No secrets, no SQL injection, input validation

### How to Request Code Review
Post on PR:
@claude-review
Please review this PR for:

Code style and conventions
Spring Boot best practices
Test coverage
Error handling
Security issues

## Code Fix Standards

### How Claude Fixes Code
1. Read the code and identify issues
2. Fix the bugs/problems
3. Generate comprehensive tests
4. Run tests to verify fixes work
5. Commit changes
6. Create PR with fixes

### How to Request Code Fix
Create issue:

@claude
The UserController has these problems:

Missing null checks for user input
No custom error handling
Missing test cases

Please fix all issues and create a PR.

### How to Fix Failing Tests
Comment on PR:

@claude
The tests are failing. Can you:

Identify why tests are failing
Fix the code to make tests pass
Verify all tests pass
Push the fix to this PR


## Build & Test Commands

```bash
mvn clean install          # Build project
mvn test                   # Run all tests
mvn test -Dtest=UserControllerTest    # Run specific test class
mvn spring-boot:run        # Run application
```

## Security Restrictions
- Never add secrets to code
- Never modify workflows or CI/CD files
- Never install dependencies without asking
- Never push directly to main branch
- Always run tests before committing
- All tests must pass before creating PR

## Before Committing
1. Run: `mvn clean test`
2. Verify all tests pass
3. Check code follows patterns above
4. Run: `mvn clean install`
5. Create concise commit message
