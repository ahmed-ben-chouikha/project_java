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
    public int addTeam(Team team) {
        if (cnx == null) {
            System.err.println("Error adding team: Database connection is not available.");
            return -1;
        }

        if (team == null || team.getName() == null || team.getName().trim().isEmpty()) {
            System.err.println("Error adding team: Team name is required.");
            return -1;
        }

        if (teamNameExists(team.getName())) {
            System.err.println("Error adding team: Team name already exists.");
            return -1;
        }

        boolean hasStatut = hasColumn("team", "statut");
        boolean hasCreatorId = hasColumn("team", "creator_id");
        String sql = hasStatut
                ? (hasCreatorId
                ? "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, statut, score, creator_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())"
                : "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, statut, score, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())")
                : (hasCreatorId
                ? "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, score, creator_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())"
                : "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, score, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())");
        try (PreparedStatement pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            pst.setInt(index++, team.getScore());
            if (hasCreatorId) {
                if (team.getCreatorId() == null) {
                    pst.setNull(index, java.sql.Types.INTEGER);
                } else {
                    pst.setInt(index, team.getCreatorId());
                }
            }
            pst.executeUpdate();
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("Team added successfully!");
                    return (int) generatedKeys.getLong(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error adding team: " + e.getMessage());
            return -1;
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

    public List<Team> getTeamsByCreatorId(int creatorId) {
        List<Team> teams = new ArrayList<>();
        if (cnx == null || creatorId <= 0 || !hasColumn("team", "creator_id")) {
            return teams;
        }

        String sql = "SELECT * FROM team WHERE creator_id = ? ORDER BY created_at DESC, id DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, creatorId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapTeam(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teams by creator: " + e.getMessage());
        }
        return teams;
    }

    public int countTeamsByStatus(String statut) {
        if (cnx == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) AS total FROM team WHERE statut = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, statut);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting teams by status: " + e.getMessage());
        }
        return 0;
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
        Team team = new Team(
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
        if (columns.contains("creator_id")) {
            team.setCreatorId(rs.getObject("creator_id") == null ? null : rs.getInt("creator_id"));
        }
        return team;
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

    public List<TeamMember> getTeamMembers(int teamId) throws SQLException {
        List<TeamMember> members = new ArrayList<>();
        if (cnx == null) {
            return members;
        }

        String sql = "SELECT p.id, p.nickname, p.first_name, p.last_name, p.role FROM player p WHERE p.team_id = ? ORDER BY p.nickname ASC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    members.add(new TeamMember(
                            rs.getInt("id"),
                            rs.getString("nickname"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team members: " + e.getMessage());
        }
        return members;
    }

    public static class TeamMember {
        public final int id;
        public final String nickname;
        public final String firstName;
        public final String lastName;
        public final String role;

        public TeamMember(int id, String nickname, String firstName, String lastName, String role) {
            this.id = id;
            this.nickname = nickname;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        public String getDisplayName() {
            if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
                return firstName + " " + lastName + " (" + nickname + ")";
            }
            return nickname;
        }

        public String getId() { return String.valueOf(id); }
        public String getNickname() { return nickname; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getRole() { return role; }
    }
}
