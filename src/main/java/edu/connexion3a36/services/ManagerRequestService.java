package edu.connexion3a36.services;

import edu.connexion3a36.entities.ManagerRequest;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerRequestService {

    public void createRequest(int loggedInUserId, String teamName, String motivation) throws SQLException {
        if (loggedInUserId <= 0) {
            throw new SQLException("No logged-in user id available.");
        }

        Connection cnx = MyConnection.getInstance().getCnx();

        String sql = "INSERT INTO manager_request (player_id, team_name, motivation, status, created_at) VALUES (?, ?, ?, 'pending', NOW())";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, loggedInUserId);
            ps.setString(2, teamName);
            ps.setString(3, motivation);
            ps.executeUpdate();
        }
    }

    public List<ManagerRequest> getPendingRequests() throws SQLException {
        return getRequestsByStatus("pending");
    }

    public List<ManagerRequest> getRequestsByStatus(String status) throws SQLException {
        String sql = "SELECT mr.id, mr.player_id, mr.reviewed_by_id, mr.team_name, mr.motivation, mr.status, mr.created_at, mr.reviewed_at, mr.admin_comment, "
                + "p.nickname AS player_nickname, p.first_name AS player_first_name, p.last_name AS player_last_name "
                + "FROM manager_request mr "
                + "LEFT JOIN player p ON p.id = mr.player_id "
                + "WHERE mr.status = ? ORDER BY mr.created_at DESC, mr.id DESC";
        Connection cnx = MyConnection.getInstance().getCnx();
        List<ManagerRequest> requests = new ArrayList<>();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
            }
        }
        return requests;
    }

    public int countRequestsByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM manager_request WHERE status = ?";
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

    public void approveRequest(int requestId, int reviewedById) throws SQLException {
        updateRequestStatus(requestId, reviewedById, "approved", null);
    }

    public void rejectRequest(int requestId, int reviewedById, String adminComment) throws SQLException {
        updateRequestStatus(requestId, reviewedById, "rejected", adminComment);
    }

    public boolean hasApprovedManagerRequest(int playerId) throws SQLException {
        String sql = "SELECT 1 FROM manager_request WHERE player_id = ? AND status = 'approved' LIMIT 1";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void updateRequestStatus(int requestId, int reviewedById, String status, String adminComment) throws SQLException {
        String sql = "UPDATE manager_request SET status = ?, reviewed_by_id = ?, reviewed_at = NOW(), admin_comment = ? WHERE id = ?";
        Connection cnx = MyConnection.getInstance().getCnx();
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewedById);
            if (adminComment == null || adminComment.isBlank()) {
                ps.setNull(3, java.sql.Types.LONGVARCHAR);
            } else {
                ps.setString(3, adminComment.trim());
            }
            ps.setInt(4, requestId);
            ps.executeUpdate();
        }
    }

    private ManagerRequest mapRow(ResultSet rs) throws SQLException {
        return new ManagerRequest(
                rs.getInt("id"),
                rs.getInt("player_id"),
                rs.getObject("reviewed_by_id") == null ? null : rs.getInt("reviewed_by_id"),
                rs.getString("team_name"),
                rs.getString("motivation"),
                rs.getString("status"),
                rs.getString("created_at"),
                rs.getString("reviewed_at"),
                rs.getString("admin_comment"),
                rs.getString("player_nickname"),
                rs.getString("player_first_name"),
                rs.getString("player_last_name")
        );
    }
}
