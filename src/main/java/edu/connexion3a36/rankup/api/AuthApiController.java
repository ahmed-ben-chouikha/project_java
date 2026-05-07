package edu.connexion3a36.rankup.api;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.services.UserService;
import edu.connexion3a36.tools.JwtTokenGenerator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for User Authentication
 * Provides token-based authentication endpoints
 */
public class AuthApiController {
    
    private final UserService userService = new UserService();
    
    /**
     * Login endpoint - returns JWT token
     * Request: POST /api/auth/login
     * Body: { "email": "user@example.com", "password": "password123" }
     */
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate input
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                response.put("success", false);
                response.put("message", "Email and password are required");
                response.put("code", 400);
                return response;
            }
            
            // Authenticate user
            User user = userService.authenticate(email, password);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                response.put("code", 401);
                return response;
            }
            
            // Check if user is banned
            if ("BANNED".equals(user.getStatus())) {
                response.put("success", false);
                response.put("message", "Your account has been banned");
                response.put("code", 403);
                return response;
            }
            
            // Generate JWT token
            String token = JwtTokenGenerator.generateToken(user.getId(), user.getEmail(), user.getRole());
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("code", 200);
            response.put("data", new HashMap<String, Object>() {{
                put("token", token);
                put("userId", user.getId());
                put("email", user.getEmail());
                put("username", user.getUsername());
                put("role", user.getRole());
                put("expiresIn", 24 * 60 * 60); // 24 hours in seconds
            }});
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Server error: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
    
    /**
     * Register endpoint
     * Request: POST /api/auth/register
     * Body: { "email": "user@example.com", "password": "password123", "username": "username" }
     */
    public Map<String, Object> register(String email, String password, String username) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate input
            if (email == null || email.isBlank() || password == null || password.isBlank() || username == null || username.isBlank()) {
                response.put("success", false);
                response.put("message", "All fields are required");
                response.put("code", 400);
                return response;
            }
            
            if (password.length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                response.put("code", 400);
                return response;
            }
            
            // Check if email exists
            if (userService.emailExists(email)) {
                response.put("success", false);
                response.put("message", "Email already registered");
                response.put("code", 409);
                return response;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername(username);
            newUser.setRole("player");
            newUser.setStatus("pending");
            userService.createUser(newUser);
            
            response.put("success", true);
            response.put("message", "Registration successful. Your account is pending approval.");
            response.put("code", 201);
            response.put("data", new HashMap<String, Object>() {{
                put("email", newUser.getEmail());
                put("username", newUser.getUsername());
                put("status", "pending");
            }});
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
    
    /**
     * Validate token endpoint
     * Request: GET /api/auth/validate?token=jwt_token_here
     */
    public Map<String, Object> validateToken(String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (token == null || token.isBlank()) {
                response.put("valid", false);
                response.put("message", "Token is required");
                response.put("code", 400);
                return response;
            }
            
            if (!JwtTokenGenerator.validateToken(token)) {
                response.put("valid", false);
                response.put("message", "Token is invalid or expired");
                response.put("code", 401);
                return response;
            }
            
            Integer userId = JwtTokenGenerator.extractUserId(token);
            String email = JwtTokenGenerator.extractEmail(token);
            String role = JwtTokenGenerator.extractRole(token);
            
            response.put("valid", true);
            response.put("code", 200);
            response.put("data", new HashMap<String, Object>() {{
                put("userId", userId);
                put("email", email);
                put("role", role);
            }});
            
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Error validating token: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
    
    /**
     * Forgot password endpoint
     * Request: POST /api/auth/forgot-password
     * Body: { "email": "user@example.com" }
     */
    public Map<String, Object> forgotPassword(String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (email == null || email.isBlank()) {
                response.put("success", false);
                response.put("message", "Email is required");
                response.put("code", 400);
                return response;
            }
            
            User userFound = userService.getUserByEmail(email);
            if (userFound == null) {
                response.put("success", false);
                response.put("message", "No account found with this email");
                response.put("code", 404);
                return response;
            }
            
            response.put("success", true);
            response.put("message", "OTP sent to your email");
            response.put("code", 200);
            
        } catch (SQLException e) {
            if ("User not found".equals(e.getMessage())) {
                response.put("success", false);
                response.put("message", "No account found with this email");
                response.put("code", 404);
            } else {
                response.put("success", false);
                response.put("message", "Error: " + e.getMessage());
                response.put("code", 500);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Server error: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
    
    /**
     * Verify OTP endpoint
     * Request: POST /api/auth/verify-otp
     * Body: { "email": "user@example.com", "otp": "123456" }
     */
    public Map<String, Object> verifyOtp(String email, String otp) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
                response.put("success", false);
                response.put("message", "Email and OTP are required");
                response.put("code", 400);
                return response;
            }
            
            boolean isValid = false; // TODO: implement OTP verification
            
            if (isValid) {
                response.put("success", true);
                response.put("message", "OTP verified");
                response.put("code", 200);
            } else {
                response.put("success", false);
                response.put("message", "Invalid OTP");
                response.put("code", 401);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
    
    /**
     * Reset password endpoint
     * Request: POST /api/auth/reset-password
     * Body: { "email": "user@example.com", "newPassword": "newpass123" }
     */
    public Map<String, Object> resetPassword(String email, String newPassword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (email == null || email.isBlank() || newPassword == null || newPassword.isBlank()) {
                response.put("success", false);
                response.put("message", "Email and new password are required");
                response.put("code", 400);
                return response;
            }
            
            if (newPassword.length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                response.put("code", 400);
                return response;
            }
            
            User userToReset = userService.getUserByEmail(email);
            if (userToReset == null) throw new SQLException("User not found");
            userService.changePassword(userToReset.getId(), newPassword);
            
            response.put("success", true);
            response.put("message", "Password reset successfully");
            response.put("code", 200);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("code", 500);
        }
        
        return response;
    }
}






