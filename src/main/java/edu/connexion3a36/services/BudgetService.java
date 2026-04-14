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
        String sql = "INSERT INTO budget (montant_alloue, montant_utilise, date_allocation, team_id, notes, justificatif) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setFloat(1, budget.getMontantAlloue());
            pst.setFloat(2, budget.getMontantUtilise());
            pst.setTimestamp(3, Timestamp.valueOf(budget.getDateAllocation()));
            pst.setInt(4, budget.getTeamId());
            pst.setString(5, budget.getNotes());
            pst.setString(6, budget.getJustificatif());
            pst.executeUpdate();
            System.out.println("Budget added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding budget: " + e.getMessage());
            return false;
        }
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
        String sql = "UPDATE budget SET montant_alloue = ?, montant_utilise = ?, notes = ?, justificatif = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setFloat(1, budget.getMontantAlloue());
            pst.setFloat(2, budget.getMontantUtilise());
            pst.setString(3, budget.getNotes());
            pst.setString(4, budget.getJustificatif());
            pst.setInt(5, budget.getId());
            pst.executeUpdate();
            System.out.println("Budget updated successfully!");
            return true;
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
            pst.executeUpdate();
            System.out.println("Budget deleted successfully!");
            return true;
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

    // Get budgets by status - REMOVED (statut column removed)
    // This method is no longer available
}

