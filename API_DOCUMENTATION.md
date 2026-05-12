# User REST API Documentation

## Table of Contents

1. [API Overview](#api-overview)
2. [Base URL and Endpoints](#base-url-and-endpoints)
3. [Authentication](#authentication)
4. [User Endpoint Specifications](#user-endpoint-specifications)
5. [Error Handling](#error-handling)
6. [Testing the API](#testing-the-api)
7. [Rate Limiting](#rate-limiting)
8. [Data Models](#data-models)

---

## API Overview

### Purpose

The User REST API provides complete CRUD (Create, Read, Update, Delete) operations for managing user data. The API follows RESTful conventions and returns JSON responses.

### Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (in-memory for development)
- **Build Tool**: Maven
- **Testing**: JUnit 5 + Mockito

### API Characteristics

- **Format**: JSON
- **Versioning**: API version 1 (implicit in base URL)
- **Response Format**: Consistent JSON structure
- **Error Format**: Standard error responses with status codes
- **Validation**: Input validation on all endpoints

---

## Base URL and Endpoints

### Development Server

```
http://localhost:8080
```

### Production Server

```
https://api.example.com  (replace with actual production URL)
```

### Available Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get a specific user |
| POST | `/api/users` | Create a new user |
| PUT | `/api/users/{id}` | Update a user |
| DELETE | `/api/users/{id}` | Delete a user |

---

## Authentication

### Current Implementation

The current version of the API does **not require authentication**. All endpoints are publicly accessible.

### Future Implementation

When authentication is implemented, it will likely use:

- **Method**: JWT (JSON Web Tokens) or OAuth 2.0
- **Header**: `Authorization: Bearer <token>`
- **Response**: 401 Unauthorized if token is missing or invalid

---

## User Endpoint Specifications

### 1. GET /api/users - List All Users

#### Overview
Retrieve a list of all users in the system.

#### Request

```
GET /api/users
```

**Headers:**
```
Content-Type: application/json
```

**Query Parameters:** None

**Request Body:** Not applicable

#### Response - Success (HTTP 200 OK)

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

#### Response - Empty List (HTTP 200 OK)

```json
[]
```

#### Response - Error (HTTP 500 Internal Server Error)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unable to retrieve users"
}
```

#### cURL Example

```bash
# Basic request
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json"

# With pretty JSON output
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json" | jq
```

#### Example Use Cases

- Display user list in a web application
- Generate reports on all users
- Populate dropdown menus
- System administration dashboards

---

### 2. GET /api/users/{id} - Get Single User

#### Overview
Retrieve a specific user by their ID.

#### Request

```
GET /api/users/{id}
```

**Parameters:**
- `id` (path parameter, required): The unique identifier of the user (integer)

**Headers:**
```
Content-Type: application/json
```

**Request Body:** Not applicable

#### Response - Success (HTTP 200 OK)

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "123-456-7890"
}
```

#### Response - User Not Found (HTTP 404 Not Found)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 999 not found"
}
```

#### Response - Invalid ID Format (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid user ID format"
}
```

#### cURL Examples

```bash
# Get user with ID 1
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"

# Get user with ID 5
curl -X GET http://localhost:8080/api/users/5

# Handle not found gracefully
curl -X GET http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"
```

#### Example Use Cases

- Display user profile page
- Fetch user details for editing
- Verify user exists before operations
- User profile view in applications

#### HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 200 | User found and returned |
| 404 | User not found |
| 400 | Invalid ID format |
| 500 | Server error |

---

### 3. POST /api/users - Create New User

#### Overview
Create a new user in the system. The API validates input data and assigns an auto-generated ID.

#### Request

```
POST /api/users
Content-Type: application/json
```

**Headers:**
```
Content-Type: application/json
```

**Request Body Schema:**

```json
{
  "name": "string (required, 1-255 characters)",
  "email": "string (required, valid email format)",
  "phone": "string (optional, any length)"
}
```

#### Valid Request Body Examples

**Complete data (with phone):**
```json
{
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "phone": "555-123-4567"
}
```

**Minimal data (without phone):**
```json
{
  "name": "Bob Wilson",
  "email": "bob@example.com"
}
```

**With phone as null:**
```json
{
  "name": "Charlie Brown",
  "email": "charlie@example.com",
  "phone": null
}
```

#### Response - Success (HTTP 201 Created)

```json
{
  "id": 3,
  "name": "Alice Johnson",
  "email": "alice@example.com",
  "phone": "555-123-4567"
}
```

**Headers:**
```
Content-Type: application/json
```

#### Response - Validation Error - Missing Name (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required"
  }
}
```

#### Response - Validation Error - Invalid Email (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid"
  }
}
```

#### Response - Validation Error - Multiple Issues (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "email": "Email is required"
  }
}
```

#### Response - Duplicate Email (HTTP 409 Conflict)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: john@example.com"
}
```

#### cURL Examples

```bash
# Create user with all fields
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "phone": "555-123-4567"
  }'

# Create user without phone
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Wilson",
    "email": "bob@example.com"
  }'

# Invalid request - missing email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Charlie Brown"
  }' \
  -w "\nStatus: %{http_code}\n"

# Create from file
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d @user.json

# Pretty print response
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "David Lee",
    "email": "david@example.com"
  }' | jq
```

#### Validation Rules

| Field | Rule | Example |
|-------|------|---------|
| name | Required, not blank, 1-255 chars | "John Doe" |
| email | Required, valid email format | "john@example.com" |
| phone | Optional, any length | "123-456-7890" or null |

#### HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 201 | User created successfully |
| 400 | Validation error (see errors field) |
| 409 | Email already exists |
| 500 | Server error |

---

### 4. PUT /api/users/{id} - Update User

#### Overview
Update an existing user's information. All fields must be provided.

#### Request

```
PUT /api/users/{id}
Content-Type: application/json
```

**Parameters:**
- `id` (path parameter, required): The user ID to update (integer)

**Headers:**
```
Content-Type: application/json
```

**Request Body Schema:**

```json
{
  "name": "string (required, 1-255 characters)",
  "email": "string (required, valid email format)",
  "phone": "string (optional, any length)"
}
```

#### Request Body Examples

**Update with all fields:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "phone": "999-999-9999"
}
```

**Update with null phone:**
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "phone": null
}
```

#### Response - Success (HTTP 200 OK)

```json
{
  "id": 1,
  "name": "John Updated",
  "email": "john.updated@example.com",
  "phone": "999-999-9999"
}
```

#### Response - User Not Found (HTTP 404 Not Found)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 999 not found"
}
```

#### Response - Validation Error (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required"
  }
}
```

