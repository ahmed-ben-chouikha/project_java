package edu.connexion3a36.services;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunitionService implements IService<Punition> {

    private static final String STATUS_MATCH = "banned from match";
    private static final String STATUS_TOURNAMENT = "banned from tournament";
    private static final String STATUS_GAME = "banned from game";
    private static final String ETAT_REJETE = "REJETE";
    private static final String ETAT_APPROUVE = "APPROUVE";

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

    private void validate(Punition punition) {
        if (punition == null) {
            throw new IllegalArgumentException("Punition cannot be null");
        }
        if (punition.getStartAt() == null) {
            throw new IllegalArgumentException("Punition startAt is required");
        }
        if (punition.getEndAt() == null) {
            throw new IllegalArgumentException("Punition endAt is required");
        }
        if (punition.getPlayerStatus() == null || punition.getPlayerStatus().isBlank()) {
            throw new IllegalArgumentException("Punition playerStatus is required");
        }
        String normalizedStatus = punition.getPlayerStatus().trim().toLowerCase();
        if (!STATUS_MATCH.equals(normalizedStatus)
                && !STATUS_TOURNAMENT.equals(normalizedStatus)
                && !STATUS_GAME.equals(normalizedStatus)) {
            throw new IllegalArgumentException("Punition status must be banned from match, banned from tournament or banned from game");
        }
        if (punition.getReclamationId() <= 0) {
            throw new IllegalArgumentException("Punition reclamationId is required");
        }
    }

    private Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private String getReclamationEtat(int reclamationId) throws SQLException {
        String query = "SELECT etat FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, reclamationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Reclamation introuvable pour la punition.");
                }
                return rs.getString("etat");
            }
        }
    }

    private void ensureReclamationCanBePunished(int reclamationId) throws SQLException {
        String etat = getReclamationEtat(reclamationId);
        if (etat != null && ETAT_REJETE.equalsIgnoreCase(etat.trim())) {
            throw new IllegalStateException("Cette reclamation a ete traitee et rejetee!");
        }
    }

    private void markReclamationApproved(int reclamationId) throws SQLException {
        String query = "UPDATE reclamation SET etat = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setString(1, ETAT_APPROUVE);
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setInt(3, reclamationId);
            pst.executeUpdate();
        }
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
    public void addEntity(Punition punition) throws SQLException {
        validate(punition);
        ensureReclamationCanBePunished(punition.getReclamationId());

        String query = "INSERT INTO punition (start_at, end_at, player_status, reclamation_id, is_automatic) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setTimestamp(1, toTimestamp(punition.getStartAt()));
            pst.setTimestamp(2, toTimestamp(punition.getEndAt()));
            pst.setString(3, punition.getPlayerStatus().toLowerCase());
            pst.setInt(4, punition.getReclamationId());
            pst.setBoolean(5, punition.isAutomatic());
            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    punition.setId(generatedKeys.getInt(1));
                }
            }
        }

        markReclamationApproved(punition.getReclamationId());
    }

    @Override
    public void deleteEntity(Punition punition) throws SQLException {
        if (punition == null || punition.getId() <= 0) {
            throw new SQLException("Punition id is required for deletion");
        }

        String query = "DELETE FROM punition WHERE id = ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, punition.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void updateEntity(int id, Punition punition) throws SQLException {
        validate(punition);
        ensureReclamationCanBePunished(punition.getReclamationId());

        String query = "UPDATE punition SET start_at = ?, end_at = ?, player_status = ?, reclamation_id = ?, is_automatic = ? WHERE id = ?";

        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setTimestamp(1, toTimestamp(punition.getStartAt()));
            pst.setTimestamp(2, toTimestamp(punition.getEndAt()));
            pst.setString(3, punition.getPlayerStatus().toLowerCase());
            pst.setInt(4, punition.getReclamationId());
            pst.setBoolean(5, punition.isAutomatic());
            pst.setInt(6, id);
            pst.executeUpdate();
        }

        markReclamationApproved(punition.getReclamationId());
    }

    @Override
    public List<Punition> getData() throws SQLException {
        List<Punition> data = new ArrayList<>();
        String query = "SELECT id, start_at, end_at, player_status, reclamation_id FROM punition ORDER BY start_at DESC, id DESC";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
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
                data.add(punition);
            }
        }

        return data;
    }

    public List<Punition> getDataWithReclamation() throws SQLException {
        List<Punition> data = new ArrayList<>();
        String query = "SELECT p.id, p.start_at, p.end_at, p.player_status, p.reclamation_id, p.is_automatic, "
                + "r.id as rec_id, r.titre as rec_titre, r.description as rec_description, r.type as rec_type, "
                + "r.etat as rec_etat, r.created_at as rec_created_at, r.updated_at as rec_updated_at, "
                + "r.attachment_filename as rec_attachment_filename, r.player_id as rec_player_id "
                + "FROM punition p "
                + "JOIN reclamation r ON p.reclamation_id = r.id "
                + "ORDER BY p.start_at DESC, p.id DESC";

        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
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
                punition.setAutomatic(rs.getBoolean("is_automatic"));
                punition.setReclamation(mapReclamation(rs, "rec_"));
                data.add(punition);
            }
        }

        return data;
    }

    public List<ReclamationLookup> getReclamationChoices() throws SQLException {
        List<ReclamationLookup> choices = new ArrayList<>();
        String query = "SELECT id, titre FROM reclamation ORDER BY COALESCE(created_at, id) DESC, id DESC";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                choices.add(new ReclamationLookup(rs.getInt("id"), rs.getString("titre")));
            }
        }
        return choices;
    }

    public Map<Integer, String> getPlayerNicknamesById() throws SQLException {
        List<String> queries = List.of(
                "SELECT id, nickname FROM player",
                "SELECT id, username AS nickname FROM player",
                "SELECT id, CONCAT(nom, ' ', prenom) AS nickname FROM personne",
                "SELECT id, nom AS nickname FROM personne"
        );

        for (String query : queries) {
            try (Statement st = getConnection().createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                Map<Integer, String> nicknames = new HashMap<>();
                while (rs.next()) {
                    nicknames.put(rs.getInt("id"), rs.getString("nickname"));
                }
                return nicknames;
            } catch (SQLException ignored) {
                // Try next query fallback.
            }
        }

        return new HashMap<>();
    }

    public Integer getLatestAdminResponseIdByReclamationId(int reclamationId) throws SQLException {
        String query = "SELECT id FROM admin_response WHERE reclamation_id = ? "
                + "ORDER BY updated_at DESC, created_at DESC, id DESC LIMIT 1";
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

    /**
     * Get count of bans by status type
     */
    public long getBanCountByStatus(String status) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM punition WHERE LOWER(player_status) LIKE LOWER(?)";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setString(1, "%" + status + "%");
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("count");
                }
            }
        }
        return 0L;
    }

    /**
     * Get the top N most banned players (by count of punitions)
     * Returns a list of [playerId, nickname, banCount]
     */
    public List<Object[]> getMostBannedPlayers(int limit) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String query = "SELECT r.player_id, COUNT(*) as ban_count FROM punition p "
                + "INNER JOIN reclamation r ON r.id = p.reclamation_id "
                + "GROUP BY r.player_id "
                + "ORDER BY ban_count DESC "
                + "LIMIT ?";
        try (PreparedStatement pst = getConnection().prepareStatement(query)) {
            pst.setInt(1, limit);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    long banCount = rs.getLong("ban_count");
                    results.add(new Object[]{playerId, banCount});
                }
            }
        }
        return results;
    }

    public long getBanCountByPlayerAndStatus(int playerId, String status) throws SQLException {
        String keyword = status.contains("match") ? "%match%" : 
                         status.contains("tournament") ? "%tournament%" : 
                         status.contains("game") ? "%game%" : "%" + status + "%";

        String sql = "SELECT COUNT(*) FROM punition p " +
                     "JOIN reclamation r ON p.reclamation_id = r.id " +
                     "WHERE r.player_id = ? AND LOWER(p.player_status) LIKE ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setString(2, keyword.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        }
    }

    /**
     * Look up the player_id from a reclamation.
     * Returns null if the reclamation has no player or doesn't exist.
     */
    public Integer getPlayerIdByReclamationId(int reclamationId) throws SQLException {
        String sql = "SELECT player_id FROM reclamation WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, reclamationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    return rs.wasNull() ? null : playerId;
                }
            }
        }
        return null;
    }
}
