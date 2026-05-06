package edu.connexion3a36.services;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.interfaces.IService;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

public class TournamentService implements IService<Tournament> {

    private static final String[] TABLE_CANDIDATES = {"tournaments", "tournament"};

    @Override
    public void addEntity(Tournament tournament) throws SQLException {
        validateTournament(tournament);
        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        Set<String> columns = getColumns(cnx, table);

        SQLException lastError = null;
        for (String candidateRules : buildRulesCandidates(tournament.getRules())) {
            tournament.setRules(candidateRules);
            String query = buildInsertQuery(table, columns);
            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                bindTournamentForInsert(pst, columns, tournament);
                pst.executeUpdate();
                System.out.println("Tournament added successfully");
                return;
            } catch (SQLException e) {
                lastError = e;
                if (!isRulesConstraintError(e) || !usesRulesColumn(columns)) {
                    throw e;
                }
            }
        }
        throw lastError == null ? new SQLException("Could not save tournament") : lastError;
    }

    @Override
    public void deleteEntity(Tournament tournament) throws SQLException {
        if (tournament == null || tournament.getId() <= 0) {
            throw new SQLException("Invalid tournament for deletion");
        }

        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        String query = "DELETE FROM " + table + " WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, tournament.getId());
            int result = pst.executeUpdate();
            if (result > 0) {
                System.out.println("Tournament deleted successfully");
            } else {
                throw new SQLException("Tournament not found");
            }
        }
    }

    @Override
    public void updateEntity(int id, Tournament tournament) throws SQLException {
        validateTournament(tournament);
        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        Set<String> columns = getColumns(cnx, table);

        SQLException lastError = null;
        for (String candidateRules : buildRulesCandidates(tournament.getRules())) {
            tournament.setRules(candidateRules);
            String query = buildUpdateQuery(table, columns);
            try (PreparedStatement pst = cnx.prepareStatement(query)) {
                bindTournamentForUpdate(pst, columns, tournament, id);
                int result = pst.executeUpdate();
                if (result > 0) {
                    System.out.println("Tournament updated successfully");
                    return;
                }
                throw new SQLException("Tournament not found");
            } catch (SQLException e) {
                lastError = e;
                if (!isRulesConstraintError(e) || !usesRulesColumn(columns)) {
                    throw e;
                }
            }
        }
        throw lastError == null ? new SQLException("Could not update tournament") : lastError;
    }

    @Override
    public List<Tournament> getData() throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        String query = "SELECT * FROM " + table + " ORDER BY created_at DESC";

        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                tournaments.add(mapTournament(rs));
            }
        }
        return tournaments;
    }

    public List<Tournament> getTournamentsByStatus(String status) throws SQLException {
        List<Tournament> tournaments = new ArrayList<>();
        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        String query = "SELECT * FROM " + table + " WHERE status = ? ORDER BY created_at DESC";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setString(1, normalizeStatus(status));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    tournaments.add(mapTournament(rs));
                }
            }
        }
        return tournaments;
    }

    public Tournament getTournamentById(int id) throws SQLException {
        Connection cnx = getConnection();
        String table = resolveTournamentTable(cnx);
        String query = "SELECT * FROM " + table + " WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapTournament(rs);
                }
            }
        }
        return null;
    }

    public boolean isTournamentOpen(int tournamentId) throws SQLException {
        Tournament tournament = getTournamentById(tournamentId);
        if (tournament == null) {
            return false;
        }
        String status = normalizeStatus(tournament.getStatus());
        return "open".equals(status) || "pending".equals(status);
    }

    public List<Tournament> getOpenTournaments() throws SQLException {
        List<Tournament> open = new ArrayList<>();
        for (Tournament tournament : getData()) {
            String status = normalizeStatus(tournament.getStatus());
            if ("open".equals(status) || "pending".equals(status)) {
                open.add(tournament);
            }
        }
        return open;
    }

    private void validateTournament(Tournament tournament) throws SQLException {
        if (tournament == null) {
            throw new SQLException("Tournament cannot be null");
        }
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            throw new SQLException("Tournament name cannot be empty");
        }
        if (tournament.getGameType() == null || tournament.getGameType().trim().isEmpty()) {
            throw new SQLException("Game type cannot be empty");
        }
        tournament.setRules(normalizeRules(tournament.getRules()));
        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            throw new SQLException("Tournament dates cannot be empty");
        }
        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            throw new SQLException("End date must be after or equal to start date");
        }
        if (tournament.getMaxTeams() <= 0) {
            throw new SQLException("Max teams must be greater than 0");
        }
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "open";
        }
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        if ("pending".equals(normalized)) {
            return "open";
        }
        if ("ongoing".equals(normalized)) {
            return "open";
        }
        return normalized;
    }

    private Connection getConnection() throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        if (cnx == null) {
            throw new SQLException("Database connection is not available");
        }
        return cnx;
    }

    private String resolveTournamentTable(Connection cnx) throws SQLException {
        SQLException last = null;
        for (String table : TABLE_CANDIDATES) {
            try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery("SELECT 1 FROM " + table + " LIMIT 1")) {
                return table;
            } catch (SQLException e) {
                last = e;
            }
        }
        throw new SQLException("Could not find tournaments table", last);
    }

    private Set<String> getColumns(Connection cnx, String table) {
        Set<String> columns = new HashSet<>();
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM " + table + " LIMIT 1")) {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columns.add(meta.getColumnLabel(i).toLowerCase(Locale.ROOT));
            }
        } catch (SQLException ignored) {
        }
        return columns;
    }

    private String buildInsertQuery(String table, Set<String> columns) {
        if (columns.contains("tournament_name")) {
            StringBuilder sql = new StringBuilder("INSERT INTO ").append(table)
                    .append(" (tournament_name, game_type, start_date, end_date");
            if (columns.contains("max_teams")) {
                sql.append(", max_teams");
            }
            sql.append(", status");
            if (columns.contains("created_at")) sql.append(", created_at");
            sql.append(") VALUES (?, ?, ?, ?");
            if (columns.contains("max_teams")) {
                sql.append(", ?");
            }
            sql.append(", ?");
            if (columns.contains("created_at")) sql.append(", CURRENT_TIMESTAMP");
            sql.append(")");
            return sql.toString();
        }
        return "INSERT INTO " + table + " (name, description, start_date, end_date, status, location, prize_pool, rules" +
                (columns.contains("max_teams") ? ", max_teams" : "") +
                (columns.contains("created_at") ? ", created_at" : "") +
                (columns.contains("updated_at") ? ", updated_at" : "") +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?" +
                (columns.contains("max_teams") ? ", ?" : "") +
                (columns.contains("created_at") ? ", CURRENT_TIMESTAMP" : "") +
                (columns.contains("updated_at") ? ", CURRENT_TIMESTAMP" : "") + ")";
    }

    private String buildUpdateQuery(String table, Set<String> columns) {
        if (columns.contains("tournament_name")) {
            return "UPDATE " + table + " SET tournament_name = ?, game_type = ?, start_date = ?, end_date = ?" +
                    (columns.contains("max_teams") ? ", max_teams = ?" : "") +
                    ", status = ?" +
                    (columns.contains("updated_at") ? ", updated_at = CURRENT_TIMESTAMP" : "") +
                    " WHERE id = ?";
        }
        return "UPDATE " + table + " SET name = ?, description = ?, start_date = ?, end_date = ?, status = ?, location = ?, prize_pool = ?, rules = ?" +
                (columns.contains("max_teams") ? ", max_teams = ?" : "") +
                (columns.contains("updated_at") ? ", updated_at = CURRENT_TIMESTAMP" : "") +
                " WHERE id = ?";
    }

    private void bindTournamentForInsert(PreparedStatement pst, Set<String> columns, Tournament tournament) throws SQLException {
        if (columns.contains("tournament_name")) {
            pst.setString(1, tournament.getName().trim());
            pst.setString(2, safe(tournament.getGameType()));
            pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
            pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
            int index = 5;
            if (columns.contains("max_teams")) {
                pst.setInt(index++, tournament.getMaxTeams());
            }
            pst.setString(index, normalizeStatus(tournament.getStatus()));
            return;
        }

        pst.setString(1, tournament.getName().trim());
        pst.setString(2, safe(tournament.getDescription()));
        pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
        pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
        pst.setString(5, normalizeStatus(tournament.getStatus()));
        pst.setString(6, safe(tournament.getLocation()));
        pst.setDouble(7, tournament.getPrizePool());
        pst.setString(8, safe(tournament.getRules()));
        int index = 9;
        if (columns.contains("max_teams")) {
            pst.setInt(index++, tournament.getMaxTeams());
        }
        if (columns.contains("created_at")) {
            index++;
        }
        if (columns.contains("updated_at")) {
            index++;
        }
    }

    private void bindTournamentForUpdate(PreparedStatement pst, Set<String> columns, Tournament tournament, int id) throws SQLException {
        if (columns.contains("tournament_name")) {
            pst.setString(1, tournament.getName().trim());
            pst.setString(2, safe(tournament.getGameType()));
            pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
            pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
            int index = 5;
            if (columns.contains("max_teams")) {
                pst.setInt(index++, tournament.getMaxTeams());
            }
            pst.setString(index++, normalizeStatus(tournament.getStatus()));
            pst.setInt(index, id);
            return;
        }

        pst.setString(1, tournament.getName().trim());
        pst.setString(2, safe(tournament.getDescription()));
        pst.setDate(3, java.sql.Date.valueOf(tournament.getStartDate()));
        pst.setDate(4, java.sql.Date.valueOf(tournament.getEndDate()));
        pst.setString(5, normalizeStatus(tournament.getStatus()));
        pst.setString(6, safe(tournament.getLocation()));
        pst.setDouble(7, tournament.getPrizePool());
        pst.setString(8, safe(tournament.getRules()));
        int index = 9;
        if (columns.contains("max_teams")) {
            pst.setInt(index++, tournament.getMaxTeams());
        }
        pst.setInt(index, id);
    }

    private Tournament mapTournament(ResultSet rs) throws SQLException {
        Set<String> columns = new HashSet<>();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columns.add(meta.getColumnLabel(i).toLowerCase(Locale.ROOT));
        }

        Tournament t = new Tournament();
        t.setId(rs.getInt("id"));
        if (columns.contains("tournament_name")) {
            t.setName(rs.getString("tournament_name"));
            t.setGameType(rs.getString("game_type"));
            t.setMaxTeams(columns.contains("max_teams") ? rs.getInt("max_teams") : 8);
            t.setStatus(normalizeStatus(rs.getString("status")));
        } else {
            t.setName(rs.getString("name"));
            if (columns.contains("description")) t.setDescription(rs.getString("description"));
            if (columns.contains("location")) t.setLocation(rs.getString("location"));
            if (columns.contains("prize_pool")) t.setPrizePool(rs.getDouble("prize_pool"));
            if (columns.contains("rules")) t.setRules(rs.getString("rules"));
            t.setMaxTeams(columns.contains("max_teams") ? rs.getInt("max_teams") : 8);
            t.setStatus(normalizeStatus(rs.getString("status")));
        }
        if (columns.contains("start_date")) t.setStartDate(rs.getDate("start_date").toLocalDate());
        if (columns.contains("end_date")) t.setEndDate(rs.getDate("end_date").toLocalDate());
        if (columns.contains("created_at") && rs.getTimestamp("created_at") != null) {
            t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (columns.contains("updated_at") && rs.getTimestamp("updated_at") != null) {
            t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return t;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeRules(String value) {
        String normalized = safe(value);
        return normalized.isEmpty() ? "standard" : normalized;
    }

    private Set<String> buildRulesCandidates(String initialRules) {
        Set<String> candidates = new LinkedHashSet<>();
        candidates.add(normalizeRules(initialRules));
        candidates.add("standard");
        candidates.add("{}");
        candidates.add("{\"mode\":\"standard\"}");
        return candidates;
    }

    private boolean isRulesConstraintError(SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase(Locale.ROOT);
        return message.contains("rules") && (message.contains("constraint") || message.contains("check"));
    }

    private boolean usesRulesColumn(Set<String> columns) {
        return columns.contains("rules") && !columns.contains("tournament_name");
    }
}
