package edu.connexion3a36.services;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.interfaces.IService;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentService implements IService<Tournament> {

    @Override
    public void addEntity(Tournament tournament) throws SQLException {
        if (tournament == null || tournament.getName() == null ||
            tournament.getName().trim().isEmpty()) {
            throw new SQLException("Tournament name cannot be empty");
        }

        String query = "INSERT INTO tournament (name, description, start_date, end_date, status, location, prize_pool, rules, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, tournament.getName().trim());
        pst.setString(2, tournament.getDescription() != null ? tournament.getDescription().trim() : "");
        pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
        pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
        pst.setString(5, tournament.getStatus() != null ? tournament.getStatus() : "pending");
        pst.setString(6, tournament.getLocation() != null ? tournament.getLocation().trim() : "");
        pst.setDouble(7, tournament.getPrizePool());
        pst.setString(8, normalizeRulesJson(tournament.getRules()));

        pst.executeUpdate();
        System.out.println("Tournament added successfully");
    }

    @Override
    public void deleteEntity(Tournament tournament) throws SQLException {
        if (tournament == null || tournament.getId() <= 0) {
            throw new SQLException("Invalid tournament for deletion");
        }

        String query = "DELETE FROM tournament WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, tournament.getId());

        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Tournament deleted successfully");
        } else {
            throw new SQLException("Tournament not found");
        }
    }

    @Override
    public void updateEntity(int id, Tournament tournament) throws SQLException {
        if (tournament == null || tournament.getName() == null ||
            tournament.getName().trim().isEmpty()) {
            throw new SQLException("Tournament name cannot be empty");
        }

        String query = "UPDATE tournament SET name = ?, description = ?, start_date = ?, " +
                "end_date = ?, status = ?, location = ?, prize_pool = ?, rules = ?, updated_at = NOW() WHERE id = ?";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, tournament.getName().trim());
        pst.setString(2, tournament.getDescription() != null ? tournament.getDescription().trim() : "");
        pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
        pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
        pst.setString(5, tournament.getStatus() != null ? tournament.getStatus() : "pending");
        pst.setString(6, tournament.getLocation() != null ? tournament.getLocation().trim() : "");
        pst.setDouble(7, tournament.getPrizePool());
        pst.setString(8, normalizeRulesJson(tournament.getRules()));
        pst.setInt(9, id);

        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Tournament updated successfully");
        } else {
            throw new SQLException("Tournament not found");
        }
    }

    @Override
    public List<Tournament> getData() throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        String query = "SELECT * FROM tournament ORDER BY created_at DESC";

        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            Tournament t = new Tournament();
            t.setId(rs.getInt("id"));
            t.setName(rs.getString("name"));
            t.setDescription(rs.getString("description"));
            t.setStartDate(rs.getDate("start_date").toLocalDate());
            t.setEndDate(rs.getDate("end_date").toLocalDate());
            t.setStatus(rs.getString("status"));
            t.setLocation(rs.getString("location"));
            t.setPrizePool(rs.getDouble("prize_pool"));
            t.setRules(rs.getString("rules"));
            t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            tournaments.add(t);
        }
        return tournaments;
    }

    public List<Tournament> getTournamentsByStatus(String status) throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        String query = "SELECT * FROM tournament WHERE status = ? ORDER BY created_at DESC";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, status);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Tournament t = new Tournament();
            t.setId(rs.getInt("id"));
            t.setName(rs.getString("name"));
            t.setDescription(rs.getString("description"));
            t.setStartDate(rs.getDate("start_date").toLocalDate());
            t.setEndDate(rs.getDate("end_date").toLocalDate());
            t.setStatus(rs.getString("status"));
            t.setLocation(rs.getString("location"));
            t.setPrizePool(rs.getDouble("prize_pool"));
            t.setRules(rs.getString("rules"));
            t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            tournaments.add(t);
        }
        return tournaments;
    }

    public Tournament getTournamentById(int id) throws SQLException {
        String query = "SELECT * FROM tournament WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Tournament t = new Tournament();
            t.setId(rs.getInt("id"));
            t.setName(rs.getString("name"));
            t.setDescription(rs.getString("description"));
            t.setStartDate(rs.getDate("start_date").toLocalDate());
            t.setEndDate(rs.getDate("end_date").toLocalDate());
            t.setStatus(rs.getString("status"));
            t.setLocation(rs.getString("location"));
            t.setPrizePool(rs.getDouble("prize_pool"));
            t.setRules(rs.getString("rules"));
            t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return t;
        }
        return null;
    }

    public boolean isTournamentOpen(int tournamentId) throws SQLException {
        String query = "SELECT status FROM tournament WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, tournamentId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return "pending".equalsIgnoreCase(rs.getString("status"));
        }
        return false;
    }

    public List<Tournament> getOpenTournaments() throws SQLException {
        return getTournamentsByStatus("pending");
    }

    private String normalizeRulesJson(String rawRules) {
        String rules = rawRules != null ? rawRules.trim() : "";
        if (rules.isEmpty()) {
            return "{}";
        }

        // Keep valid-looking JSON object/array as-is; otherwise store plain text in a JSON object.
        if ((rules.startsWith("{") && rules.endsWith("}")) || (rules.startsWith("[") && rules.endsWith("]"))) {
            return rules;
        }

        return "{\"text\":\"" + escapeJson(rules) + "\"}";
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
