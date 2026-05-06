package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour gérer les notifications liées aux récompenses et demandes
 */
public class NotificationRecompenseService {
    private Connection cnx;
    private String lastErrorMessage = "";

    public NotificationRecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Créer une notification pour une demande de récompense
     */
    public boolean createNotification(int demandeId, String typeNotification, String message) {
        lastErrorMessage = "";
        String sql = "INSERT INTO notification_recompense (demande_id, type_notification, statut_notification, message, date_creation) " +
                "VALUES (?, ?, 'non_lue', ?, NOW())";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, demandeId);
            pst.setString(2, typeNotification);
            pst.setString(3, message);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la création de notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Créer une notification d'approbation
     */
    public boolean notifyApproved(int demandeId, String nomDemandeur) {
        String message = "Félicitations! Votre demande de récompense a été approuvée.";
        return createNotification(demandeId, "APPROUVEE", message);
    }

    /**
     * Créer une notification de rejet
     */
    public boolean notifyRejected(int demandeId, String raisonRejet) {
        String message = "Votre demande de récompense a été rejetée. Raison: " + raisonRejet;
        return createNotification(demandeId, "REJETEE", message);
    }

    /**
     * Créer une notification de nouvelle demande
     */
    public boolean notifyNewRequest(int demandeId, String nomDemandeur) {
        String message = "Nouvelle demande de récompense de " + nomDemandeur + " en attente de révision.";
        return createNotification(demandeId, "NOUVELLE_DEMANDE", message);
    }

    /**
     * Marquer une notification comme lue
     */
    public boolean markAsRead(int notificationId) {
        lastErrorMessage = "";
        String sql = "UPDATE notification_recompense SET statut_notification='lue', date_lecture=NOW() WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, notificationId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Récupérer les notifications non lues pour une demande
     */
    public List<String> getUnreadNotifications(int demandeId) {
        List<String> notifications = new ArrayList<>();
        String sql = "SELECT message FROM notification_recompense WHERE demande_id=? AND statut_notification='non_lue' ORDER BY date_creation DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, demandeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                notifications.add(rs.getString("message"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des notifications: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Supprimer une notification
     */
    public boolean deleteNotification(int notificationId) {
        String sql = "DELETE FROM notification_recompense WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, notificationId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer toutes les notifications lues pour une demande
     */
    public boolean deleteReadNotifications(int demandeId) {
        String sql = "DELETE FROM notification_recompense WHERE demande_id=? AND statut_notification='lue'";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, demandeId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des notifications lues: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer le nombre de notifications non lues
     */
    public int getUnreadCount(int demandeId) {
        String sql = "SELECT COUNT(*) FROM notification_recompense WHERE demande_id=? AND statut_notification='non_lue'";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, demandeId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du comptage des notifications: " + e.getMessage());
        }
        return 0;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}

