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
public class ReclamationService implements IService<Reclamation> {
    private static final String TYPE_JOUEUR = "JOUEUR";
    private static final String TYPE_TECHNIQUE = "TECHNIQUE";
    public static class PlayerLookup {
        private final Integer id;
        private final String nickname;
        public PlayerLookup(Integer id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }
        public Integer getId() {
            return id;
        }
        public String getNickname() {
            return nickname;
        }
    }
    private Connection getConnection() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available");
        }
        return connection;
    }
    private void validate(Reclamation reclamation) {
        if (reclamation == null) {
            throw new IllegalArgumentException("Reclamation cannot be null");
        }
        if (reclamation.getTitre() == null || reclamation.getTitre().isBlank()) {
            throw new IllegalArgumentException("Reclamation titre is required");
        }
        if (reclamation.getDescription() == null || reclamation.getDescription().isBlank()) {
            throw new IllegalArgumentException("Reclamation description is required");
        }
        if (reclamation.getType() == null || reclamation.getType().isBlank()) {
            throw new IllegalArgumentException("Reclamation type is required");
        }
        String normalizedType = reclamation.getType().trim().toUpperCase();
        if (!TYPE_JOUEUR.equals(normalizedType) && !TYPE_TECHNIQUE.equals(normalizedType)) {
            throw new IllegalArgumentException("Reclamation type must be JOUEUR or TECHNIQUE");
        }
        if (TYPE_JOUEUR.equals(normalizedType) && reclamation.getPlayerId() == null) {
            throw new IllegalArgumentException("Player is required when type is JOUEUR");
        }
        if (TYPE_TECHNIQUE.equals(normalizedType) && reclamation.getPlayerId() != null) {
            throw new IllegalArgumentException("Player must be empty when type is TECHNIQUE");
        }
        if (reclamation.getEtat() == null || reclamation.getEtat().isBlank()) {
            throw new IllegalArgumentException("Reclamation etat is required");
        }
    }
    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }
    private Reclamation mapReclamation(ResultSet rs) throws SQLException {
        Reclamation reclamation = new Reclamation();
        reclamation.setId(rs.getInt("id"));
        reclamation.setTitre(rs.getString("titre"));
        reclamation.setDescription(rs.getString("description"));
        reclamation.setType(rs.getString("type"));
        reclamation.setEtat(rs.getString("etat"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            reclamation.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            reclamation.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        reclamation.setAttachmentFilename(rs.getString("attachment_filename"));
        int playerId = rs.getInt("player_id");
        reclamation.setPlayerId(rs.wasNull() ? null : playerId);
        return reclamation;
    }
    @Override
    public void addEntity(Reclamation reclamation) throws SQLException {
        validate(reclamation);
        String query = "INSERT INTO reclamation (titre, description, type, etat, created_at, updated_at, attachment_filename, player_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        LocalDateTime createdAt = reclamation.getCreatedAt() != null ? reclamation.getCreatedAt() : LocalDateTime.now();
        reclamation.setCreatedAt(createdAt);
        try (PreparedStatement pst = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, reclamation.getTitre());
            pst.setString(2, reclamation.getDescription());
            pst.setString(3, reclamation.getType());
            pst.setString(4, reclamation.getEtat());
            pst.setTimestamp(5, toTimestamp(createdAt));
            pst.setTimestamp(6, toTimestamp(reclamation.getUpdatedAt()));
            pst.setString(7, reclamation.getAttachmentFilename());
            if (reclamation.getPlayerId() == null) {
                pst.setNull(8, java.sql.Types.INTEGER);
            } else {
                pst.setInt(8, reclamation.getPlayerId());
            }
            pst.executeUpdate();
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reclamation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    @Override
    public void deleteEntity(Reclamation reclamation) throws SQLException {
        if (reclamation == null || reclamation.getId() <= 0) {
            throw new SQLException("Reclamation id is required for deletion");
        }
        String query = "DELETE FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamation.getId());
            pst.executeUpdate();
        }
    }
    @Override
    public void updateEntity(int id, Reclamation reclamation) throws SQLException {
        validate(reclamation);
        String query = "UPDATE reclamation SET titre = ?, description = ?, type = ?, etat = ?, updated_at = ?, attachment_filename = ?, player_id = ? WHERE id = ?";
        reclamation.setUpdatedAt(LocalDateTime.now());
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setString(1, reclamation.getTitre());
            pst.setString(2, reclamation.getDescription());
            pst.setString(3, reclamation.getType());
            pst.setString(4, reclamation.getEtat());
            pst.setTimestamp(5, toTimestamp(reclamation.getUpdatedAt()));
            pst.setString(6, reclamation.getAttachmentFilename());
            if (reclamation.getPlayerId() == null) {
                pst.setNull(7, java.sql.Types.INTEGER);
            } else {
                pst.setInt(7, reclamation.getPlayerId());
            }
            pst.setInt(8, id);
            pst.executeUpdate();
        }
    }
    @Override
    public List<Reclamation> getData() throws SQLException {
        List<Reclamation> data = new ArrayList<>();
        String query = "SELECT id, titre, description, type, etat, created_at, updated_at, attachment_filename, player_id FROM reclamation ORDER BY COALESCE(updated_at, created_at) DESC, id DESC";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                data.add(mapReclamation(rs));
            }
        }
        return data;
    }
    public List<Reclamation> getDataWithRelations() throws SQLException {
        List<Reclamation> data = new ArrayList<>();
        String query = "SELECT r.id, r.titre, r.description, r.type, r.etat, r.created_at, r.updated_at, r.attachment_filename, r.player_id, "
                + "ar.id AS ar_id, ar.message, ar.created_at AS ar_created_at, ar.updated_at AS ar_updated_at, ar.reclamation_id AS ar_reclamation_id, "
                + "p.id AS p_id, p.start_at, p.end_at, p.player_status, p.reclamation_id AS p_reclamation_id "
                + "FROM reclamation r "
                + "LEFT JOIN admin_response ar ON ar.reclamation_id = r.id "
                + "LEFT JOIN punition p ON p.reclamation_id = r.id "
                + "ORDER BY COALESCE(r.updated_at, r.created_at) DESC, r.id DESC";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setTitre(rs.getString("titre"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setType(rs.getString("type"));
                reclamation.setEtat(rs.getString("etat"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    reclamation.setCreatedAt(createdAt.toLocalDateTime());
                }
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    reclamation.setUpdatedAt(updatedAt.toLocalDateTime());
                }
                reclamation.setAttachmentFilename(rs.getString("attachment_filename"));
                int playerId = rs.getInt("player_id");
                reclamation.setPlayerId(rs.wasNull() ? null : playerId);
                int adminResponseId = rs.getInt("ar_id");
                if (!rs.wasNull()) {
                    AdminResponse adminResponse = new AdminResponse();
                    adminResponse.setId(adminResponseId);
                    adminResponse.setMessage(rs.getString("message"));
                    Timestamp arCreated = rs.getTimestamp("ar_created_at");
                    if (arCreated != null) {
                        adminResponse.setCreatedAt(arCreated.toLocalDateTime());
                    }
                    Timestamp arUpdated = rs.getTimestamp("ar_updated_at");
                    if (arUpdated != null) {
                        adminResponse.setUpdatedAt(arUpdated.toLocalDateTime());
                    }
                    adminResponse.setReclamationId(rs.getInt("ar_reclamation_id"));
                    adminResponse.setReclamation(reclamation);
                    reclamation.setAdminResponse(adminResponse);
                }
                int punitionId = rs.getInt("p_id");
                if (!rs.wasNull()) {
                    Punition punition = new Punition();
                    punition.setId(punitionId);
                    Timestamp pStart = rs.getTimestamp("start_at");
                    if (pStart != null) {
                        punition.setStartAt(pStart.toLocalDateTime());
                    }
                    Timestamp pEnd = rs.getTimestamp("end_at");
                    if (pEnd != null) {
                        punition.setEndAt(pEnd.toLocalDateTime());
                    }
                    punition.setPlayerStatus(rs.getString("player_status"));
                    punition.setReclamationId(rs.getInt("p_reclamation_id"));
                    punition.setReclamation(reclamation);
                    reclamation.setPunition(punition);
                }
                data.add(reclamation);
            }
        }
        return data;
    }
    public List<PlayerLookup> getAvailablePlayers() throws SQLException {
        List<String> queries = List.of(
                "SELECT id, nickname FROM player ORDER BY id",
                "SELECT id, username AS nickname FROM player ORDER BY id",
                "SELECT id, CONCAT(nom, ' ', prenom) AS nickname FROM personne ORDER BY id",
                "SELECT id, nom AS nickname FROM personne ORDER BY id"
        );
        for (String query : queries) {
            try (Statement st = getConnection().createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                List<PlayerLookup> players = new ArrayList<>();
                while (rs.next()) {
                    players.add(new PlayerLookup(rs.getInt("id"), rs.getString("nickname")));
                }
                return players;
            } catch (SQLException ignored) {
                // Try the next query fallback.
            }
        }
        return new ArrayList<>();
    }
    public String getLatestAdminResponseMessage(int reclamationId) throws SQLException {
        String query = "SELECT message FROM admin_response WHERE reclamation_id = ? ORDER BY COALESCE(updated_at, created_at) DESC, id DESC LIMIT 1";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("message");
                }
            }
        }
        return null;
    }
    public Integer getLatestAdminResponseId(int reclamationId) throws SQLException {
        String query = "SELECT id FROM admin_response WHERE reclamation_id = ? ORDER BY COALESCE(updated_at, created_at) DESC, id DESC LIMIT 1";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }
}

