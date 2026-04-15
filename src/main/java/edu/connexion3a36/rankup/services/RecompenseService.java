package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.rankup.entities.Tournament;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecompenseService {
    private Connection cnx;
    private static final List<String> ALLOWED_TYPES = Arrays.asList("Medaille", "Argent", "Trophee", "Accessoir PC");
    private String lastErrorMessage = "";

    public RecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Ajouter une nouvelle récompense
     */
    public boolean add(Recompense recompense) {
        lastErrorMessage = "";
        if (!isValidRecompense(recompense)) {
            lastErrorMessage = "Donnees de recompense invalides.";
            return false;
        }
        String sql = "INSERT INTO recompense (recompense, type, classement, description, tournament_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, recompense.getRecompense());
            pst.setString(2, recompense.getType());
            pst.setInt(3, recompense.getClassement());
            pst.setString(4, recompense.getDescription());
            pst.setInt(5, recompense.getTournamentId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier une récompense existante
     */
    public boolean update(Recompense recompense) {
        lastErrorMessage = "";
        if (!isValidRecompense(recompense)) {
            lastErrorMessage = "Donnees de recompense invalides.";
            return false;
        }
        String sql = "UPDATE recompense SET recompense=?, type=?, classement=?, description=?, tournament_id=? " +
                "WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, recompense.getRecompense());
            pst.setString(2, recompense.getType());
            pst.setInt(3, recompense.getClassement());
            pst.setString(4, recompense.getDescription());
            pst.setInt(5, recompense.getTournamentId());
            pst.setInt(6, recompense.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer une récompense
     */
    public boolean delete(int id) {
        lastErrorMessage = "";
        boolean initialAutoCommit;
        try {
            initialAutoCommit = cnx.getAutoCommit();
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }

        String detachSql = "UPDATE demande_recompense SET recompense_id = NULL WHERE recompense_id = ?";
        String deleteLinkedDemandesSql = "DELETE FROM demande_recompense WHERE recompense_id = ?";
        String deleteSql = "DELETE FROM recompense WHERE id=?";

        try {
            cnx.setAutoCommit(false);

            if (isRecompenseIdNullable()) {
                try (PreparedStatement detachPst = cnx.prepareStatement(detachSql)) {
                    detachPst.setInt(1, id);
                    detachPst.executeUpdate();
                }
            } else {
                try (PreparedStatement deleteDemandesPst = cnx.prepareStatement(deleteLinkedDemandesSql)) {
                    deleteDemandesPst.setInt(1, id);
                    deleteDemandesPst.executeUpdate();
                }
            }

            try (PreparedStatement deletePst = cnx.prepareStatement(deleteSql)) {
                deletePst.setInt(1, id);
                boolean deleted = deletePst.executeUpdate() > 0;
                cnx.commit();
                return deleted;
            }
        } catch (SQLException e) {
            try {
                cnx.rollback();
            } catch (SQLException ignored) {
                // Ignore rollback secondary failure.
            }
            lastErrorMessage = e.getMessage();
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        } finally {
            try {
                cnx.setAutoCommit(initialAutoCommit);
            } catch (SQLException ignored) {
                // Ignore restore failure.
            }
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Récupérer une récompense par ID
     */
    public Recompense getById(int id) {
        String sql = "SELECT * FROM recompense WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToRecompense(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer toutes les récompenses
     */
    public List<Recompense> getAll() {
        List<Recompense> recompenses = new ArrayList<>();
        String sql = "SELECT * FROM recompense";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                recompenses.add(mapResultSetToRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return recompenses;
    }

    /**
     * Récupérer les récompenses par tournoi
     */
    public List<Recompense> getByTournament(int tournamentId) {
        List<Recompense> recompenses = new ArrayList<>();
        String sql = "SELECT * FROM recompense WHERE tournament_id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, tournamentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                recompenses.add(mapResultSetToRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par tournoi: " + e.getMessage());
        }
        return recompenses;
    }

    /**
     * Chercher les récompenses par type
     */
    public List<Recompense> searchByType(String type) {
        List<Recompense> recompenses = new ArrayList<>();
        String sql = "SELECT * FROM recompense WHERE type LIKE ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, "%" + type + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                recompenses.add(mapResultSetToRecompense(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return recompenses;
    }

    public List<String> getAllowedTypes() {
        return new ArrayList<>(ALLOWED_TYPES);
    }

    public List<Tournament> getAllTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT id, name FROM tournament ORDER BY name";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Tournament tournament = new Tournament();
                tournament.setId(rs.getInt("id"));
                tournament.setName(rs.getString("name"));
                tournaments.add(tournament);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des tournois: " + e.getMessage());
        }
        return tournaments;
    }

    public Map<Integer, String> getTournamentNameMap() {
        Map<Integer, String> tournamentNameMap = new HashMap<>();
        for (Tournament tournament : getAllTournaments()) {
            tournamentNameMap.put(tournament.getId(), tournament.getName());
        }
        return tournamentNameMap;
    }

    public boolean tournamentExists(int tournamentId) {
        String sql = "SELECT 1 FROM tournament WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, tournamentId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean isValidRecompense(Recompense recompense) {
        if (recompense == null) {
            return false;
        }
        String label = recompense.getRecompense() == null ? "" : recompense.getRecompense().trim();
        if (label.isEmpty() || label.length() > 30) {
            return false;
        }
        if (recompense.getType() == null || !ALLOWED_TYPES.contains(recompense.getType())) {
            return false;
        }
        if (recompense.getClassement() <= 0) {
            return false;
        }

        String description = recompense.getDescription() == null ? "" : recompense.getDescription().trim();
        if (!description.isEmpty() && description.length() <= 10) {
            return false;
        }

        return tournamentExists(recompense.getTournamentId());
    }

    private boolean isRecompenseIdNullable() {
        String sql = "SELECT IS_NULLABLE FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'demande_recompense' AND COLUMN_NAME = 'recompense_id'";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return "YES".equalsIgnoreCase(rs.getString("IS_NULLABLE"));
            }
        } catch (SQLException ignored) {
            // Fallback below.
        }
        return false;
    }

    /**
     * Mapper les résultats SQL vers une entité Recompense
     */
    private Recompense mapResultSetToRecompense(ResultSet rs) throws SQLException {
        Recompense recompense = new Recompense();
        recompense.setId(rs.getInt("id"));
        recompense.setRecompense(rs.getString("recompense"));
        recompense.setType(rs.getString("type"));
        recompense.setClassement(rs.getInt("classement"));
        recompense.setDescription(rs.getString("description"));
        recompense.setTournamentId(rs.getInt("tournament_id"));
        return recompense;
    }
}

