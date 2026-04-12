package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DemandeRecompenseService {
    private Connection cnx;

    public DemandeRecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Ajouter une nouvelle demande de récompense
     */
    public boolean add(DemandeRecompense demande) {
        String sql = "INSERT INTO demande_recompense (nom_demandeur, email, motif, date_demande, statut) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, demande.getNomDemandeur());
            pst.setString(2, demande.getEmail());
            pst.setString(3, demande.getMotif());
            pst.setTimestamp(4, Timestamp.valueOf(demande.getDateDemande()));
            pst.setString(5, demande.getStatut());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier une demande existante
     */
    public boolean update(DemandeRecompense demande) {
        String sql = "UPDATE demande_recompense SET nom_demandeur=?, email=?, motif=?, date_demande=?, statut=? " +
                "WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, demande.getNomDemandeur());
            pst.setString(2, demande.getEmail());
            pst.setString(3, demande.getMotif());
            pst.setTimestamp(4, Timestamp.valueOf(demande.getDateDemande()));
            pst.setString(5, demande.getStatut());
            pst.setInt(6, demande.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer une demande
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM demande_recompense WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer une demande par ID
     */
    public DemandeRecompense getById(int id) {
        String sql = "SELECT * FROM demande_recompense WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToDemandeRecompense(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer toutes les demandes
     */
    public List<DemandeRecompense> getAll() {
        List<DemandeRecompense> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande_recompense ORDER BY date_demande DESC";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return demandes;
    }

    /**
     * Récupérer les demandes par statut
     */
    public List<DemandeRecompense> getByStatut(String statut) {
        List<DemandeRecompense> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande_recompense WHERE statut=? ORDER BY date_demande DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, statut);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par statut: " + e.getMessage());
        }
        return demandes;
    }

    /**
     * Chercher les demandes par nom ou email
     */
    public List<DemandeRecompense> search(String keyword) {
        List<DemandeRecompense> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande_recompense WHERE nom_demandeur LIKE ? OR email LIKE ? ORDER BY date_demande DESC";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                demandes.add(mapResultSetToDemandeRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return demandes;
    }

    /**
     * Mapper les résultats SQL vers une entité DemandeRecompense
     */
    private DemandeRecompense mapResultSetToDemandeRecompense(ResultSet rs) throws SQLException {
        DemandeRecompense demande = new DemandeRecompense();
        demande.setId(rs.getInt("id"));
        demande.setNomDemandeur(rs.getString("nom_demandeur"));
        demande.setEmail(rs.getString("email"));
        demande.setMotif(rs.getString("motif"));
        demande.setDateDemande(rs.getTimestamp("date_demande").toLocalDateTime());
        demande.setStatut(rs.getString("statut"));
        return demande;
    }
}

