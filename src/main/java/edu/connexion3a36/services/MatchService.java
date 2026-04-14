package edu.connexion3a36.services;

import edu.connexion3a36.entities.Match;
import edu.connexion3a36.tools.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchService {

    public static class TeamOption {
        private final int id;
        private final String label;

        public TeamOption(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public List<Match> getAllMatches() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        SQLException lastError = null;
        for (String tableName : new String[]{"game", "matches"}) {
            List<Match> matches = new ArrayList<>();
            String query = "SELECT * FROM " + tableName;
            Map<Integer, String> teamCache = new HashMap<>();

            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery(query)) {
                while (rs.next()) {
                    matches.add(mapRow(connection, rs, teamCache));
                }
                return matches;
            } catch (SQLException e) {
                lastError = e;
            }
        }

        throw new SQLException("Could not load matches from table 'game' or 'matches'.", lastError);
    }

    public void deleteMatchById(int matchId) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        try (PreparedStatement pst = connection.prepareStatement("DELETE FROM game WHERE id = ?")) {
            pst.setInt(1, matchId);
            pst.executeUpdate();
        }
    }

    public void updateMatch(Match match) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String query = "UPDATE game SET team1_id = ?, team2_id = ?, score1 = ?, score2 = ?, matchdate = ?, status = ?, updated_at = ?, tournament_id = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, match.getTeam1Id());
            pst.setInt(2, match.getTeam2Id());
            pst.setInt(3, match.getScore1());
            pst.setInt(4, match.getScore2());
            pst.setTimestamp(5, Timestamp.valueOf(match.getMatchDate()));
            pst.setString(6, match.getStatus());
            pst.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));
            if (match.getTournamentId() > 0) {
                pst.setInt(8, match.getTournamentId());
            } else {
                pst.setNull(8, java.sql.Types.INTEGER);
            }
            pst.setInt(9, match.getId());
            pst.executeUpdate();
        }
    }

    public void createMatch(Match match) throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        String query = "INSERT INTO game (score1, score2, matchdate, status, created_at, updated_at, team1_id, team2_id, tournament_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, match.getScore1());
            pst.setInt(2, match.getScore2());
            pst.setTimestamp(3, Timestamp.valueOf(match.getMatchDate()));
            pst.setString(4, match.getStatus());
            pst.setTimestamp(5, now);
            pst.setTimestamp(6, now);
            pst.setInt(7, match.getTeam1Id());
            pst.setInt(8, match.getTeam2Id());
            if (match.getTournamentId() > 0) {
                pst.setInt(9, match.getTournamentId());
            } else {
                pst.setNull(9, java.sql.Types.INTEGER);
            }
            pst.executeUpdate();
        }
    }

    public List<TeamOption> getTeamOptions() throws SQLException {
        Connection connection = MyConnection.getInstance().getCnx();
        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        Map<String, String> tableQueries = new LinkedHashMap<>();
        tableQueries.put("team", "SELECT id, name AS label FROM team ORDER BY id");
        tableQueries.put("equipe", "SELECT id, nom AS label FROM equipe ORDER BY id");
        tableQueries.put("teams", "SELECT id, name AS label FROM teams ORDER BY id");

        SQLException lastError = null;
        for (String query : tableQueries.values()) {
            List<TeamOption> options = new ArrayList<>();
            try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    options.add(new TeamOption(rs.getInt("id"), rs.getString("label")));
                }
                if (!options.isEmpty()) {
                    return options;
                }
            } catch (SQLException e) {
                lastError = e;
            }
        }

        if (lastError != null) {
            throw lastError;
        }
        return new ArrayList<>();
    }

    private Match mapRow(Connection connection, ResultSet rs, Map<Integer, String> teamCache) throws SQLException {
        Set<String> columns = getColumnNames(rs);

        int id = parseInt(firstNonBlank(rs, columns, "id"), -1);
        int team1Id = parseInt(firstNonBlank(rs, columns, "team1_id", "team_1_id", "home_team_id"), -1);
        int team2Id = parseInt(firstNonBlank(rs, columns, "team2_id", "team_2_id", "away_team_id"), -1);
        int tournamentId = parseInt(firstNonBlank(rs, columns, "tournament_id"), -1);

        String team1 = resolveTeamDisplayName(connection, rs, columns, teamCache,
                "team1", "team1_id", "team_1", "team_1_id", "home_team", "home_team_id", "team_one", "team_one_id");
        String team2 = resolveTeamDisplayName(connection, rs, columns, teamCache,
                "team2", "team2_id", "team_2", "team_2_id", "away_team", "away_team_id", "team_two", "team_two_id");

        if (team1.isBlank() || team2.isBlank()) {
            String teams = firstNonBlank(rs, columns, "teams", "matchup");
            String[] parsed = splitTeams(teams);
            if (team1.isBlank()) {
                team1 = parsed[0];
            }
            if (team2.isBlank()) {
                team2 = parsed[1];
            }
        }

        String score = firstNonBlank(rs, columns, "score", "result");
        int score1Value = parseInt(firstNonBlank(rs, columns, "score1", "team1_score", "home_score"), 0);
        int score2Value = parseInt(firstNonBlank(rs, columns, "score2", "team2_score", "away_score"), 0);
        if (score.isBlank()) {
            String score1 = firstNonBlank(rs, columns, "score1", "team1_score", "home_score");
            String score2 = firstNonBlank(rs, columns, "score2", "team2_score", "away_score");
            if (!score1.isBlank() && !score2.isBlank()) {
                score = score1 + " - " + score2;
            }
        } else {
            int[] parsed = parseScorePair(score);
            score1Value = parsed[0];
            score2Value = parsed[1];
        }

        String date = firstNonBlank(rs, columns, "matchdate", "date", "match_date", "datetime", "scheduled_at", "time", "start_time");
        if (date.isBlank()) {
            date = "1970-01-01 00:00:00";
        }
        String status = firstNonBlank(rs, columns, "status", "state");

        return new Match(
                id,
                team1Id,
                team2Id,
                tournamentId,
                score1Value,
                score2Value,
                date,
                team1.isBlank() ? "TBD" : team1,
                team2.isBlank() ? "TBD" : team2,
                status.isBlank() ? "Unknown" : status
        );
    }

    private String resolveTeamDisplayName(Connection connection,
                                          ResultSet rs,
                                          Set<String> columns,
                                          Map<Integer, String> teamCache,
                                          String... candidateColumns) throws SQLException {
        for (String candidate : candidateColumns) {
            String rawValue = firstNonBlank(rs, columns, candidate);
            if (rawValue.isBlank()) {
                continue;
            }

            String lowered = candidate.toLowerCase();
            boolean looksLikeId = lowered.endsWith("_id") || lowered.contains("id") || isNumeric(rawValue);
            if (looksLikeId && isNumeric(rawValue)) {
                int teamId = Integer.parseInt(rawValue);
                return teamCache.computeIfAbsent(teamId, id -> fetchTeamName(connection, id));
            }

            return rawValue;
        }

        return "";
    }

    private String fetchTeamName(Connection connection, int teamId) {
        String cachedFallback = "Team #" + teamId;
        for (String tableName : new String[]{"team", "equipe", "teams"}) {
            String query = "SELECT * FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setInt(1, teamId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Set<String> columns = getColumnNames(rs);
                        String name = firstNonBlank(rs, columns, "name", "team_name", "nom");
                        if (!name.isBlank()) {
                            return name;
                        }
                    }
                }
            } catch (SQLException ignored) {
                // Try next possible table name.
            }
        }
        return cachedFallback;
    }

    private Set<String> getColumnNames(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columns.add(meta.getColumnLabel(i).toLowerCase());
        }
        return columns;
    }

    private String firstNonBlank(ResultSet rs, Set<String> columns, String... names) throws SQLException {
        for (String name : names) {
            String lowered = name.toLowerCase();
            if (columns.contains(lowered)) {
                String value = rs.getString(name);
                if (value != null && !value.isBlank()) {
                    return value.trim();
                }
            }
        }
        return "";
    }

    private String[] splitTeams(String teams) {
        if (teams == null || teams.isBlank()) {
            return new String[]{"", ""};
        }

        String normalized = teams.trim();
        String[] separators = {" vs ", " VS ", " vs. ", " - ", "|"};

        for (String separator : separators) {
            if (normalized.contains(separator)) {
                String[] parts = normalized.split(java.util.regex.Pattern.quote(separator), 2);
                return new String[]{parts[0].trim(), parts[1].trim()};
            }
        }

        return new String[]{normalized, ""};
    }

    private boolean isNumeric(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (char c : value.trim().toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private int parseInt(String value, int fallback) {
        if (!isNumeric(value)) {
            return fallback;
        }
        return Integer.parseInt(value);
    }

    private int[] parseScorePair(String score) {
        if (score == null) {
            return new int[]{0, 0};
        }
        String[] parts = score.split("-");
        if (parts.length != 2) {
            return new int[]{0, 0};
        }
        return new int[]{parseInt(parts[0].trim(), 0), parseInt(parts[1].trim(), 0)};
    }
}

