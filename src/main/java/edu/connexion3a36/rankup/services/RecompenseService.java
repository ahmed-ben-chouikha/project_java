package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.Recompense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecompenseService {
    private Connection cnx;

    public RecompenseService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    /**
     * Ajouter une nouvelle récompense
     */
    public boolean add(Recompense recompense) {
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
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier une récompense existante
     */
    public boolean update(Recompense recompense) {
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
            System.out.println("Erreur lors de la modification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer une récompense
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM recompense WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
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