#### Response - Invalid Email (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid"
  }
}
```

#### cURL Examples

```bash
# Update user with ID 1
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com",
    "phone": "999-999-9999"
  }'

# Update user removing phone (set to null)
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane@example.com",
    "phone": null
  }'

# Update non-existent user
curl -X PUT http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ghost User",
    "email": "ghost@example.com"
  }' \
  -w "\nStatus: %{http_code}\n"

# Update from file
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d @user-update.json | jq
```

#### HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 200 | User updated successfully |
| 400 | Validation error |
| 404 | User not found |
| 500 | Server error |

#### Important Notes

- All fields must be provided (no partial updates currently)
- Future implementation may support PATCH for partial updates
- Email must still be valid (unique constraint depends on database)
- Phone field can be set to null or empty string

---

### 5. DELETE /api/users/{id} - Delete User

#### Overview
Remove a user from the system. This operation is permanent and cannot be undone.

#### Request

```
DELETE /api/users/{id}
```

**Parameters:**
- `id` (path parameter, required): The user ID to delete (integer)

**Headers:**
```
Content-Type: application/json
```

**Request Body:** Not applicable

#### Response - Success (HTTP 204 No Content)

```
(Empty response body)
```

**Headers:**
```
Content-Type: application/json
```

#### Response - User Not Found (HTTP 404 Not Found)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 999 not found"
}
```

#### Response - Invalid ID (HTTP 400 Bad Request)

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid user ID format"
}
```

#### cURL Examples

```bash
# Delete user with ID 1
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"

# Delete with verbose output showing status
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -v

# Delete non-existent user
curl -X DELETE http://localhost:8080/api/users/999 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"

# Delete and show only HTTP status
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -o /dev/null \
  -w "%{http_code}\n"
```

#### HTTP Status Codes

| Status | Meaning |
|--------|---------|
| 204 | User deleted successfully (No Content) |
| 404 | User not found |
| 400 | Invalid ID format |
| 500 | Server error |

#### Important Notes

- **Permanent Operation**: Deletion cannot be reversed
- **No Response Body**: 204 response returns no content
- **Idempotency**: Deleting a non-existent user returns 404
- Consider implementing soft deletes for audit trails

---

## Error Handling

### Standard Error Response Format

All error responses follow this consistent structure:

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Error Type",
  "message": "Human-readable error message",
  "errors": {
    "field1": "Field-specific error message"
  }
}
```

### Error Response Fields

| Field | Description |
|-------|-------------|
| `timestamp` | ISO 8601 datetime of the error |
| `status` | HTTP status code |
| `error` | Error category name |
| `message` | General error description |
| `errors` | Object with field-specific validation errors |

### HTTP Status Codes Reference

| Code | Status | Meaning | Example |
|------|--------|---------|---------|
| 200 | OK | Request successful | GET, PUT successful |
| 201 | Created | Resource created | POST successful |
| 204 | No Content | Request successful, no content to return | DELETE successful |
| 400 | Bad Request | Invalid input or validation error | Missing required field |
| 404 | Not Found | Resource not found | User ID doesn't exist |
| 409 | Conflict | Resource conflict (e.g., duplicate email) | Email already exists |
| 500 | Internal Server Error | Server-side error | Database connection error |

### Common Validation Errors

