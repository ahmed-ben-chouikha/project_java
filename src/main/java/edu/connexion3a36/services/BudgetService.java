package edu.connexion3a36.services;

import edu.connexion3a36.entities.Budget;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private Connection cnx;

    public BudgetService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    // CREATE
    public boolean addBudget(Budget budget) {
        if (hasBudgetForTeam(budget.getTeamId())) {
            System.err.println("Error adding budget: this team already has a budget");
            return false;
        }

        String sql = "INSERT INTO budget (montant_alloue, montant_utilise, date_allocation, team_id, notes, justificatif) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setFloat(1, budget.getMontantAlloue());
            pst.setFloat(2, budget.getMontantUtilise());
            pst.setTimestamp(3, Timestamp.valueOf(budget.getDateAllocation()));
            pst.setInt(4, budget.getTeamId());
            pst.setString(5, budget.getNotes());
            pst.setString(6, budget.getJustificatif());
            int affected = pst.executeUpdate();
            if (affected > 0) {
                System.out.println("Budget added successfully!");
                return true;
            }
            System.err.println("Error adding budget: no row inserted");
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding budget: " + e.getMessage());
            return false;
        }
    }

    public boolean hasBudgetForTeam(int teamId) {
        String sql = "SELECT COUNT(*) FROM budget WHERE team_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking budget by team: " + e.getMessage());
        }
        return false;
    }

    public Budget getBudgetByTeamId(int teamId) {
        String sql = "SELECT b.id, b.montant_alloue, b.montant_utilise, b.date_allocation, b.date_modification, b.team_id, t.name as team_name, b.notes, b.justificatif FROM budget b LEFT JOIN team t ON b.team_id = t.id WHERE b.team_id = ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Budget(
                        rs.getInt("id"),
                        rs.getFloat("montant_alloue"),
                        rs.getFloat("montant_utilise"),
                        rs.getTimestamp("date_allocation").toLocalDateTime(),
                        rs.getTimestamp("date_modification") != null ? rs.getTimestamp("date_modification").toLocalDateTime() : null,
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getString("notes"),
                        null,
                        rs.getString("justificatif")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budget by team: " + e.getMessage());
        }
        return null;
    }

    // READ - Get all budgets
    public List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.id, b.montant_alloue, b.montant_utilise, b.date_allocation, b.date_modification, b.team_id, t.name as team_name, b.notes, b.justificatif FROM budget b LEFT JOIN team t ON b.team_id = t.id ORDER BY b.date_allocation DESC";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Budget budget = new Budget(
                    rs.getInt("id"),
                    rs.getFloat("montant_alloue"),
                    rs.getFloat("montant_utilise"),
                    rs.getTimestamp("date_allocation").toLocalDateTime(),
                    rs.getTimestamp("date_modification") != null ? rs.getTimestamp("date_modification").toLocalDateTime() : null,
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    rs.getString("notes"),
                    null, // statut removed
                    rs.getString("justificatif")
                );
                budgets.add(budget);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budgets: " + e.getMessage());
        }
        return budgets;
    }

    // READ - Get budget by ID
    public Budget getBudgetById(int id) {
        String sql = "SELECT b.id, b.montant_alloue, b.montant_utilise, b.date_allocation, b.date_modification, b.team_id, t.name as team_name, b.notes, b.justificatif FROM budget b LEFT JOIN team t ON b.team_id = t.id WHERE b.id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Budget(
                        rs.getInt("id"),
                        rs.getFloat("montant_alloue"),
                        rs.getFloat("montant_utilise"),
                        rs.getTimestamp("date_allocation").toLocalDateTime(),
                        rs.getTimestamp("date_modification") != null ? rs.getTimestamp("date_modification").toLocalDateTime() : null,
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getString("notes"),
                        null, // statut removed
                        rs.getString("justificatif")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budget by ID: " + e.getMessage());
        }
        return null;
    }

    // READ - Get budgets by team
    public List<Budget> getBudgetsByTeam(int teamId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT b.id, b.montant_alloue, b.montant_utilise, b.date_allocation, b.date_modification, b.team_id, t.name as team_name, b.notes, b.justificatif FROM budget b LEFT JOIN team t ON b.team_id = t.id WHERE b.team_id = ? ORDER BY b.date_allocation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget(
                        rs.getInt("id"),
                        rs.getFloat("montant_alloue"),
                        rs.getFloat("montant_utilise"),
                        rs.getTimestamp("date_allocation").toLocalDateTime(),
                        rs.getTimestamp("date_modification") != null ? rs.getTimestamp("date_modification").toLocalDateTime() : null,
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getString("notes"),
                        null, // statut removed
                        rs.getString("justificatif")
                    );
                    budgets.add(budget);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budgets by team: " + e.getMessage());
        }
        return budgets;
    }

    // UPDATE
    public boolean updateBudget(Budget budget) {
        Budget existing = getBudgetById(budget.getId());
        if (existing == null) {
            System.err.println("Error updating budget: budget not found");
            return false;
        }

        if (budget.getTeamId() != existing.getTeamId() && hasBudgetForTeam(budget.getTeamId())) {
            System.err.println("Error updating budget: target team already has a budget");
            return false;
        }

        if (budget.getMontantUtilise() > budget.getMontantAlloue() || budget.getMontantUtilise() < 0) {
            System.err.println("Error updating budget: used amount is invalid compared to allocated amount");
            return false;
        }

        String sql = "UPDATE budget SET montant_alloue = ?, montant_utilise = ?, team_id = ?, notes = ?, justificatif = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setFloat(1, budget.getMontantAlloue());
            pst.setFloat(2, budget.getMontantUtilise());
            pst.setInt(3, budget.getTeamId());
            pst.setString(4, budget.getNotes());
            pst.setString(5, budget.getJustificatif());
            pst.setInt(6, budget.getId());
            int affected = pst.executeUpdate();
            if (affected > 0) {
                System.out.println("Budget updated successfully!");
                return true;
            }
            System.err.println("Error updating budget: no row updated");
            return false;
        } catch (SQLException e) {
            System.err.println("Error updating budget: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteBudget(int id) {
        String sql = "DELETE FROM budget WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            int affected = pst.executeUpdate();
            if (affected > 0) {
                System.out.println("Budget deleted successfully!");
                return true;
            }
            System.err.println("Error deleting budget: no row deleted");
            return false;
        } catch (SQLException e) {
            System.err.println("Error deleting budget: " + e.getMessage());
            return false;
        }
    }

    // Update montant utilisé (used for tracking expenses)
    public boolean updateMontantUtilise(int budgetId, float montantUtilise) {
        String sql = "UPDATE budget SET montant_utilise = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setFloat(1, montantUtilise);
            pst.setInt(2, budgetId);
            pst.executeUpdate();
            System.out.println("Budget montant utilisé updated!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating montant utilisé: " + e.getMessage());
            return false;
        }
    }

    
}

