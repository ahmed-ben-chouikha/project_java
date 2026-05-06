package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour l'approbation automatique des demandes de récompenses
 */
public class AutoApprovalService {
    private Connection cnx;
    private DemandeRecompenseService demandeService;
    private NotificationRecompenseService notificationService;
    private String lastErrorMessage = "";

    public AutoApprovalService() {
        cnx = MyConnection.getInstance().getCnx();
        demandeService = new DemandeRecompenseService();
        notificationService = new NotificationRecompenseService();
    }

    /**
     * Effectuer l'approbation automatique selon les critères
     */
    public int autoApproveDemands() {
        lastErrorMessage = "";
        int approvedCount = 0;

        // Récupérer les demandes en attente
        List<DemandeRecompense> pendingDemands = demandeService.getByStatut("en_attente");

        for (DemandeRecompense demande : pendingDemands) {
            if (shouldAutoApprove(demande)) {
                if (approveDemand(demande)) {
                    approvedCount++;
                    notificationService.notifyApproved(demande.getId(), demande.getNomDemandeur());
                }
            }
        }

        return approvedCount;
    }

    /**
     * Déterminer si une demande doit être approuvée automatiquement
     */
    private boolean shouldAutoApprove(DemandeRecompense demande) {
        // Critère 1: Score d'approbation >= 70
        if (demande.getScoreApprobation() != null && demande.getScoreApprobation() >= 70) {
            return true;
        }

        // Critère 2: Priorité haute et demande valide
        if ("haute".equalsIgnoreCase(demande.getPriorite())) {
            if (isValidMotif(demande.getMotif())) {
                return true;
            }
        }

        // Critère 3: Demande avec motif excellent et priorité normale/haute
        if (isExcellentMotif(demande.getMotif())) {
            String priorite = demande.getPriorite() != null ? demande.getPriorite() : "normale";
            if ("haute".equalsIgnoreCase(priorite) || "normale".equalsIgnoreCase(priorite)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Valider la qualité du motif
     */
    private boolean isValidMotif(String motif) {
        if (motif == null || motif.isEmpty()) {
            return false;
        }
        // Au minimum 100 caractères et sans fautes évidentes
        return motif.length() >= 100 && !containsSpamKeywords(motif);
    }

    /**
     * Vérifier si le motif est d'excellente qualité
     */
    private boolean isExcellentMotif(String motif) {
        if (motif == null || motif.isEmpty()) {
            return false;
        }
        // Plus de 200 caractères et bien structuré
        return motif.length() >= 200 && countSentences(motif) >= 3 && !containsSpamKeywords(motif);
    }

    /**
     * Compter le nombre de phrases dans le motif
     */
    private int countSentences(String motif) {
        if (motif == null) {
            return 0;
        }
        return motif.split("[.!?]+").length;
    }

    /**
     * Vérifier la présence de mots-clés suspects
     */
    private boolean containsSpamKeywords(String text) {
        if (text == null) {
            return false;
        }
        String[] spamKeywords = {"spam", "fake", "test", "xxx", "blablabla"};
        String lowerText = text.toLowerCase();
        for (String keyword : spamKeywords) {
            if (lowerText.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Approuver une demande
     */
    public boolean approveDemand(DemandeRecompense demande) {
        lastErrorMessage = "";
        String sql = "UPDATE demande_recompense SET statut=?, date_approbation=NOW() WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "approuvee");
            pst.setInt(2, demande.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de l'approbation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rejeter une demande avec raison
     */
    public boolean rejectDemand(int demandeId, String raison) {
        lastErrorMessage = "";
        String sql = "UPDATE demande_recompense SET statut=?, date_rejet=NOW(), raison_rejet=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "rejetee");
            pst.setString(2, raison);
            pst.setInt(3, demandeId);
            boolean success = pst.executeUpdate() > 0;
            if (success) {
                notificationService.notifyRejected(demandeId, raison);
            }
            return success;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors du rejet: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculer le score d'approbation
     */
    public int calculateApprovalScore(DemandeRecompense demande) {
        int score = 0;

        // Score basé sur la longueur du motif
        if (demande.getMotif() != null) {
            int motifLength = demande.getMotif().length();
            if (motifLength >= 50) score += 10;
            if (motifLength >= 100) score += 10;
            if (motifLength >= 200) score += 10;
            if (motifLength >= 500) score += 10;
        }

        // Score basé sur la priorité
        if ("haute".equalsIgnoreCase(demande.getPriorite())) {
            score += 20;
        } else if ("normale".equalsIgnoreCase(demande.getPriorite())) {
            score += 10;
        } else if ("basse".equalsIgnoreCase(demande.getPriorite())) {
            score += 5;
        }

        // Score basé sur la qualité du motif
        if (demande.getMotif() != null && countSentences(demande.getMotif()) >= 3) {
            score += 15;
        }

        // Score basé sur le format de l'email
        if (demande.getEmail() != null && demande.getEmail().contains("@")) {
            score += 5;
        }

        // Déduction pour mots suspects
        if (demande.getMotif() != null && containsSpamKeywords(demande.getMotif())) {
            score -= 30;
        }

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Mettre à jour le score d'approbation
     */
    public boolean updateApprovalScore(int demandeId, int score) {
        lastErrorMessage = "";
        String sql = "UPDATE demande_recompense SET score_approbation=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, score);
            pst.setInt(2, demandeId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Activer une règle d'approbation automatique
     */
    public boolean createApprovalRule(String nomRegle, String description, String conditionPriorite, 
                                     int conditionScoreMin, int conditionDateMinJours) {
        lastErrorMessage = "";
        String sql = "INSERT INTO regle_approbation_auto (nom_regle, description, condition_priorite, " +
                "condition_score_min, condition_date_min_jours, active) VALUES (?, ?, ?, ?, ?, 1)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, nomRegle);
            pst.setString(2, description);
            pst.setString(3, conditionPriorite);
            pst.setInt(4, conditionScoreMin);
            pst.setInt(5, conditionDateMinJours);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la création de règle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer toutes les règles d'approbation actives
     */
    public List<String> getActiveRules() {
        List<String> rules = new ArrayList<>();
        String sql = "SELECT nom_regle FROM regle_approbation_auto WHERE active=1";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rules.add(rs.getString("nom_regle"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des règles: " + e.getMessage());
        }
        return rules;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}

