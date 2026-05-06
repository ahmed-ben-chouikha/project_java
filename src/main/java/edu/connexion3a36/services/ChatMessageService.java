package edu.connexion3a36.services;

import edu.connexion3a36.entities.ChatMessage;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageService {

    public ChatMessageService() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS team_chat_messages (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "team_id INT NOT NULL, " +
                     "user_id INT NOT NULL, " +
                     "message TEXT NOT NULL, " +
                     "username VARCHAR(100), " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "INDEX idx_team_id (team_id), " +
                     "INDEX idx_user_id (user_id), " +
                     "INDEX idx_created_at (created_at)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try {
            Connection conn = MyConnection.getInstance().getCnx();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.execute();
            }
        } catch (Exception e) {
            System.err.println("Failed to create team_chat_messages table: " + e.getMessage());
        }
    }
    public boolean saveMessage(ChatMessage message) throws SQLException {
        if (message == null || message.getMessage() == null || message.getMessage().isBlank()) {
            return false;
        }

        String sql = "INSERT INTO team_chat_messages (team_id, user_id, message, username) VALUES (?, ?, ?, ?)";

        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, message.getTeamId());
            pst.setInt(2, message.getUserId());
            pst.setString(3, message.getMessage().trim());
            pst.setString(4, message.getUsername());

            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Get all messages for a team, ordered by creation time
     */
    public List<ChatMessage> getMessagesByTeam(int teamId) throws SQLException {
        if (teamId <= 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT cm.id, cm.team_id, cm.user_id, cm.message, cm.created_at, cm.username " +
                     "FROM team_chat_messages cm " +
                     "WHERE cm.team_id = ? " +
                     "ORDER BY cm.created_at ASC";

        List<ChatMessage> messages = new ArrayList<>();
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ChatMessage msg = new ChatMessage();
                msg.setId(rs.getInt("id"));
                msg.setTeamId(rs.getInt("team_id"));
                msg.setUserId(rs.getInt("user_id"));
                msg.setMessage(rs.getString("message"));
                msg.setCreatedAt(rs.getString("created_at"));
                msg.setUsername(rs.getString("username"));
                messages.add(msg);
            }
        }
        return messages;
    }

    /**
     * Get recent messages for a team (last N messages)
     */
    public List<ChatMessage> getRecentMessagesByTeam(int teamId, int limit) throws SQLException {
        if (teamId <= 0 || limit <= 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT cm.id, cm.team_id, cm.user_id, cm.message, cm.created_at, cm.username " +
                     "FROM team_chat_messages cm " +
                     "WHERE cm.team_id = ? " +
                     "ORDER BY cm.created_at DESC " +
                     "LIMIT ?";

        List<ChatMessage> messages = new ArrayList<>();
        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            pst.setInt(2, limit);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ChatMessage msg = new ChatMessage();
                msg.setId(rs.getInt("id"));
                msg.setTeamId(rs.getInt("team_id"));
                msg.setUserId(rs.getInt("user_id"));
                msg.setMessage(rs.getString("message"));
                msg.setCreatedAt(rs.getString("created_at"));
                msg.setUsername(rs.getString("username"));
                messages.add(0, msg); // Add to front to reverse order
            }
        }
        return messages;
    }

    /**
     * Delete old messages (cleanup method)
     */
    public int deleteOldMessages(int daysOld) throws SQLException {
        if (daysOld <= 0) {
            return 0;
        }

        String sql = "DELETE FROM chat_messages WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";

        Connection conn = MyConnection.getInstance().getCnx();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, daysOld);
            return pst.executeUpdate();
        }
    }
}