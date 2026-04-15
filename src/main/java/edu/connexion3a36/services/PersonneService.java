package edu.connexion3a36.services;

import edu.connexion3a36.entities.Personne;
import edu.connexion3a36.interfaces.IService;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements IService<Personne> {

    private Connection getConnection() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        return connection;
    }

    @Override
    public void addEntity(Personne personne) throws SQLException {
        insertPersonne(personne);
    }

    public void addEntity2(Personne personne) throws SQLException {
        insertPersonne(personne);

    }

    private void validatePersonne(Personne personne) {
        if (personne == null) {
            throw new IllegalArgumentException("Personne cannot be null");
        }

        if (personne.getNom() == null || personne.getNom().isBlank()) {
            throw new IllegalArgumentException("Personne nom is required");
        }

        if (personne.getPrenom() == null || personne.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Personne prenom is required");
        }
    }

    private void insertPersonne(Personne personne) throws SQLException {
        validatePersonne(personne);

        String requete = "INSERT INTO personne (nom, prenom) VALUES (?, ?)";

        try (PreparedStatement pst = getConnection().prepareStatement(requete, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, personne.getNom());
            pst.setString(2, personne.getPrenom());
            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    personne.setId(generatedKeys.getInt(1));
                }
            }
        }

        System.out.println("Personne added");

    }

    @Override
    public void deleteEntity(Personne personne) throws SQLException {
        if (personne == null || personne.getId() <= 0) {
            throw new SQLException("Personne id is required for deletion");
        }

        String requete = "DELETE FROM personne WHERE id = ?";

        try (PreparedStatement pst = getConnection().prepareStatement(requete)) {
            pst.setInt(1, personne.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void updateEntity(int id, Personne personne) throws SQLException {
        validatePersonne(personne);

        String requete = "UPDATE personne SET nom = ?, prenom = ? WHERE id = ?";

        try (PreparedStatement pst = getConnection().prepareStatement(requete)) {
            pst.setString(1, personne.getNom());
            pst.setString(2, personne.getPrenom());
            pst.setInt(3, id);
            pst.executeUpdate();
        }
    }

    @Override
    public List<Personne> getData() throws SQLException {
        List<Personne> data = new ArrayList<>();
        String requete = "SELECT id, nom, prenom FROM personne";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(requete)) {
            while (rs.next()){
                Personne p = new Personne();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setPrenom(rs.getString("prenom"));
                data.add(p);
            }
        }
        return data;
    }
}
