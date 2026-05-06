package edu.connexion3a36.services;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamService {

    private Connection cnx;

    public TeamService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    // CREATE
    public boolean addTeam(Team team) {
        if (cnx == null) {
            System.err.println("Error adding team: Database connection is not available.");
            return false;
        }

        if (team == null || team.getName() == null || team.getName().trim().isEmpty()) {
            System.err.println("Error adding team: Team name is required.");
            return false;
        }

        if (teamNameExists(team.getName())) {
            System.err.println("Error adding team: Team name already exists.");
            return false;
        }

        boolean hasStatut = hasColumn("team", "statut");
        String sql = hasStatut
                ? "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, statut, score, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())"
                : "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, score, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, team.getName());
            pst.setString(2, team.getCountry());
            pst.setString(3, team.getDescription());
            pst.setString(4, team.getDetailedDescription());
            pst.setString(5, team.getLogo());
            pst.setString(6, team.getJeu());
            pst.setString(7, team.getNiveau());
            if (hasStatut) {
                pst.setString(8, team.getStatut());
                pst.setInt(9, team.getScore());
            } else {
                pst.setInt(8, team.getScore());
            }
            pst.executeUpdate();
            System.out.println("Team added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding team: " + e.getMessage());
            return false;
        }
    }

    // READ - Get all teams
    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        if (cnx == null) {
            System.err.println("Error fetching teams: Database connection is not available.");
            return teams;
        }

        String sql = "SELECT * FROM team ORDER BY score DESC";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                teams.add(mapTeam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teams: " + e.getMessage());
        }
        return teams;
    }

    // READ - Get team by ID
    public Team getTeamById(int id) {
        if (cnx == null) {
            System.err.println("Error fetching team by ID: Database connection is not available.");
            return null;
        }

        String sql = "SELECT * FROM team WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapTeam(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team by ID: " + e.getMessage());
        }
        return null;
    }

    // UPDATE
    public boolean updateTeam(Team team) {
        if (cnx == null) {
            System.err.println("Error updating team: Database connection is not available.");
            return false;
        }

        if (team == null || team.getName() == null || team.getName().trim().isEmpty()) {
            System.err.println("Error updating team: Team name is required.");
            return false;
        }

        if (teamNameExistsForAnotherId(team.getName(), team.getId())) {
            System.err.println("Error updating team: Team name already exists.");
            return false;
        }

        boolean hasStatut = hasColumn("team", "statut");
        boolean hasDateValidation = hasColumn("team", "date_validation");
        String sql = hasStatut && hasDateValidation
                ? "UPDATE team SET name = ?, country = ?, description = ?, detailed_description = ?, logo = ?, jeu = ?, niveau = ?, statut = ?, date_validation = ?, score = ?, updated_at = NOW() WHERE id = ?"
                : hasStatut
                ? "UPDATE team SET name = ?, country = ?, description = ?, detailed_description = ?, logo = ?, jeu = ?, niveau = ?, statut = ?, score = ?, updated_at = NOW() WHERE id = ?"
                : "UPDATE team SET name = ?, country = ?, description = ?, detailed_description = ?, logo = ?, jeu = ?, niveau = ?, score = ?, updated_at = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, team.getName());
            pst.setString(2, team.getCountry());
            pst.setString(3, team.getDescription());
            pst.setString(4, team.getDetailedDescription());
            pst.setString(5, team.getLogo());
            pst.setString(6, team.getJeu());
            pst.setString(7, team.getNiveau());
            int index = 8;
            if (hasStatut) {
                pst.setString(index++, team.getStatut());
            }
            if (hasDateValidation) {
                pst.setTimestamp(index++, team.getDateValidation() != null ? new java.sql.Timestamp(team.getDateValidation().getTime()) : null);
            }
            pst.setInt(index++, team.getScore());
            pst.setInt(index, team.getId());
            pst.executeUpdate();
            System.out.println("Team updated successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating team: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteTeam(int id) {
        if (cnx == null) {
            System.err.println("Error deleting team: Database connection is not available.");
            return false;
        }
        String sql = "DELETE FROM team WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Team deleted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting team: " + e.getMessage());
            return false;
        }
    }

    // Search teams by name
    public List<Team> searchTeamsByName(String name) {
        List<Team> teams = new ArrayList<>();
        if (cnx == null) {
            System.err.println("Error searching teams: Database connection is not available.");
            return teams;
        }

        String sql = "SELECT * FROM team WHERE name LIKE ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + name + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapTeam(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching teams: " + e.getMessage());
        }
        return teams;
    }

    // Search teams by status
    public List<Team> searchTeamsByStatus(String statut) {
        List<Team> teams = new ArrayList<>();
        if (cnx == null) {
            System.err.println("Error searching teams by status: Database connection is not available.");
            return teams;
        }
        if (!hasColumn("team", "statut")) {
            return getAllTeams();
        }

        String sql = "SELECT * FROM team WHERE statut = ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, statut);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapTeam(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching teams by status: " + e.getMessage());
        }
        return teams;
    }

    // Search teams by game (jeu)
    public List<Team> searchTeamsByGame(String jeu) {
        List<Team> teams = new ArrayList<>();
        if (cnx == null) {
            System.err.println("Error searching teams by game: Database connection is not available.");
            return teams;
        }

        String sql = "SELECT * FROM team WHERE jeu = ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, jeu);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapTeam(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching teams by game: " + e.getMessage());
        }
        return teams;
    }

    // Update team status
    public boolean updateTeamStatus(int id, String newStatus) {
        if (cnx == null) {
            System.err.println("Error updating team status: Database connection is not available.");
            return false;
        }
        if (!hasColumn("team", "statut")) {
            System.err.println("Error updating team status: Column 'statut' does not exist.");
            return false;
        }

        String sql = hasColumn("team", "date_validation")
                ? "UPDATE team SET statut = ?, date_validation = NOW() WHERE id = ?"
                : "UPDATE team SET statut = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, newStatus);
            pst.setInt(2, id);
            pst.executeUpdate();
            System.out.println("Team status updated to: " + newStatus);
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating team status: " + e.getMessage());
            return false;
        }
    }

    // Update team score
    public boolean updateTeamScore(int id, int newScore) {
        if (cnx == null) {
            System.err.println("Error updating team score: Database connection is not available.");
            return false;
        }
        String sql = "UPDATE team SET score = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, newScore);
            pst.setInt(2, id);
            pst.executeUpdate();
            System.out.println("Team score updated to: " + newScore);
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating team score: " + e.getMessage());
            return false;
        }
    }

    public boolean teamNameExists(String name) {
        if (cnx == null || name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM team WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, name);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking team name uniqueness: " + e.getMessage());
            return false;
        }
    }

    public boolean teamNameExistsForAnotherId(String name, int currentId) {
        if (cnx == null || name == null || name.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM team WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) AND id <> ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setInt(2, currentId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking team name uniqueness for update: " + e.getMessage());
            return false;
        }
    }

    private Team mapTeam(ResultSet rs) throws SQLException {
        Set<String> columns = getColumnNames(rs);
        return new Team(
                getIntOrDefault(rs, columns, "id", 0),
                getStringOrDefault(rs, columns, "name", ""),
                getStringOrDefault(rs, columns, "country", ""),
                getStringOrDefault(rs, columns, "description", ""),
                getStringOrDefault(rs, columns, "detailed_description", ""),
                getStringOrDefault(rs, columns, "logo", ""),
                getStringOrDefault(rs, columns, "jeu", ""),
                getStringOrDefault(rs, columns, "niveau", ""),
                getStringOrDefault(rs, columns, "statut", "en attente"),
                columns.contains("date_validation") ? rs.getTimestamp("date_validation") : null,
                getIntOrDefault(rs, columns, "score", 0),
                columns.contains("created_at") ? rs.getTimestamp("created_at") : null,
                columns.contains("updated_at") ? rs.getTimestamp("updated_at") : null
        );
    }

    private boolean hasColumn(String table, String column) {
        if (cnx == null) {
            return false;
        }
        String sql = "SELECT * FROM " + table + " LIMIT 1";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            Set<String> columns = getColumnNames(rs);
            return columns.contains(column.toLowerCase());
        } catch (SQLException e) {
            return false;
        }
    }

    private Set<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Set<String> names = new HashSet<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            names.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return names;
    }

    private String getStringOrDefault(ResultSet rs, Set<String> columns, String key, String fallback) throws SQLException {
        if (!columns.contains(key.toLowerCase())) {
            return fallback;
        }
        String value = rs.getString(key);
        return value == null ? fallback : value;
    }

    private int getIntOrDefault(ResultSet rs, Set<String> columns, String key, int fallback) throws SQLException {
        if (!columns.contains(key.toLowerCase())) {
            return fallback;
        }
        return rs.getInt(key);
    }
}
