# RankUp E-Sports API Documentation

## Token-Based Authentication API

This document describes the REST API endpoints with JWT token-based authentication.

### Base URL
```
http://localhost:8080/api
```

### Authentication
All endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

Tokens expire after 24 hours.

---

## Endpoints

### 1. Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "user@example.com",
    "username": "username",
    "role": "player",
    "expiresIn": 86400
  }
}
```

**Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "code": 401
}
```

---

### 2. Register
**Endpoint:** `POST /auth/register`

**Description:** Create a new user account

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "securepass123",
  "username": "newuser"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Registration successful. Your account is pending approval.",
  "code": 201,
  "data": {
    "email": "newuser@example.com",
    "username": "newuser",
    "status": "pending"
  }
}
```

**Error Response (409 - Email exists):**
```json
{
  "success": false,
  "message": "Email already registered",
  "code": 409
}
```

---

### 3. Validate Token
**Endpoint:** `GET /auth/validate?token=<jwt_token>`

**Description:** Verify if a token is valid

**Query Parameters:**
- `token` (required): JWT token to validate

**Success Response (200):**
```json
{
  "valid": true,
  "code": 200,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "role": "player"
  }
}
```

**Error Response (401 - Invalid/Expired):**
```json
{
  "valid": false,
  "message": "Token is invalid or expired",
  "code": 401
}
```

---

### 4. Forgot Password
**Endpoint:** `POST /auth/forgot-password`

**Description:** Request password reset - sends OTP via email

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "OTP sent to your email",
  "code": 200
}
```

**Error Response (404 - User not found):**
```json
{
  "success": false,
  "message": "No account found with this email",
  "code": 404
}
```

---

### 5. Verify OTP
**Endpoint:** `POST /auth/verify-otp`

**Description:** Verify OTP for password reset

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "OTP verified",
  "code": 200
}
```

**Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid OTP",
  "code": 401
}
```

---

### 6. Reset Password
**Endpoint:** `POST /auth/reset-password`

**Description:** Reset password after OTP verification

**Request Body:**
```json
{
  "email": "user@example.com",
  "newPassword": "newpassword123"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "code": 200
}
```

---

## Error Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 500 | Server Error |

---

## Authentication Example (JavaScript)

```javascript
// Login and get token
fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123'
  })
})
.then(res => res.json())
.then(data => {
  const token = data.data.token;
  // Store token in localStorage
  localStorage.setItem('token', token);
});

// Use token in subsequent requests
fetch('/api/auth/validate?token=' + localStorage.getItem('token'))
  .then(res => res.json())
  .then(data => console.log(data));
```

---

## Email Service Setup

### Gmail Configuration

1. Enable "2-Step Verification" on your Google Account
2. Generate an "App Password":
   - Go to https://myaccount.google.com/apppasswords
   - Select "Mail" and "Windows Computer"
   - Copy the generated password

3. Update `EmailService.java`:
   ```java
   private static final String SENDER_EMAIL = "your-email@gmail.com";
   private static final String SENDER_PASSWORD = "your-app-password";
   ```

### Alternative Email Providers

For other email providers, update the SMTP settings:
- **Outlook:** smtp.outlook.com:587
- **Yahoo:** smtp.mail.yahoo.com:587
- **Custom Server:** your-server.com:port

---

## Implementation Notes

- Tokens are valid for 24 hours
- OTPs expire after 15 minutes
- Passwords must be at least 6 characters
- All emails are sent with HTML formatting
- Database stores hashed passwords using bcrypt

