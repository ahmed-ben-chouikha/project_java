package edu.connexion3a36.services;

import edu.connexion3a36.entities.AdminResponse;
import edu.connexion3a36.entities.Punition;
import edu.connexion3a36.entities.Reclamation;
import edu.connexion3a36.interfaces.IService;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminResponseService implements IService<AdminResponse> {

    public static class ReclamationLookup {
        private final int id;
        private final String titre;

        public ReclamationLookup(int id, String titre) {
            this.id = id;
            this.titre = titre;
        }

        public int getId() {
            return id;
        }

        public String getTitre() {
            return titre;
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        return connection;
    }

    private void validate(AdminResponse adminResponse) {
        if (adminResponse == null) {
            throw new IllegalArgumentException("AdminResponse cannot be null");
        }
        if (adminResponse.getMessage() == null || adminResponse.getMessage().isBlank()) {
            throw new IllegalArgumentException("AdminResponse message is required");
        }
        if (adminResponse.getReclamationId() <= 0) {
            throw new IllegalArgumentException("AdminResponse reclamationId is required");
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private Reclamation mapReclamation(ResultSet rs, String prefix) throws SQLException {
        int reclamationId = rs.getInt(prefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setId(reclamationId);
        reclamation.setTitre(rs.getString(prefix + "titre"));
        reclamation.setDescription(rs.getString(prefix + "description"));
        reclamation.setType(rs.getString(prefix + "type"));
        reclamation.setEtat(rs.getString(prefix + "etat"));

        Timestamp createdAt = rs.getTimestamp(prefix + "created_at");
        if (createdAt != null) {
            reclamation.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp(prefix + "updated_at");
        if (updatedAt != null) {
            reclamation.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        reclamation.setAttachmentFilename(rs.getString(prefix + "attachment_filename"));
        int playerId = rs.getInt(prefix + "player_id");
        reclamation.setPlayerId(rs.wasNull() ? null : playerId);
        return reclamation;
    }

    @Override
    public void addEntity(AdminResponse adminResponse) throws SQLException {
        validate(adminResponse);

        String query = "INSERT INTO admin_response (message, created_at, updated_at, reclamation_id) VALUES (?, ?, ?, ?)";
        LocalDateTime createdAt = adminResponse.getCreatedAt() != null ? adminResponse.getCreatedAt() : LocalDateTime.now();
        adminResponse.setCreatedAt(createdAt);

        try (PreparedStatement pst = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, adminResponse.getMessage());
            pst.setTimestamp(2, toTimestamp(createdAt));
            pst.setTimestamp(3, toTimestamp(adminResponse.getUpdatedAt()));
            pst.setInt(4, adminResponse.getReclamationId());
            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    adminResponse.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void deleteEntity(AdminResponse adminResponse) throws SQLException {
        if (adminResponse == null || adminResponse.getId() <= 0) {
            throw new SQLException("AdminResponse id is required for deletion");
        }

        String query = "DELETE FROM admin_response WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, adminResponse.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void updateEntity(int id, AdminResponse adminResponse) throws SQLException {
        validate(adminResponse);

        String query = "UPDATE admin_response SET message = ?, updated_at = ?, reclamation_id = ? WHERE id = ?";
        adminResponse.setUpdatedAt(LocalDateTime.now());

        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setString(1, adminResponse.getMessage());
            pst.setTimestamp(2, toTimestamp(adminResponse.getUpdatedAt()));
            pst.setInt(3, adminResponse.getReclamationId());
            pst.setInt(4, id);
            pst.executeUpdate();
        }
    }

    @Override
    public List<AdminResponse> getData() throws SQLException {
        List<AdminResponse> data = new ArrayList<>();
        String query = "SELECT id, message, created_at, updated_at, reclamation_id FROM admin_response "
                + "ORDER BY COALESCE(updated_at, created_at) DESC, id DESC";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                AdminResponse adminResponse = new AdminResponse();
                adminResponse.setId(rs.getInt("id"));
                adminResponse.setMessage(rs.getString("message"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    adminResponse.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    adminResponse.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                adminResponse.setReclamationId(rs.getInt("reclamation_id"));
                data.add(adminResponse);
            }
        }

        return data;
    }

    public List<AdminResponse> getDataWithReclamation() throws SQLException {
        List<AdminResponse> data = new ArrayList<>();
        String query = "SELECT ar.id, ar.message, ar.created_at, ar.updated_at, ar.reclamation_id, "
                + "r.id AS r_id, r.titre AS r_titre, r.description AS r_description, r.type AS r_type, r.etat AS r_etat, "
                + "r.created_at AS r_created_at, r.updated_at AS r_updated_at, r.attachment_filename AS r_attachment_filename, r.player_id AS r_player_id "
                + "FROM admin_response ar "
                + "INNER JOIN reclamation r ON r.id = ar.reclamation_id "
                + "ORDER BY COALESCE(ar.updated_at, ar.created_at) DESC, ar.id DESC";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                AdminResponse adminResponse = new AdminResponse();
                adminResponse.setId(rs.getInt("id"));
                adminResponse.setMessage(rs.getString("message"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    adminResponse.setCreatedAt(createdAt.toLocalDateTime());
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    adminResponse.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                adminResponse.setReclamationId(rs.getInt("reclamation_id"));
                adminResponse.setReclamation(mapReclamation(rs, "r_"));
                data.add(adminResponse);
            }
        }

        return data;
    }

    public List<ReclamationLookup> getReclamationChoices() throws SQLException {
        List<ReclamationLookup> choices = new ArrayList<>();
        String query = "SELECT id, titre FROM reclamation ORDER BY id";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                choices.add(new ReclamationLookup(rs.getInt("id"), rs.getString("titre")));
            }
        }
        return choices;
    }

    public Reclamation getReclamationById(int reclamationId) throws SQLException {
        String query = "SELECT id, titre, description, type, etat, created_at, updated_at, attachment_filename, player_id "
                + "FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapReclamation(rs, "");
            }
        }
    }

    public Punition getLatestPunitionByReclamationId(int reclamationId) throws SQLException {
        String query = "SELECT id, start_at, end_at, player_status, reclamation_id "
                + "FROM punition WHERE reclamation_id = ? "
                + "ORDER BY start_at DESC, id DESC LIMIT 1";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Punition punition = new Punition();
                punition.setId(rs.getInt("id"));
                Timestamp startAt = rs.getTimestamp("start_at");
                if (startAt != null) {
                    punition.setStartAt(startAt.toLocalDateTime());
                }
                Timestamp endAt = rs.getTimestamp("end_at");
                if (endAt != null) {
                    punition.setEndAt(endAt.toLocalDateTime());
                }
                punition.setPlayerStatus(rs.getString("player_status"));
                punition.setReclamationId(rs.getInt("reclamation_id"));
                return punition;
            }
        }
    }
}




