package edu.connexion3a36.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating SHA-256 password hashes with salt
 * Used for test data setup and password hashing
 * 
 * Security: SHA-256 with random salt (16 bytes)
 * Format: salt$hash (both base64 encoded)
 */
public class PasswordHashGenerator {

    private static final int SALT_LENGTH = 16;

    /**
     * Generate SHA-256 hash of a password with random salt
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            String hash = hashWithSalt(password, salt);
            
            // Return salt$hash format
            return Base64.getEncoder().encodeToString(salt) + "$" + hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify a password against its hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = hashWithSalt(password, salt);
            
            return expectedHash.equals(parts[1]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | java.security.NoSuchAlgorithmException e) {
            return false;
        }
    }

    /**
     * Hash password with given salt
     */
    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * Generate SQL INSERT statement with hashed passwords
     */
    public static void main(String[] args) {
        System.out.println("=== SHA-256 Password Hash Generator (with Salt) ===\n");

        String[] testUsers = {
                "admin@esports.com:admin:admin123",
                "player@esports.com:player1:player123",
                "ahmed@esports.com:ahmed:ahmed123"
        };

        System.out.println("Password Hashes for test users:\n");
        for (String userInfo : testUsers) {
            String[] parts = userInfo.split(":");
            String email = parts[0];
            String username = parts[1];
            String password = parts[2];
            String hash = hashPassword(password);
            System.out.println("Email:    " + email);
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
        }

        System.out.println("\nSQL INSERT Statement:");
        System.out.println("INSERT IGNORE INTO users (email, username, password, role, status) VALUES");
        System.out.println("('admin@esports.com', 'admin', '" + hashPassword("admin123") + "', 'admin', 'active'),");
        System.out.println("('player@esports.com', 'player1', '" + hashPassword("player123") + "', 'player', 'active'),");
        System.out.println("('ahmed@esports.com', 'ahmed', '" + hashPassword("ahmed123") + "', 'player', 'active');");
    }
}

