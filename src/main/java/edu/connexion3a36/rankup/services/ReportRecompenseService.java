package edu.connexion3a36.rankup.services;

import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour générer des rapports sur les récompenses et demandes
 */
public class ReportRecompenseService {
    private Connection cnx;
    private String lastErrorMessage = "";

    public ReportRecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Générer un rapport complet des demandes de récompenses
     */
    public Map<String, Object> generateDemandReport(LocalDate startDate, LocalDate endDate) {
        lastErrorMessage = "";
        Map<String, Object> report = new HashMap<>();

        try {
            // Statistiques générales
            String statsSql = "SELECT " +
                    "COUNT(*) as total_demandes, " +
                    "SUM(CASE WHEN statut='approuvee' THEN 1 ELSE 0 END) as approuvees, " +
                    "SUM(CASE WHEN statut='rejetee' THEN 1 ELSE 0 END) as rejetees, " +
                    "SUM(CASE WHEN statut='en_attente' THEN 1 ELSE 0 END) as en_attente " +
                    "FROM demande_recompense " +
                    "WHERE DATE(date_demande) BETWEEN ? AND ?";

            try (PreparedStatement pst = cnx.prepareStatement(statsSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.put("total_demandes", rs.getInt("total_demandes"));
                    report.put("approuvees", rs.getInt("approuvees"));
                    report.put("rejetees", rs.getInt("rejetees"));
                    report.put("en_attente", rs.getInt("en_attente"));
                }
            }

            // Statistiques par priorité
            Map<String, Integer> byPriority = new HashMap<>();
            String prioritySql = "SELECT priorite, COUNT(*) as count FROM demande_recompense " +
                    "WHERE DATE(date_demande) BETWEEN ? AND ? GROUP BY priorite";
            try (PreparedStatement pst = cnx.prepareStatement(prioritySql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    byPriority.put(rs.getString("priorite"), rs.getInt("count"));
                }
            }
            report.put("par_priorite", byPriority);

            // Score moyen d'approbation
            String scoreSql = "SELECT AVG(score_approbation) as score_moyen FROM demande_recompense " +
                    "WHERE DATE(date_demande) BETWEEN ? AND ? AND score_approbation > 0";
            try (PreparedStatement pst = cnx.prepareStatement(scoreSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.put("score_approbation_moyen", rs.getDouble("score_moyen"));
                }
            }

            // Temps moyen pour approbation
            String timeSql = "SELECT AVG(TIMESTAMPDIFF(DAY, date_demande, date_approbation)) as jours_moyen " +
                    "FROM demande_recompense WHERE date_approbation IS NOT NULL " +
                    "AND DATE(date_demande) BETWEEN ? AND ?";
            try (PreparedStatement pst = cnx.prepareStatement(timeSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    double avgDays = rs.getDouble("jours_moyen");
                    if (!rs.wasNull()) {
                        report.put("jours_moyen_approbation", avgDays);
                    }
                }
            }

        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la génération du rapport: " + e.getMessage());
        }

        return report;
    }

    /**
     * Générer un rapport des récompenses
     */
    public Map<String, Object> generateRecompenseReport(LocalDate startDate, LocalDate endDate) {
        lastErrorMessage = "";
        Map<String, Object> report = new HashMap<>();

        try {
            // Statistiques des récompenses
            String statsSql = "SELECT " +
                    "COUNT(*) as total_recompenses, " +
                    "SUM(CASE WHEN statut_recompense='active' THEN 1 ELSE 0 END) as actives, " +
                    "SUM(CASE WHEN statut_recompense='inactive' THEN 1 ELSE 0 END) as inactives, " +
                    "SUM(cout_estimation) as cout_total " +
                    "FROM recompense " +
                    "WHERE DATE(date_creation) BETWEEN ? AND ?";

            try (PreparedStatement pst = cnx.prepareStatement(statsSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.put("total_recompenses", rs.getInt("total_recompenses"));
                    report.put("actives", rs.getInt("actives"));
                    report.put("inactives", rs.getInt("inactives"));
                    report.put("cout_total", rs.getInt("cout_total"));
                }
            }

            // Récompenses par type
            Map<String, Integer> byType = new HashMap<>();
            String typeSql = "SELECT type, COUNT(*) as count FROM recompense " +
                    "WHERE DATE(date_creation) BETWEEN ? AND ? GROUP BY type";
            try (PreparedStatement pst = cnx.prepareStatement(typeSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    byType.put(rs.getString("type"), rs.getInt("count"));
                }
            }
            report.put("par_type", byType);

            // Coût moyen par récompense
            String costSql = "SELECT AVG(cout_estimation) as cout_moyen FROM recompense " +
                    "WHERE DATE(date_creation) BETWEEN ? AND ? AND cout_estimation > 0";
            try (PreparedStatement pst = cnx.prepareStatement(costSql)) {
                pst.setDate(1, java.sql.Date.valueOf(startDate));
                pst.setDate(2, java.sql.Date.valueOf(endDate));
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.put("cout_moyen", rs.getDouble("cout_moyen"));
                }
            }

        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la génération du rapport de récompenses: " + e.getMessage());
        }

