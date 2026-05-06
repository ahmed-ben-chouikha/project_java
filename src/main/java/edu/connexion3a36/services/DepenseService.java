package edu.connexion3a36.services;

import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import edu.connexion3a36.services.BrevoMailService;
import edu.connexion3a36.rankup.app.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class DepenseService {

    private Connection cnx;

    public DepenseService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    // CREATE
    public boolean addDepense(Depense depense) {
        if (depense.getTeamId() == null) {
            System.err.println("Error adding depense: team is required");
            return false;
        }

        BudgetSnapshot budget = getBudgetSnapshotByTeam(depense.getTeamId());
        if (budget == null) {
            System.err.println("Error adding depense: no budget found for this team");
            return false;
        }

        float newMontantUtilise = budget.montantUtilise + depense.getMontant();
        if (newMontantUtilise > budget.montantAlloue) {
            System.err.println("Error adding depense: expense exceeds team budget");
            return false;
        }

        String sql = "INSERT INTO depense (titre, montant, description, date_creation, statut, categorie, team_id, facture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateBudgetSql = "UPDATE budget SET montant_utilise = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            boolean previousAutoCommit = cnx.getAutoCommit();
            cnx.setAutoCommit(false);

            pst.setString(1, depense.getTitre());
            pst.setFloat(2, depense.getMontant());
            pst.setString(3, depense.getDescription());
            pst.setTimestamp(4, Timestamp.valueOf(depense.getDateCreation()));
            pst.setString(5, depense.getStatut());
            pst.setString(6, depense.getCategorie());
            pst.setInt(7, depense.getTeamId());
            pst.setString(8, depense.getFacture());

            int inserted = pst.executeUpdate();
            if (inserted <= 0) {
                cnx.rollback();
                cnx.setAutoCommit(previousAutoCommit);
                return false;
            }

            try (PreparedStatement budgetPst = cnx.prepareStatement(updateBudgetSql)) {
                budgetPst.setFloat(1, newMontantUtilise);
                budgetPst.setInt(2, budget.id);
                int updated = budgetPst.executeUpdate();
                if (updated <= 0) {
                    cnx.rollback();
                    cnx.setAutoCommit(previousAutoCommit);
                    return false;
                }
            }

            cnx.commit();
            cnx.setAutoCommit(previousAutoCommit);
            System.out.println("Depense added successfully!");
            // After commit, check thresholds and send notifications if crossed
            try {
                float prevUsed = budget.montantUtilise;
                float allocated = budget.montantAlloue;
                float newUsed = newMontantUtilise;
                if (allocated > 0) {
                    float prevPct = prevUsed / allocated;
                    float newPct = newUsed / allocated;
                    String managerEmail = SessionManager.getCurrentEmail();
                    if (managerEmail != null && !managerEmail.isBlank()) {
                        final String to = managerEmail;
                        if (prevPct < 0.5f && newPct >= 0.5f) {
                            sendBudgetAlertEmail(to, depense.getTeamName(), 50, allocated, newUsed);
                        }
                        if (prevPct < 0.9f && newPct >= 0.9f) {
                            sendBudgetAlertEmail(to, depense.getTeamName(), 90, allocated, newUsed);
                        }
                    }
                }
            } catch (Exception ignored) {}
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding depense: " + e.getMessage());
            try {
                cnx.rollback();
                cnx.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            return false;
        }
    }

    // READ - Get all depenses
    public List<Depense> getAllDepenses() {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture, (b.montant_alloue - b.montant_utilise) AS montant_restant FROM depense d LEFT JOIN team t ON d.team_id = t.id LEFT JOIN budget b ON b.team_id = d.team_id ORDER BY d.date_creation DESC";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Depense depense = new Depense(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getFloat("montant"),
                    rs.getString("description"),
                    rs.getTimestamp("date_creation").toLocalDateTime(),
                    rs.getString("statut"),
                    rs.getString("categorie"),
                    rs.getObject("team_id") != null ? rs.getInt("team_id") : null,
                    rs.getString("team_name"),
                    rs.getString("facture")
                );
                Float montantRestant = rs.getObject("montant_restant") != null ? rs.getFloat("montant_restant") : 0f;
                depense.setMontantRestant(montantRestant);
                depenses.add(depense);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depenses: " + e.getMessage());
        }
        return depenses;
    }

    // READ - Get depense by ID
    public Depense getDepenseById(int id) {
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture, (b.montant_alloue - b.montant_utilise) AS montant_restant FROM depense d LEFT JOIN team t ON d.team_id = t.id LEFT JOIN budget b ON b.team_id = d.team_id WHERE d.id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Depense depense = new Depense(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getFloat("montant"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getString("statut"),
                        rs.getString("categorie"),
                        rs.getObject("team_id") != null ? rs.getInt("team_id") : null,
                        rs.getString("team_name"),
                        rs.getString("facture")
                    );
                    Float montantRestant = rs.getObject("montant_restant") != null ? rs.getFloat("montant_restant") : 0f;
                    depense.setMontantRestant(montantRestant);
                    return depense;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depense by ID: " + e.getMessage());
        }
        return null;
    }

    // READ - Get depenses by team
    public List<Depense> getDepensesByTeam(int teamId) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture, (b.montant_alloue - b.montant_utilise) AS montant_restant FROM depense d LEFT JOIN team t ON d.team_id = t.id LEFT JOIN budget b ON b.team_id = d.team_id WHERE d.team_id = ? ORDER BY d.date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Depense depense = new Depense(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getFloat("montant"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getString("statut"),
                        rs.getString("categorie"),
                        rs.getObject("team_id") != null ? rs.getInt("team_id") : null,
                        rs.getString("team_name"),
                        rs.getString("facture")
                    );
                    Float montantRestant = rs.getObject("montant_restant") != null ? rs.getFloat("montant_restant") : 0f;
                    depense.setMontantRestant(montantRestant);
                    depenses.add(depense);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depenses by team: " + e.getMessage());
        }
        return depenses;
    }

    // UPDATE
    public boolean updateDepense(Depense depense) {
        if (depense.getTeamId() == null) {
            System.err.println("Error updating depense: team is required");
            return false;
        }

        Depense current = getDepenseById(depense.getId());
        if (current == null || current.getTeamId() == null) {
            System.err.println("Error updating depense: existing depense not found or team not linked");
            return false;
        }

        BudgetSnapshot oldTeamBudget = getBudgetSnapshotByTeam(current.getTeamId());
        BudgetSnapshot newTeamBudget = getBudgetSnapshotByTeam(depense.getTeamId());

        if (oldTeamBudget == null || newTeamBudget == null) {
            System.err.println("Error updating depense: required team budget not found");
            return false;
        }

        float oldTeamNewUsed;
        float newTeamNewUsed;
        if (current.getTeamId().equals(depense.getTeamId())) {
            oldTeamNewUsed = oldTeamBudget.montantUtilise - current.getMontant() + depense.getMontant();
            newTeamNewUsed = oldTeamNewUsed;
            if (newTeamNewUsed > newTeamBudget.montantAlloue || newTeamNewUsed < 0) {
                System.err.println("Error updating depense: expense exceeds team budget");
                return false;
            }
        } else {
            oldTeamNewUsed = oldTeamBudget.montantUtilise - current.getMontant();
            newTeamNewUsed = newTeamBudget.montantUtilise + depense.getMontant();
            if (newTeamNewUsed > newTeamBudget.montantAlloue || oldTeamNewUsed < 0) {
                System.err.println("Error updating depense: invalid budget usage after transfer");
                return false;
            }
        }

        String sql = "UPDATE depense SET titre = ?, montant = ?, description = ?, statut = ?, categorie = ?, team_id = ?, facture = ? WHERE id = ?";
        String updateBudgetSql = "UPDATE budget SET montant_utilise = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            boolean previousAutoCommit = cnx.getAutoCommit();
            cnx.setAutoCommit(false);

            pst.setString(1, depense.getTitre());
            pst.setFloat(2, depense.getMontant());
            pst.setString(3, depense.getDescription());
            pst.setString(4, depense.getStatut());
            pst.setString(5, depense.getCategorie());
            pst.setInt(6, depense.getTeamId());
            pst.setString(7, depense.getFacture());
            pst.setInt(8, depense.getId());

            int updatedDepense = pst.executeUpdate();
            if (updatedDepense <= 0) {
                cnx.rollback();
                cnx.setAutoCommit(previousAutoCommit);
                return false;
            }

            try (PreparedStatement budgetPst = cnx.prepareStatement(updateBudgetSql)) {
                if (current.getTeamId().equals(depense.getTeamId())) {
                    budgetPst.setFloat(1, newTeamNewUsed);
                    budgetPst.setInt(2, newTeamBudget.id);
                    if (budgetPst.executeUpdate() <= 0) {
                        cnx.rollback();
                        cnx.setAutoCommit(previousAutoCommit);
                        return false;
                    }
                } else {
                    budgetPst.setFloat(1, oldTeamNewUsed);
                    budgetPst.setInt(2, oldTeamBudget.id);
                    if (budgetPst.executeUpdate() <= 0) {
                        cnx.rollback();
                        cnx.setAutoCommit(previousAutoCommit);
                        return false;
                    }

                    budgetPst.setFloat(1, newTeamNewUsed);
                    budgetPst.setInt(2, newTeamBudget.id);
                    if (budgetPst.executeUpdate() <= 0) {
                        cnx.rollback();
                        cnx.setAutoCommit(previousAutoCommit);
                        return false;
                    }
                }
            }

            cnx.commit();
            cnx.setAutoCommit(previousAutoCommit);
            System.out.println("Depense updated successfully!");
            // Notify manager if usage thresholds crossed (only when staying in same team)
            try {
                if (current.getTeamId().equals(depense.getTeamId())) {
                    float allocated = newTeamBudget.montantAlloue;
                    float prevUsed = oldTeamBudget.montantUtilise;
                    float newUsed = newTeamNewUsed;
                    if (allocated > 0) {
                        float prevPct = prevUsed / allocated;
                        float newPct = newUsed / allocated;
                        String managerEmail = edu.connexion3a36.rankup.app.SessionManager.getCurrentEmail();
                        if (managerEmail != null && !managerEmail.isBlank()) {
                            if (prevPct < 0.5f && newPct >= 0.5f) {
                                sendBudgetAlertEmail(managerEmail, depense.getTeamName(), 50, allocated, newUsed);
                            }
                            if (prevPct < 0.9f && newPct >= 0.9f) {
                                sendBudgetAlertEmail(managerEmail, depense.getTeamName(), 90, allocated, newUsed);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating depense: " + e.getMessage());
            try {
                cnx.rollback();
                cnx.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            return false;
        }
    }

    // DELETE
    public boolean deleteDepense(int id) {
        Depense current = getDepenseById(id);
        if (current == null) {
            System.err.println("Error deleting depense: depense not found");
            return false;
        }

        if (current.getTeamId() == null) {
            System.err.println("Error deleting depense: depense is not linked to any team");
            return false;
        }

        BudgetSnapshot budget = getBudgetSnapshotByTeam(current.getTeamId());
        if (budget == null) {
            System.err.println("Error deleting depense: no budget found for team");
            return false;
        }

        float newMontantUtilise = budget.montantUtilise - current.getMontant();
        if (newMontantUtilise < 0) {
            newMontantUtilise = 0;
        }

        String sql = "DELETE FROM depense WHERE id = ?";
        String updateBudgetSql = "UPDATE budget SET montant_utilise = ?, date_modification = NOW() WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            boolean previousAutoCommit = cnx.getAutoCommit();
            cnx.setAutoCommit(false);

            pst.setInt(1, id);

            int deleted = pst.executeUpdate();
            if (deleted <= 0) {
                cnx.rollback();
                cnx.setAutoCommit(previousAutoCommit);
                return false;
            }

            try (PreparedStatement budgetPst = cnx.prepareStatement(updateBudgetSql)) {
                budgetPst.setFloat(1, newMontantUtilise);
                budgetPst.setInt(2, budget.id);
                if (budgetPst.executeUpdate() <= 0) {
                    cnx.rollback();
                    cnx.setAutoCommit(previousAutoCommit);
                    return false;
                }
            }

            cnx.commit();
            cnx.setAutoCommit(previousAutoCommit);
            System.out.println("Depense deleted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting depense: " + e.getMessage());
            try {
                cnx.rollback();
                cnx.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            return false;
        }
    }

    private BudgetSnapshot getBudgetSnapshotByTeam(int teamId) {
        String sql = "SELECT id, montant_alloue, montant_utilise FROM budget WHERE team_id = ? LIMIT 1";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new BudgetSnapshot(
                        rs.getInt("id"),
                        rs.getFloat("montant_alloue"),
                        rs.getFloat("montant_utilise")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading budget snapshot: " + e.getMessage());
        }
        return null;
    }

    private void sendBudgetAlertEmail(String recipientEmail, String teamName, int thresholdPercent, float allocated, float used) {
        new Thread(() -> {
            try {
                String safeTeamName = (teamName == null || teamName.isBlank()) ? "votre équipe" : teamName;
                float usedPercent = allocated > 0 ? (used / allocated) * 100f : 0f;
                String subject = "Budget " + thresholdPercent + "% atteint - " + safeTeamName;
                String html = String.format(
                    "<div style='font-family:Arial,sans-serif;line-height:1.6;color:#0f172a;'>" +
                    "<h2 style='margin:0 0 12px;color:#0ea5e9;'>Alerte budget</h2>" +
                    "<p>Bonjour,</p>" +
                    "<p>Le budget de <strong>%s</strong> a franchi le seuil de <strong>%d%%</strong>.</p>" +
                    "<div style='padding:12px 16px;background:#f8fafc;border:1px solid #e2e8f0;border-radius:12px;'>" +
                    "<p style='margin:0 0 6px;'>Budget alloué : <strong>%.2f €</strong></p>" +
                    "<p style='margin:0 0 6px;'>Budget utilisé : <strong>%.2f €</strong> (%.2f%%)</p>" +
                    "<p style='margin:0;'>Montant restant : <strong>%.2f €</strong></p>" +
                    "</div>" +
                    "<p style='margin-top:16px;'>Merci de vérifier la situation budgétaire et d'ajuster les prochaines dépenses si nécessaire.</p>" +
                    "<p style='margin-top:20px;color:#475569;'>Cordialement,<br>Equipe RankUp</p>" +
                    "</div>",
                    safeTeamName,
                    thresholdPercent,
                    allocated,
                    used,
                    usedPercent,
                    Math.max(allocated - used, 0f)
                );
                String text = String.format(
                    "Alerte budget: %s a atteint %d%%. Alloué: %.2f EUR, utilisé: %.2f EUR, restant: %.2f EUR.",
                    safeTeamName,
                    thresholdPercent,
                    allocated,
                    used,
                    Math.max(allocated - used, 0f)
                );
                BrevoMailService.sendEmail(recipientEmail, subject, html, text);
            } catch (Exception ignored) {
            }
        }).start();
    }

    private static class BudgetSnapshot {
        private final int id;
        private final float montantAlloue;
        private final float montantUtilise;

        private BudgetSnapshot(int id, float montantAlloue, float montantUtilise) {
            this.id = id;
            this.montantAlloue = montantAlloue;
            this.montantUtilise = montantUtilise;
        }
    }

    // Get depenses by status
    public List<Depense> getDepensesByStatus(String statut) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture, (b.montant_alloue - b.montant_utilise) AS montant_restant FROM depense d LEFT JOIN team t ON d.team_id = t.id LEFT JOIN budget b ON b.team_id = d.team_id WHERE d.statut = ? ORDER BY d.date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, statut);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Depense depense = new Depense(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getFloat("montant"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getString("statut"),
                        rs.getString("categorie"),
                        rs.getObject("team_id") != null ? rs.getInt("team_id") : null,
                        rs.getString("team_name"),
                        rs.getString("facture")
                    );
                    Float montantRestant = rs.getObject("montant_restant") != null ? rs.getFloat("montant_restant") : 0f;
                    depense.setMontantRestant(montantRestant);
                    depenses.add(depense);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depenses by status: " + e.getMessage());
        }
        return depenses;
    }

    // Get depenses by categorie
    public List<Depense> getDepensesByCategorie(String categorie) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture, (b.montant_alloue - b.montant_utilise) AS montant_restant FROM depense d LEFT JOIN team t ON d.team_id = t.id LEFT JOIN budget b ON b.team_id = d.team_id WHERE d.categorie = ? ORDER BY d.date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, categorie);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Depense depense = new Depense(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getFloat("montant"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getString("statut"),
                        rs.getString("categorie"),
                        rs.getObject("team_id") != null ? rs.getInt("team_id") : null,
                        rs.getString("team_name"),
                        rs.getString("facture")
                    );
                    Float montantRestant = rs.getObject("montant_restant") != null ? rs.getFloat("montant_restant") : 0f;
                    depense.setMontantRestant(montantRestant);
                    depenses.add(depense);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depenses by categorie: " + e.getMessage());
        }
        return depenses;
    }

    // Get total depenses amount
    public float getTotalDepenses() {
        String sql = "SELECT SUM(montant) as total FROM depense";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getFloat("total");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total depenses: " + e.getMessage());
        }
        return 0;
    }

    // Get total depenses by team
    public float getTotalDepensesByTeam(int teamId) {
        String sql = "SELECT SUM(montant) as total FROM depense WHERE team_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, teamId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total depenses by team: " + e.getMessage());
        }
        return 0;
    }
}

