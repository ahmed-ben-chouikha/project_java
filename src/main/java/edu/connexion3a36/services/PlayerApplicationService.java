package edu.connexion3a36.services;

import edu.connexion3a36.entities.PlayerApplication;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlayerApplicationService {

    public boolean playerExists(int userId) throws SQLException {
        String sql = "SELECT 1 FROM player WHERE id = ? LIMIT 1";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void createApplication(String nickname,
                                  String firstName,
                                  String lastName,
                                  LocalDate birthDate,
                                  String role,
                                  int loggedInUserId) throws SQLException {
        if (loggedInUserId <= 0) {
            throw new SQLException("No logged-in user id available.");
        }

        String sql = "INSERT INTO player (id, nickname, first_name, last_name, birth_date, role, created_at, updated_at, team_id, player_status) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), NULL, 'pending')";

        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, loggedInUserId);
            ps.setString(2, normalizeText(nickname));
            ps.setString(3, normalizeText(firstName));
            ps.setString(4, normalizeText(lastName));
            if (birthDate != null) {
                ps.setDate(5, Date.valueOf(birthDate));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, normalizeText(role));
            ps.executeUpdate();
        }
    }

    public List<PlayerApplication> getPendingApplications() throws SQLException {
        return getApplicationsByStatus("pending");
    }

    public List<PlayerApplication> getApplicationsByStatus(String status) throws SQLException {
        String sql = "SELECT id, nickname, first_name, last_name, birth_date, role, created_at, updated_at, team_id, player_status "
                + "FROM player WHERE player_status = ? ORDER BY created_at DESC, id DESC";
        Connection cnx = MyConnection.getInstance().getCnx();
        List<PlayerApplication> applications = new ArrayList<>();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                applications.add(mapRow(rs));
            }
            }
        }
        return applications;
    }

    public int countApplicationsByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM player WHERE player_status = ?";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public void approveApplication(int playerId) throws SQLException {
        updateApplicationStatus(playerId, "approved");
    }

    public void rejectApplication(int playerId) throws SQLException {
        updateApplicationStatus(playerId, "rejected");
    }

    public boolean hasPendingApplication(int userId) throws SQLException {
        String sql = "SELECT 1 FROM player WHERE id = ? AND player_status = 'pending' LIMIT 1";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isApprovedPlayer(int userId) throws SQLException {
        String sql = "SELECT 1 FROM player WHERE id = ? AND player_status = 'approved' LIMIT 1";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private void updateApplicationStatus(int playerId, String status) throws SQLException {
        String sql = "UPDATE player SET player_status = ?, updated_at = NOW() WHERE id = ?";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, playerId);
            ps.executeUpdate();
        }
    }

    private PlayerApplication mapRow(ResultSet rs) throws SQLException {
        return new PlayerApplication(
                rs.getInt("id"),
                rs.getString("nickname"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("birth_date"),
                rs.getString("role"),
                rs.getString("created_at"),
                rs.getString("updated_at"),
                rs.getObject("team_id") == null ? null : rs.getInt("team_id"),
                rs.getString("player_status")
        );
    }
}
