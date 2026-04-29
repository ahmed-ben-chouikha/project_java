package edu.connexion3a36.services;

import edu.connexion3a36.entities.User;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;

public class UserService {

    /**
     * Authenticate user by email and password
     */
    public User authenticate(String email, String password) throws SQLException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }

        String sql = "SELECT id, email, username, password, typeuser, approval_status FROM user WHERE email = ? AND approval_status != 'rejected'";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email.trim());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (verifyPassword(password, hashedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("typeuser"));
                    user.setStatus(rs.getString("approval_status"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT id, email, username, password, typeuser, approval_status FROM user WHERE id = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("typeuser"));
                user.setStatus(rs.getString("approval_status"));
                return user;
            }
        }
        return null;
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, username, password, typeuser, approval_status FROM user WHERE email = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email.trim());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("typeuser"));
                user.setStatus(rs.getString("approval_status"));
                return user;
            }
        }
        return null;
    }

    /**
     * Create new user
     */
    public boolean createUser(User user) throws SQLException {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            return false;
        }

        String sql = "INSERT INTO user (email, username, password, typeuser, approval_status, roles, discr) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getEmail().trim());
            pst.setString(2, user.getUsername() != null ? user.getUsername().trim() : user.getEmail().trim());
            pst.setString(3, hashPassword(user.getPassword())); // Hash the password
            String role = user.getRole() != null ? user.getRole().toLowerCase() : "player";
            pst.setString(4, role);
            String status = user.getStatus() != null ? user.getStatus().toLowerCase() : "pending";
            pst.setString(5, status);
            // Set roles as JSON array for Doctrine
            pst.setString(6, "[\"ROLE_" + role.toUpperCase() + "\"]");
            pst.setString(7, "user");
            
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Update user
     */
    public boolean updateUser(User user) throws SQLException {
        if (user == null || user.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE user SET email = ?, username = ?, typeuser = ?, approval_status = ? WHERE id = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user.getEmail().trim());
            pst.setString(2, user.getUsername());
            pst.setString(3, user.getRole());
            pst.setString(4, user.getStatus());
            pst.setInt(5, user.getId());
            
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Change user password
     */
    public boolean changePassword(int userId, String newPassword) throws SQLException {
        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }

        String sql = "UPDATE user SET password = ? WHERE id = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, hashPassword(newPassword));
            pst.setInt(2, userId);
            
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Delete user
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT id FROM user WHERE email = ?";
        
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, email.trim());
            ResultSet rs = pst.executeQuery();
            return rs.next();
        }
    }

    /**
     * Password hashing using BCrypt
     * Secure password hashing algorithm
     */
    private String hashPassword(String password) {
        if (password == null) return "";
        return edu.connexion3a36.tools.PasswordHashGenerator.hashPassword(password);
    }

    /**
     * Verify password against BCrypt hash
     */
    private boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        return edu.connexion3a36.tools.PasswordHashGenerator.verifyPassword(password, hash);
    }
}



