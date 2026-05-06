package edu.connexion3a36.services;

import edu.connexion3a36.entities.Personne;
import edu.connexion3a36.entities.Team;
import edu.connexion3a36.entities.TeamInvitation;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeamInvitationService {

    public TeamInvitationService() {
        ensureSchema();
    }

    public int invitePlayers(int teamId, List<Integer> playerIds, int invitedById, String message) throws SQLException {
        if (teamId <= 0) {
            throw new SQLException("Invalid team id.");
        }
        if (playerIds == null || playerIds.isEmpty()) {
            throw new SQLException("No players selected.");
        }

        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        int inserted = 0;
        String sql = "INSERT INTO team_invitations (team_id, player_id, invited_by_id, status, message, created_at) VALUES (?, ?, ?, 'pending', ?, NOW())";
        
        // Get team info and player info
        TeamService teamService = new TeamService();
        PersonneService personneService = new PersonneService();
        
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            for (Integer playerId : playerIds) {
                if (playerId == null || playerId <= 0) {
                    continue;
                }
                // Check if invitation already exists
                if (invitationExists(teamId, playerId)) {
                    continue;
                }
                pst.setInt(1, teamId);
                pst.setInt(2, playerId);
                pst.setInt(3, invitedById);
                pst.setString(4, message);
                pst.addBatch();
            }
            int[] results = pst.executeBatch();
            for (int result : results) {
                if (result >= 0) {
                    inserted += result == Statement.SUCCESS_NO_INFO ? 1 : result;
                }
            }
        }
        
        // Send emails asynchronously after insertion succeeds
        if (inserted > 0) {
            new Thread(() -> {
                try {
                    Team team = teamService.getTeamById(teamId);
                    for (Integer playerId : playerIds) {
                        if (playerId == null || playerId <= 0) continue;
                        try {
                            String playerEmail = getPlayerEmailById(playerId);
                            String playerName = getPlayerNameById(playerId);
                            if (playerEmail != null && !playerEmail.isEmpty()) {
                                String subject = "Team Invitation - " + (team != null ? team.getName() : "RankUp Team");
                                String htmlContent = buildInvitationEmailHtml(playerName, team != null ? team.getName() : "Unknown Team", message);
                                String textContent = buildInvitationEmailText(playerName, team != null ? team.getName() : "Unknown Team", message);
                                BrevoMailService.sendEmail(playerEmail, subject, htmlContent, textContent);
                            }
                        } catch (Exception e) {
                            System.err.println("Could not send invitation email to player " + playerId + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error sending invitation emails: " + e.getMessage());
                }
            }).start();
        }
        
        return inserted;
    }

    private boolean invitationExists(int teamId, int playerId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            return false;
        }

        String sql = "SELECT 1 FROM team_invitations WHERE team_id = ? AND player_id = ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            pst.setInt(2, playerId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<TeamInvitation> getInvitationsForTeam(int teamId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        List<TeamInvitation> invitations = new ArrayList<>();
        String sql = "SELECT id, team_id, player_id, invited_by_id, status, message, created_at FROM team_invitations WHERE team_id = ? ORDER BY created_at DESC, id DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    invitations.add(new TeamInvitation(
                            rs.getInt("id"),
                            rs.getInt("team_id"),
                            rs.getInt("player_id"),
                            rs.getInt("invited_by_id"),
                            rs.getString("status"),
                            rs.getString("message"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return invitations;
    }

    public List<TeamInvitation> getPendingInvitationsForPlayer(int playerId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        List<TeamInvitation> invitations = new ArrayList<>();
        String sql = "SELECT id, team_id, player_id, invited_by_id, status, message, created_at FROM team_invitations WHERE player_id = ? AND status = 'pending' ORDER BY created_at DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, playerId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    invitations.add(new TeamInvitation(
                            rs.getInt("id"),
                            rs.getInt("team_id"),
                            rs.getInt("player_id"),
                            rs.getInt("invited_by_id"),
                            rs.getString("status"),
                            rs.getString("message"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return invitations;
    }

    public void updateInvitationStatus(int invitationId, String newStatus) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        String sql = "UPDATE team_invitations SET status = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, newStatus);
            pst.setInt(2, invitationId);
            pst.executeUpdate();
        }
    }

    public void acceptInvitation(int invitationId, int playerId, int teamId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available.");
        }

        try {
            // Update invitation status
            updateInvitationStatus(invitationId, "accepted");

            // Add player to team
            String sql = "UPDATE player SET team_id = ? WHERE id = ?";
            try (PreparedStatement pst = cnx.prepareStatement(sql)) {
                pst.setInt(1, teamId);
                pst.setInt(2, playerId);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLException("Could not accept invitation: " + e.getMessage(), e);
        }
    }

    public void rejectInvitation(int invitationId) throws SQLException {
        updateInvitationStatus(invitationId, "rejected");
    }

    private String buildInvitationEmailHtml(String playerName, String teamName, String message) {
        return String.format(
                "<html><body style=\"font-family: Arial, sans-serif;\">" +
                "<h2>Team Invitation</h2>" +
                "<p>Hello <strong>%s</strong>,</p>" +
                "<p>You have been invited to join <strong>%s</strong>!</p>" +
                "<p><em>Message from the team manager:</em></p>" +
                "<blockquote style=\"background-color: #f0f0f0; padding: 10px; border-left: 4px solid #007bff;\">%s</blockquote>" +
                "<p><a href=\"#\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">View Invitation</a></p>" +
                "<p>Best regards,<br/>RankUp Team</p>" +
                "</body></html>",
                htmlEscape(playerName), htmlEscape(teamName), htmlEscape(message != null ? message : "")
        );
    }

    private String buildInvitationEmailText(String playerName, String teamName, String message) {
        return String.format(
                "Team Invitation\n\n" +
                "Hello %s,\n\n" +
                "You have been invited to join %s!\n\n" +
                "Message from the team manager:\n" +
                "%s\n\n" +
                "Best regards,\n" +
                "RankUp Team",
                playerName, teamName, message != null ? message : ""
        );
    }

    private String htmlEscape(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }

    private String getPlayerEmailById(int playerId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) return null;

        String sql = "SELECT email FROM `user` WHERE id = ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, playerId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        }
        return null;
    }

    private String getPlayerNameById(int playerId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) return "Player";

        String sql = "SELECT nickname, first_name, last_name FROM player WHERE id = ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, playerId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String nickname = rs.getString("nickname");
                    if (nickname != null && !nickname.isEmpty()) {
                        return nickname;
                    }
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String fullName = ((firstName == null ? "" : firstName.trim()) + " " + (lastName == null ? "" : lastName.trim())).trim();
                    return fullName.isEmpty() ? "Player" : fullName;
                }
            }
        }
        return "Player";
    }

    private void ensureSchema() {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS team_invitations ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "team_id INT NOT NULL,"
                + "player_id INT NOT NULL,"
                + "invited_by_id INT NOT NULL,"
                + "status VARCHAR(30) NOT NULL DEFAULT 'pending',"
                + "message TEXT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "UNIQUE KEY unique_team_player_invite (team_id, player_id),"
                + "INDEX idx_team_invites_team (team_id),"
                + "INDEX idx_team_invites_player (player_id)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try (Statement st = cnx.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Could not ensure team_invitations schema: " + e.getMessage());
        }
    }
}
