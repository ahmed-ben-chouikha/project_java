package edu.connexion3a36.services;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    private Connection cnx;

    public TeamService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    // CREATE
    public boolean addTeam(Team team) {
        String sql = "INSERT INTO team (name, country, description, detailed_description, logo, jeu, niveau, statut, score) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, team.getName());
            pst.setString(2, team.getCountry());
            pst.setString(3, team.getDescription());
            pst.setString(4, team.getDetailedDescription());
            pst.setString(5, team.getLogo());
            pst.setString(6, team.getJeu());
            pst.setString(7, team.getNiveau());
            pst.setString(8, team.getStatut());
            pst.setInt(9, team.getScore());
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
        String sql = "SELECT id, name, country, description, detailed_description, logo, jeu, niveau, statut, date_validation, score, created_at, updated_at FROM team ORDER BY score DESC";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Team team = new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("country"),
                    rs.getString("description"),
                    rs.getString("detailed_description"),
                    rs.getString("logo"),
                    rs.getString("jeu"),
                    rs.getString("niveau"),
                    rs.getString("statut"),
                    rs.getTimestamp("date_validation"),
                    rs.getInt("score"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
                teams.add(team);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching teams: " + e.getMessage());
        }
        return teams;
    }

    // READ - Get team by ID
    public Team getTeamById(int id) {
        String sql = "SELECT id, name, country, description, detailed_description, logo, jeu, niveau, statut, date_validation, score, created_at, updated_at FROM team WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("description"),
                        rs.getString("detailed_description"),
                        rs.getString("logo"),
                        rs.getString("jeu"),
                        rs.getString("niveau"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_validation"),
                        rs.getInt("score"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching team by ID: " + e.getMessage());
        }
        return null;
    }

    // UPDATE
    public boolean updateTeam(Team team) {
        String sql = "UPDATE team SET name = ?, country = ?, description = ?, detailed_description = ?, logo = ?, jeu = ?, niveau = ?, statut = ?, date_validation = ?, score = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, team.getName());
            pst.setString(2, team.getCountry());
            pst.setString(3, team.getDescription());
            pst.setString(4, team.getDetailedDescription());
            pst.setString(5, team.getLogo());
            pst.setString(6, team.getJeu());
            pst.setString(7, team.getNiveau());
            pst.setString(8, team.getStatut());
            pst.setTimestamp(9, team.getDateValidation() != null ? new java.sql.Timestamp(team.getDateValidation().getTime()) : null);
            pst.setInt(10, team.getScore());
            pst.setInt(11, team.getId());
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
        String sql = "SELECT id, name, country, description, detailed_description, logo, jeu, niveau, statut, date_validation, score, created_at, updated_at FROM team WHERE name LIKE ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + name + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("description"),
                        rs.getString("detailed_description"),
                        rs.getString("logo"),
                        rs.getString("jeu"),
                        rs.getString("niveau"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_validation"),
                        rs.getInt("score"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    teams.add(team);
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
        String sql = "SELECT id, name, country, description, detailed_description, logo, jeu, niveau, statut, date_validation, score, created_at, updated_at FROM team WHERE statut = ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, statut);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("description"),
                        rs.getString("detailed_description"),
                        rs.getString("logo"),
                        rs.getString("jeu"),
                        rs.getString("niveau"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_validation"),
                        rs.getInt("score"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    teams.add(team);
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
        String sql = "SELECT id, name, country, description, detailed_description, logo, jeu, niveau, statut, date_validation, score, created_at, updated_at FROM team WHERE jeu = ? ORDER BY score DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, jeu);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("description"),
                        rs.getString("detailed_description"),
                        rs.getString("logo"),
                        rs.getString("jeu"),
                        rs.getString("niveau"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_validation"),
                        rs.getInt("score"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching teams by game: " + e.getMessage());
        }
        return teams;
    }

    // Update team status
    public boolean updateTeamStatus(int id, String newStatus) {
        String sql = "UPDATE team SET statut = ?, date_validation = NOW() WHERE id = ?";
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
}

