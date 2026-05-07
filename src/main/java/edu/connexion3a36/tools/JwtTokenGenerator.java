package edu.connexion3a36.tools;

import java.util.*;

/**
 * JWT Token Generator for API Authentication
 * Generates and validates JWT tokens for stateless authentication
 */
public class JwtTokenGenerator {
    
    private static final String SECRET_KEY = "rankup-esports-secret-key-2026";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours
    
    /**
     * Generate a JWT token for a user
     * @param userId User ID
     * @param email User email
     * @param role User role
     * @return JWT token
     */
    public static String generateToken(int userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("iat", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + EXPIRATION_TIME);
        
        return encodeToken(claims);
    }
    
    /**
     * Validate a JWT token
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public static boolean validateToken(String token) {
        try {
            Map<String, Object> claims = decodeToken(token);
            if (claims == null) {
                return false;
            }
            
            long exp = ((Number) claims.get("exp")).longValue();
            return exp > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract user ID from token
     */
    public static Integer extractUserId(String token) {
        try {
            Map<String, Object> claims = decodeToken(token);
            if (claims != null && claims.containsKey("userId")) {
                return ((Number) claims.get("userId")).intValue();
            }
        } catch (Exception e) {
            // Silent fail
        }
        return null;
    }
    
    /**
     * Extract email from token
     */
    public static String extractEmail(String token) {
        try {
            Map<String, Object> claims = decodeToken(token);
            if (claims != null && claims.containsKey("email")) {
                return (String) claims.get("email");
            }
        } catch (Exception e) {
            // Silent fail
        }
        return null;
    }
    
    /**
     * Extract role from token
     */
    public static String extractRole(String token) {
        try {
            Map<String, Object> claims = decodeToken(token);
            if (claims != null && claims.containsKey("role")) {
                return (String) claims.get("role");
            }
        } catch (Exception e) {
            // Silent fail
        }
        return null;
    }
    
    /**
     * Simple encoding - in production, use a proper JWT library like jjwt
     */
    private static String encodeToken(Map<String, Object> claims) {
        // This is a simple implementation. In production, use proper JWT library
        StringBuilder sb = new StringBuilder();
        sb.append(Base64.getEncoder().encodeToString(claims.toString().getBytes()));
        sb.append(".");
        sb.append(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        return sb.toString();
    }
    
    /**
     * Simple decoding - matches the encoding logic
     */
    private static Map<String, Object> decodeToken(String token) {
        try {
            if (!token.contains(".")) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 2) {
                return null;
            }
            
            // Verify signature
            String decodedSecret = new String(Base64.getDecoder().decode(parts[1]));
            if (!decodedSecret.equals(SECRET_KEY)) {
                return null;
            }
            
            // Decode claims
            String decodedClaims = new String(Base64.getDecoder().decode(parts[0]));
            // Parse the claims string (simplified - would need proper JSON parsing in production)
            return parseClaims(decodedClaims);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parse claims from string (simplified version)
     */
    private static Map<String, Object> parseClaims(String claimsStr) {
        Map<String, Object> claims = new HashMap<>();
        // This is a simplified version. In production, use a JSON parser
        claimsStr = claimsStr.replace("{", "").replace("}", "");
        String[] pairs = claimsStr.split(", ");
        
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                
                // Try to parse as number
                try {
                    if (value.contains(".")) {
                        claims.put(key, Double.parseDouble(value));
                    } else {
                        claims.put(key, Long.parseLong(value));
                    }
                } catch (NumberFormatException e) {
                    // Parse as string, remove quotes if present
                    value = value.replaceAll("^\"|\"$", "");
                    claims.put(key, value);
                }
            }
        }
        
        return claims;
    }
}