        return report;
    }

    /**
     * Sauvegarder un rapport en base de données
     */
    public boolean saveReport(String typeRapport, LocalDate startDate, LocalDate endDate,
                             Map<String, Object> reportData) {
        lastErrorMessage = "";
        String sql = "INSERT INTO rapport_recompense (type_rapport, date_debut, date_fin, " +
                "total_demandes, total_approuvees, total_rejetees, total_en_attente, cout_total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, typeRapport);
            pst.setDate(2, java.sql.Date.valueOf(startDate));
            pst.setDate(3, java.sql.Date.valueOf(endDate));
            pst.setInt(4, (Integer) reportData.getOrDefault("total_demandes", 0));
            pst.setInt(5, (Integer) reportData.getOrDefault("approuvees", 0));
            pst.setInt(6, (Integer) reportData.getOrDefault("rejetees", 0));
            pst.setInt(7, (Integer) reportData.getOrDefault("en_attente", 0));
            pst.setInt(8, (Integer) reportData.getOrDefault("cout_total", 0));
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la sauvegarde du rapport: " + e.getMessage());
            return false;
        }
    }

    /**
     * Générer un rapport CSV
     */
    public String generateCSVReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Rapport Demandes de Récompenses\n");
        csv.append("Date généré,").append(java.time.LocalDateTime.now()).append("\n");
        csv.append("Période,").append(startDate).append(" à ").append(endDate).append("\n\n");

        csv.append("Statistiques Générales\n");
        Map<String, Object> report = generateDemandReport(startDate, endDate);

        csv.append("Total Demandes,").append(report.get("total_demandes")).append("\n");
        csv.append("Approuvées,").append(report.get("approuvees")).append("\n");
        csv.append("Rejetées,").append(report.get("rejetees")).append("\n");
        csv.append("En Attente,").append(report.get("en_attente")).append("\n");
        csv.append("Score Moyen,").append(report.getOrDefault("score_approbation_moyen", "N/A")).append("\n");
        csv.append("Jours Moyen d'Approbation,").append(report.getOrDefault("jours_moyen_approbation", "N/A")).append("\n");

        return csv.toString();
    }

    /**
     * Récupérer les statistiques complètes
     */
    public Map<String, Object> getCompleteStatistics() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Total de toutes les demandes
            String sql = "SELECT COUNT(*) as count FROM demande_recompense";
            try (Statement st = cnx.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    stats.put("total_all_requests", rs.getInt("count"));
                }
            }

            // Taux d'approbation
            String rateSql = "SELECT " +
                    "(COUNT(CASE WHEN statut='approuvee' THEN 1 END) * 100.0 / COUNT(*)) as taux_approbation " +
                    "FROM demande_recompense WHERE statut IN ('approuvee', 'rejetee')";
            try (Statement st = cnx.createStatement();
                 ResultSet rs = st.executeQuery(rateSql)) {
                if (rs.next()) {
                    stats.put("taux_approbation", rs.getDouble("taux_approbation"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors du calcul des statistiques: " + e.getMessage());
        }

        return stats;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}

