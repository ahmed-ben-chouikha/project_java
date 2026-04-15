package edu.connexion3a36.services;

import edu.connexion3a36.entities.Depense;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepenseService {

    private Connection cnx;

    public DepenseService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    // CREATE
    public boolean addDepense(Depense depense) {
        String sql = "INSERT INTO depense (titre, montant, description, date_creation, statut, categorie, team_id, facture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, depense.getTitre());
            pst.setFloat(2, depense.getMontant());
            pst.setString(3, depense.getDescription());
            pst.setTimestamp(4, Timestamp.valueOf(depense.getDateCreation()));
            pst.setString(5, depense.getStatut());
            pst.setString(6, depense.getCategorie());
            if (depense.getTeamId() != null) {
                pst.setInt(7, depense.getTeamId());
            } else {
                pst.setNull(7, Types.INTEGER);
            }
            pst.setString(8, depense.getFacture());
            pst.executeUpdate();
            System.out.println("Depense added successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding depense: " + e.getMessage());
            return false;
        }
    }

    // READ - Get all depenses
    public List<Depense> getAllDepenses() {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture FROM depense d LEFT JOIN team t ON d.team_id = t.id ORDER BY d.date_creation DESC";
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
                depenses.add(depense);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching depenses: " + e.getMessage());
        }
        return depenses;
    }

    // READ - Get depense by ID
    public Depense getDepenseById(int id) {
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture FROM depense d LEFT JOIN team t ON d.team_id = t.id WHERE d.id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Depense(
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
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture FROM depense d LEFT JOIN team t ON d.team_id = t.id WHERE d.team_id = ? ORDER BY d.date_creation DESC";
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
        String sql = "UPDATE depense SET titre = ?, montant = ?, description = ?, statut = ?, categorie = ?, facture = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, depense.getTitre());
            pst.setFloat(2, depense.getMontant());
            pst.setString(3, depense.getDescription());
            pst.setString(4, depense.getStatut());
            pst.setString(5, depense.getCategorie());
            pst.setString(6, depense.getFacture());
            pst.setInt(7, depense.getId());
            pst.executeUpdate();
            System.out.println("Depense updated successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating depense: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteDepense(int id) {
        String sql = "DELETE FROM depense WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Depense deleted successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting depense: " + e.getMessage());
            return false;
        }
    }

    // Get depenses by status
    public List<Depense> getDepensesByStatus(String statut) {
        List<Depense> depenses = new ArrayList<>();
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture FROM depense d LEFT JOIN team t ON d.team_id = t.id WHERE d.statut = ? ORDER BY d.date_creation DESC";
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
        String sql = "SELECT d.id, d.titre, d.montant, d.description, d.date_creation, d.statut, d.categorie, d.team_id, t.name as team_name, d.facture FROM depense d LEFT JOIN team t ON d.team_id = t.id WHERE d.categorie = ? ORDER BY d.date_creation DESC";
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

