package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.UserAccount;
import edu.connexion3a36.tools.MyConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final Connection cnx;

    public AuthService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    public UserAccount login(String identifier, String password) throws SQLException {
        String sql = "SELECT id, email, username, roles, password, first_name, last_name, typeuser, approval_status " +
                "FROM `user` WHERE email = ? OR username = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            String normalized = identifier.trim().toLowerCase();
            pst.setString(1, normalized);
            pst.setString(2, normalized);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (checkPassword(password, storedHash)) {
                        UserAccount user = new UserAccount();
                        user.setId(rs.getInt("id"));
                        user.setFullName(buildFullName(rs.getString("first_name"), rs.getString("last_name")));
                        user.setEmail(rs.getString("email"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(storedHash);
                        user.setRole(extractRole(rs.getString("roles"), rs.getString("typeuser")));
                        return user;
                    }
                }
            }
        }
        return null;
    }

    public boolean registerPlayer(String fullName, String email, String password) throws SQLException {
        if (emailExists(email)) {
            return false;
        }

        String firstName = extractFirstName(fullName);
        String lastName = extractLastName(fullName);
        String username = buildUniqueUsername(email);
        String sql = "INSERT INTO `user` (email, username, roles, password, typeuser, confirmation_file, discr, first_name, last_name, birth_date, approval_status) " +
                "VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, NULL, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, email.trim().toLowerCase());
            pst.setString(2, username);
            pst.setString(3, "[\"ROLE_USER\"]");
            pst.setString(4, hashPassword(password));
            pst.setString(5, "user");
            pst.setString(6, "user");
            pst.setString(7, firstName);
            pst.setString(8, lastName);
            pst.setString(9, "approved");
            return pst.executeUpdate() > 0;
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM `user` WHERE email = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM `user` WHERE username = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String buildUniqueUsername(String email) throws SQLException {
        String local = email.split("@")[0].replaceAll("[^a-zA-Z0-9._-]", "").toLowerCase();
        if (local.isBlank()) {
            local = "player";
        }
        String candidate = local;
        int suffix = 1;
        while (usernameExists(candidate)) {
            candidate = local + suffix;
            suffix++;
        }
        return candidate;
    }

    private String hashPassword(String password) {
        String javaHash = BCrypt.hashpw(password, BCrypt.gensalt(13));
        // Stockage compatible avec les hashes Symfony/PHP observes ($2y$)
        return javaHash.replaceFirst("^\\$2a\\$", "\\$2y\\$");
    }

    private boolean checkPassword(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        String normalized = storedHash.replaceFirst("^\\$2y\\$", "\\$2a\\$");
        return BCrypt.checkpw(rawPassword, normalized);
    }

    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "user";
    }

    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length <= 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (i > 1) {
                sb.append(' ');
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    private String buildFullName(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();
        return (first + " " + last).trim();
    }

    private String extractRole(String rolesJson, String typeUser) {
        if (rolesJson != null && rolesJson.contains("ROLE_ADMIN")) {
            return "ADMIN";
        }
        if (rolesJson != null && rolesJson.contains("ROLE_USER")) {
            return "PLAYER";
        }
        return typeUser == null ? "PLAYER" : typeUser.toUpperCase();
    }
}


