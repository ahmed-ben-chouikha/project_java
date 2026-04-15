package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DemandeRecompenseService {
    private final Connection cnx;
    private final boolean hasUserIdColumn;
    private final boolean hasRecompenseIdColumn;
    private String lastErrorMessage = "";

    public DemandeRecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
        hasUserIdColumn = hasColumn("demande_recompense", "user_id");
        hasRecompenseIdColumn = hasColumn("demande_recompense", "recompense_id");
    }

    /**
     * Ajouter une nouvelle demande de récompense
     */
    public boolean add(DemandeRecompense demande) {
        lastErrorMessage = "";
        if (!isValidDemande(demande)) {
            if (lastErrorMessage.isEmpty()) {
                lastErrorMessage = "Donnees de demande invalides.";
            }
            return false;
        }

        if (hasReachedDemandLimit(demande)) {
            lastErrorMessage = "Un demandeur ne peut pas depasser 3 demandes.";
            return false;
        }

        String sql;
        if (hasUserIdColumn && hasRecompenseIdColumn) {
            sql = "INSERT INTO demande_recompense (user_id, nom_demandeur, email, motif, date_demande, statut, recompense_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else if (hasUserIdColumn) {
            sql = "INSERT INTO demande_recompense (user_id, nom_demandeur, email, motif, date_demande, statut) VALUES (?, ?, ?, ?, ?, ?)";
        } else if (hasRecompenseIdColumn) {
            sql = "INSERT INTO demande_recompense (nom_demandeur, email, motif, date_demande, statut, recompense_id) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO demande_recompense (nom_demandeur, email, motif, date_demande, statut) VALUES (?, ?, ?, ?, ?)";
        }
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            int index = 1;
            if (hasUserIdColumn) {
                if (demande.getUserId() == null) {
                    pst.setNull(index, Types.INTEGER);
                } else {
                    pst.setInt(index, demande.getUserId());
                }
                index++;
            }
            pst.setString(index++, demande.getNomDemandeur());
            pst.setString(index++, demande.getEmail());
            pst.setString(index++, demande.getMotif());
            pst.setTimestamp(index++, Timestamp.valueOf(demande.getDateDemande()));
            pst.setString(index++, normalizeStatus(demande.getStatut()));
            if (hasRecompenseIdColumn) {
                if (demande.getRecompenseId() == null) {
                    pst.setNull(index, Types.INTEGER);
                } else {
                    pst.setInt(index, demande.getRecompenseId());
                }
            }
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier une demande existante
     */
    public boolean update(DemandeRecompense demande) {
        lastErrorMessage = "";
        if (!isValidDemande(demande)) {
            if (lastErrorMessage.isEmpty()) {
                lastErrorMessage = "Donnees de demande invalides.";
            }
            return false;
        }
        String sql;
        if (hasUserIdColumn && hasRecompenseIdColumn) {
            sql = "UPDATE demande_recompense SET user_id=?, nom_demandeur=?, email=?, motif=?, date_demande=?, statut=?, recompense_id=? WHERE id=?";
        } else if (hasUserIdColumn) {
            sql = "UPDATE demande_recompense SET user_id=?, nom_demandeur=?, email=?, motif=?, date_demande=?, statut=? WHERE id=?";
        } else if (hasRecompenseIdColumn) {
            sql = "UPDATE demande_recompense SET nom_demandeur=?, email=?, motif=?, date_demande=?, statut=?, recompense_id=? WHERE id=?";
        } else {
            sql = "UPDATE demande_recompense SET nom_demandeur=?, email=?, motif=?, date_demande=?, statut=? WHERE id=?";
        }
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            int index = 1;
            if (hasUserIdColumn) {
                if (demande.getUserId() == null) {
                    pst.setNull(index, Types.INTEGER);
                } else {
                    pst.setInt(index, demande.getUserId());
                }
                index++;
            }
            pst.setString(index++, demande.getNomDemandeur());
            pst.setString(index++, demande.getEmail());
            pst.setString(index++, demande.getMotif());
            pst.setTimestamp(index++, Timestamp.valueOf(demande.getDateDemande()));
            pst.setString(index++, normalizeStatus(demande.getStatut()));
            if (hasRecompenseIdColumn) {
                if (demande.getRecompenseId() == null) {
                    pst.setNull(index++, Types.INTEGER);
                } else {
                    pst.setInt(index++, demande.getRecompenseId());
                }
            }
            pst.setInt(index, demande.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            return false;
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
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
            pst.setString(1, normalizeStatus(statut));
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
        if (hasUserIdColumn) {
            Object userId = rs.getObject("user_id");
            demande.setUserId(userId == null ? null : ((Number) userId).intValue());
        } else {
            demande.setUserId(null);
        }
        demande.setNomDemandeur(rs.getString("nom_demandeur"));
        demande.setEmail(rs.getString("email"));
        demande.setMotif(rs.getString("motif"));
        Timestamp date = rs.getTimestamp("date_demande");
        demande.setDateDemande(date == null ? null : date.toLocalDateTime());
        demande.setStatut(rs.getString("statut"));
        if (hasRecompenseIdColumn) {
            Object recompenseId = rs.getObject("recompense_id");
            demande.setRecompenseId(recompenseId == null ? null : ((Number) recompenseId).intValue());
        } else {
            demande.setRecompenseId(null);
        }
        return demande;
    }

    private boolean isValidDemande(DemandeRecompense demande) {
        if (demande == null) {
            lastErrorMessage = "Demande absente.";
            return false;
        }
        String nom = demande.getNomDemandeur() == null ? "" : demande.getNomDemandeur().trim();
        String email = demande.getEmail() == null ? "" : demande.getEmail().trim();
        String motif = demande.getMotif() == null ? "" : demande.getMotif().trim();
        String statut = demande.getStatut() == null ? "" : demande.getStatut().trim();

        if (nom.isEmpty() || nom.length() > 255) {
            lastErrorMessage = "Nom demandeur invalide.";
            return false;
        }
        if (!nom.matches("^[A-Za-z][A-Za-z'\\-]*\\s+[A-Za-z][A-Za-z'\\-]*$")) {
            lastErrorMessage = "Le nom doit contenir deux parties separees par un espace.";
            return false;
        }
        if (email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lastErrorMessage = "Email invalide.";
            return false;
        }
        if (motif.isEmpty()) {
            lastErrorMessage = "Description obligatoire.";
            return false;
        }
        if (motif.length() < 50 || motif.length() > 1500) {
            lastErrorMessage = "La description doit contenir entre 50 et 1500 caracteres.";
            return false;
        }
        String normalizedStatus = normalizeStatus(statut);
        if (!("en_attente".equals(normalizedStatus) || "approuvee".equals(normalizedStatus) || "rejetee".equals(normalizedStatus))) {
            lastErrorMessage = "Statut invalide.";
            return false;
        }
        if (hasRecompenseIdColumn && (demande.getRecompenseId() == null || demande.getRecompenseId() <= 0)) {
            lastErrorMessage = "Recompense obligatoire.";
            return false;
        }
        if (demande.getDateDemande() == null) {
            lastErrorMessage = "Date de demande obligatoire.";
            return false;
        }
        return true;
    }

    private boolean hasReachedDemandLimit(DemandeRecompense demande) {
        String sql;
        boolean byUserId = hasUserIdColumn && demande.getUserId() != null;
        if (byUserId) {
            sql = "SELECT COUNT(*) FROM demande_recompense WHERE user_id = ?";
        } else {
            sql = "SELECT COUNT(*) FROM demande_recompense WHERE LOWER(email) = LOWER(?)";
        }

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            if (byUserId) {
                pst.setInt(1, demande.getUserId());
            } else {
                pst.setString(1, demande.getEmail());
            }
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) >= 3;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return true;
        }
        return false;
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "en_attente";
        }
        String normalized = status.trim().toLowerCase();
        if (normalized.equals("en attente") || normalized.equals("en_attente")) {
            return "en_attente";
        }
        if (normalized.equals("approuvee") || normalized.equals("approuvée")) {
            return "approuvee";
        }
        if (normalized.equals("rejetee") || normalized.equals("rejetée")) {
            return "rejetee";
        }
        return normalized;
    }

    private boolean hasColumn(String tableName, String columnName) {
        String sql = "SELECT 1 FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, tableName);
            pst.setString(2, columnName);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}