#### Missing Required Fields

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "email": "Email is required"
  }
}
```

#### Invalid Email Format

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid"
  }
}
```

#### Blank Values

```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required"
  }
}
```

### Error Handling Best Practices

1. **Always check HTTP status codes**
   ```bash
   curl -X GET http://localhost:8080/api/users/999 \
     -w "\nStatus: %{http_code}\n"
   ```

2. **Parse error responses carefully**
   ```bash
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"name": ""}' | jq .errors
   ```

3. **Implement retry logic for 5xx errors**
   - 500, 502, 503: Temporary server issues, retry with backoff

4. **Validate input before sending requests**
   - Check required fields
   - Validate email format
   - Trim whitespace

---

## Testing the API

### Prerequisites

- Application running: `mvn spring-boot:run`
- Port available: `8080`
- cURL or Postman installed

### Quick Test Workflow

#### 1. Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "phone": "123-456-7890"
  }' | jq
```

Expected response with user ID 1 (or next available).

#### 2. Get All Users

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json" | jq
```

Should show the user you just created.

#### 3. Get Specific User

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" | jq
```

Should return the user with ID 1.

#### 4. Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated User",
    "email": "updated@example.com",
    "phone": "999-999-9999"
  }' | jq
```

Should show updated user data.

#### 5. Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"
```

Should return 204 No Content.

#### 6. Verify Deletion

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -w "\nStatus: %{http_code}\n"
```

Should return 404 Not Found.

### Testing with Postman

1. **Import API Collection** (optional):
   - Create new folder "User API"
   - Create requests for each endpoint

2. **Create Request Example**:
   - Method: POST
   - URL: `{{baseUrl}}/api/users`
   - Body (raw JSON):
     ```json
     {
       "name": "Postman User",
       "email": "postman@example.com",
       "phone": "111-222-3333"
     }
     ```

3. **Set Environment Variable**:
   - Key: `baseUrl`
   - Value: `http://localhost:8080`

### Testing with Script

Create `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing User API ==="

echo -e "\n1. Create user..."
USER_ID=$(curl -s -X POST $BASE_URL/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Script Test",
    "email": "script@example.com",
    "phone": "555-1234"
  }' | jq -r '.id')
echo "Created user with ID: $USER_ID"

echo -e "\n2. Get all users..."
curl -s -X GET $BASE_URL/api/users | jq

echo -e "\n3. Get specific user..."
curl -s -X GET $BASE_URL/api/users/$USER_ID | jq

echo -e "\n4. Update user..."
curl -s -X PUT $BASE_URL/api/users/$USER_ID \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Script Test",
    "email": "updated@example.com",
    "phone": "555-5678"
  }' | jq

echo -e "\n5. Delete user..."
curl -s -X DELETE $BASE_URL/api/users/$USER_ID -w "\nStatus: %{http_code}\n"

echo -e "\n=== Testing Complete ==="
```

Run with:
```bash
chmod +x test-api.sh
./test-api.sh
```

---

## Rate Limiting

### Current Implementation

- **No rate limiting** currently implemented
- All requests are processed immediately

### Future Implementation

When rate limiting is implemented:

```
Rate Limit: 1000 requests per minute per IP
```

**Response Headers:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1620000000
```

**Rate Limit Exceeded (HTTP 429):**
```json
{
  "timestamp": "2026-05-13T10:30:00+00:00",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again after 60 seconds."
}
```

---

## Data Models

### User Object

The User object represents a user in the system.

**User Schema:**

```json
{
  "id": "integer (auto-generated)",
  "name": "string (required, 1-255 characters)",
  "email": "string (required, valid email, unique)",
  "phone": "string (optional)"
}
```

**Complete Example:**

```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "123-456-7890"
}
```

**Example with Null Phone:**

```json
{
  "id": 2,
  "name": "Jane Smith",
  "email": "jane@example.com",
  "phone": null
}
```

### Field Descriptions

| Field | Type | Length | Required | Unique | Description |
|-------|------|--------|----------|--------|-------------|
| id | integer | N/A | Auto | Yes | Unique identifier, auto-generated |
| name | string | 1-255 | Yes | No | User's full name |
| email | string | N/A | Yes | Yes | User's email address |
| phone | string | N/A | No | No | User's phone number |

### Constraints

- **id**: Auto-generated, positive integer, immutable
- **name**: Non-blank, maximum 255 characters
- **email**: Valid email format (must match email regex), unique in database
- **phone**: Optional, any string value, can be null

---

## API Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-05-13 | Initial release with CRUD operations |

---

## Support & Questions

For issues or questions about the API:

1. Check this documentation
2. Review test cases in `UserControllerTest.java`
3. Check GitHub issues: https://github.com/bhsingh0/my-spring-rest-api/issues
4. Review CLAUDE.md for coding guidelines
5. Check WORKFLOW.md for development process

---

**Last Updated**: May 13, 2026
**API Version**: 1.0
**Documentation Version**: 1.0
