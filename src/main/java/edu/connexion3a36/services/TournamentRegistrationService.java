package edu.connexion3a36.services;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.interfaces.ITournamentRegistration;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TournamentRegistrationService implements ITournamentRegistration {

    @Override
    public void addEntity(TournamentRegistration registration) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);

        if (registration == null || registration.getPlayerName() == null ||
            registration.getPlayerName().trim().isEmpty()) {
            throw new SQLException("Player name cannot be empty");
        }

        if (registration.getTeamName() == null || registration.getTeamName().trim().isEmpty()) {
            throw new SQLException("Team name cannot be empty");
        }

        if (registration.getTeamName().trim().length() < 3) {
            throw new SQLException("Team name must be at least 3 characters");
        }

        // Check for duplicate registration
        if (isDuplicateRegistration(registration.getPlayerName().trim(), registration.getTournamentId())) {
            throw new SQLException("You are already registered for this tournament");
        }

        // Check if tournament is planned and still accepting registrations
        TournamentService tournamentService = new TournamentService();
        Tournament tournament = tournamentService.getTournamentById(registration.getTournamentId());
        if (tournament == null || !isRegisterableStatus(tournament.getStatus())) {
            throw new SQLException("Cannot register unless the tournament is in planned status");
        }

        // Insert registration with pending status
        String query = "INSERT INTO " + registrationTable + " (player_name, team_name, tournament_id, status) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, registration.getPlayerName().trim());
        pst.setString(2, registration.getTeamName().trim());
        pst.setInt(3, registration.getTournamentId());
        pst.setString(4, "pending");

        pst.executeUpdate();
        System.out.println("Registration added successfully");
    }

    @Override
    public void deleteEntity(TournamentRegistration registration) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);

        if (registration == null || registration.getId() <= 0) {
            throw new SQLException("Invalid registration for deletion");
        }

        // Only allow deletion if status is pending
        TournamentRegistration existing = getRegistrationById(registration.getId());
        if (existing != null && !"pending".equalsIgnoreCase(existing.getStatus())) {
            throw new SQLException("Can only cancel pending registrations");
        }

        String query = "DELETE FROM " + registrationTable + " WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setInt(1, registration.getId());

        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Registration deleted successfully");
        } else {
            throw new SQLException("Registration not found");
        }
    }

    @Override
    public void updateEntity(int id, TournamentRegistration registration) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);

        if (registration == null) {
            throw new SQLException("Registration cannot be null");
        }

        String query = "UPDATE " + registrationTable + " SET team_name = ?, status = ? WHERE id = ?";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, registration.getTeamName().trim());
        pst.setString(2, registration.getStatus() != null ? registration.getStatus() : "pending");
        pst.setInt(3, id);

        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Registration updated successfully");
        } else {
            throw new SQLException("Registration not found");
        }
    }

    @Override
    public List<TournamentRegistration> getData() throws SQLException {
        return getAllRegistrations();
    }

    @Override
    public List<TournamentRegistration> getPlayerRegistrations(String playerName) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT " + registrationTable + ".*, t.name as tournament_name FROM " + registrationTable + " " +
                "LEFT JOIN " + tournamentTable + " t ON " + registrationTable + ".tournament_id = t.id " +
                "WHERE " + registrationTable + ".player_name = ? ORDER BY " + registrationTable + ".registration_date DESC";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, playerName.trim());
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    public List<Tournament> getConfirmedTournamentsByPlayer(String playerName) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        List<Tournament> tournaments = new ArrayList<>();

        String query = "SELECT t.* FROM " + tournamentTable + " t " +
                "INNER JOIN " + registrationTable + " r ON r.tournament_id = t.id " +
                "WHERE r.player_name = ? AND LOWER(r.status) = 'confirmed' " +
                "ORDER BY r.registration_date DESC";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, playerName.trim());
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tournaments.add(mapTournamentResultSet(rs));
            }
        }
        return tournaments;
    }

    @Override
    public boolean isDuplicateRegistration(String playerName, int tournamentId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String query = "SELECT COUNT(*) as count FROM " + registrationTable + " " +
                "WHERE player_name = ? AND tournament_id = ?";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, playerName.trim());
        pst.setInt(2, tournamentId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
        return false;
    }

    @Override
    public boolean hasConfirmedRegistration(String playerName, int tournamentId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String query = "SELECT COUNT(*) as count FROM " + registrationTable + " " +
                "WHERE player_name = ? AND tournament_id = ? AND status = 'confirmed'";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, playerName.trim());
        pst.setInt(2, tournamentId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt("count") > 0;
        }
        return false;
    }

    @Override
    public List<TournamentRegistration> getAllRegistrations() throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT " + registrationTable + ".*, t.name as tournament_name FROM " + registrationTable + " " +
                "LEFT JOIN " + tournamentTable + " t ON " + registrationTable + ".tournament_id = t.id " +
                "ORDER BY " + registrationTable + ".registration_date DESC";

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByStatus(String status) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT " + registrationTable + ".*, t.name as tournament_name FROM " + registrationTable + " " +
                "LEFT JOIN " + tournamentTable + " t ON " + registrationTable + ".tournament_id = t.id " +
                "WHERE " + registrationTable + ".status = ? ORDER BY " + registrationTable + ".registration_date DESC";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, status);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByTournament(int tournamentId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT " + registrationTable + ".*, t.name as tournament_name FROM " + registrationTable + " " +
                "LEFT JOIN " + tournamentTable + " t ON " + registrationTable + ".tournament_id = t.id " +
                "WHERE " + registrationTable + ".tournament_id = ? ORDER BY " + registrationTable + ".registration_date DESC";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setInt(1, tournamentId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public void acceptRegistration(int registrationId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);

        // Get the registration
        TournamentRegistration registration = getRegistrationById(registrationId);
        if (registration == null) {
            throw new SQLException("Registration not found");
        }

        // Update status to confirmed
        String updateQuery = "UPDATE " + registrationTable + " SET status = 'confirmed' WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(updateQuery);
        pst.setInt(1, registrationId);
        pst.executeUpdate();

        // Check if tournament has reached max_teams
        long confirmedCount = getConfirmedCountForTournament(registration.getTournamentId());
        int maxTeams = getTournamentMaxTeams(registration.getTournamentId());

        if (confirmedCount >= maxTeams) {
            // Auto-close tournament
            closeTournament(registration.getTournamentId());
            // Auto-reject remaining pending registrations
            autoRejectRemaining(registration.getTournamentId(), "Tournament has reached maximum team limit");
        }

        System.out.println("Registration accepted successfully");
    }

    @Override
    public void rejectRegistration(int registrationId, String reason) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String updateQuery = "UPDATE " + registrationTable + " SET status = 'rejected', rejection_reason = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(updateQuery);
        pst.setString(1, reason != null ? reason : "");
        pst.setInt(2, registrationId);

        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Registration rejected successfully");
        } else {
            throw new SQLException("Registration not found");
        }
    }

    @Override
    public long getPendingCount() throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String query = "SELECT COUNT(*) as count FROM " + registrationTable + " WHERE status = 'pending'";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(query);

        if (rs.next()) {
            return rs.getLong("count");
        }
        return 0;
    }

    @Override
    public TournamentRegistration getRegistrationById(int id) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String tournamentTable = resolveTournamentTable(cnx);
        String query = "SELECT " + registrationTable + ".*, t.name as tournament_name FROM " + registrationTable + " " +
                "LEFT JOIN " + tournamentTable + " t ON " + registrationTable + ".tournament_id = t.id WHERE " + registrationTable + ".id = ?";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return mapResultSetToEntity(rs);
        }
        return null;
    }

    @Override
    public long getConfirmedCountForTournament(int tournamentId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String query = "SELECT COUNT(*) as count FROM " + registrationTable + " " +
                "WHERE tournament_id = ? AND status = 'confirmed'";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setInt(1, tournamentId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getLong("count");
        }
        return 0;
    }

    @Override
    public void autoRejectRemaining(int tournamentId, String reason) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String registrationTable = resolveRegistrationTable(cnx);
        String query = "UPDATE " + registrationTable + " " +
                "SET status = 'rejected', rejection_reason = ? " +
                "WHERE tournament_id = ? AND status = 'pending'";

        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setString(1, reason);
        pst.setInt(2, tournamentId);

        int result = pst.executeUpdate();
        System.out.println("Auto-rejected " + result + " pending registrations for tournament " + tournamentId);
    }

    // Helper methods
    private int getTournamentMaxTeams(int tournamentId) throws SQLException {
        TournamentService tournamentService = new TournamentService();
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament != null) {
            return tournament.getMaxTeams();
        }
        return 0;
    }

    private void closeTournament(int tournamentId) throws SQLException {
        Connection cnx = MyConnection.getInstance().getCnx();
        String query = "UPDATE " + resolveTournamentTable(cnx) + " SET status = 'closed' WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(query);
        pst.setInt(1, tournamentId);
        pst.executeUpdate();
        System.out.println("Tournament closed due to max_teams reached");
    }

    private String resolveRegistrationTable(Connection cnx) throws SQLException {
        String[] candidates = {"tournament_registrations", "tournament_registration"};
        String[] requiredColumns = {"id", "player_name", "team_name", "tournament_id", "registration_date", "status"};

        for (String table : candidates) {
            if (tableExists(cnx, table) && tableHasColumns(cnx, table, requiredColumns)) {
                return table;
            }
        }
        throw new SQLException("Could not find tournament registration table with required columns: " + String.join(", ", requiredColumns));
    }

    private boolean tableExists(Connection cnx, String tableName) throws SQLException {
        try (ResultSet rs = cnx.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private boolean tableHasColumns(Connection cnx, String tableName, String... columns) throws SQLException {
        for (String column : columns) {
            try (ResultSet rs = cnx.getMetaData().getColumns(null, null, tableName, column)) {
                if (!rs.next()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String resolveTournamentTable(Connection cnx) throws SQLException {
        SQLException last = null;
        for (String table : new String[]{"tournaments", "tournament"}) {
            try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery("SELECT 1 FROM " + table + " LIMIT 1")) {
                return table;
            } catch (SQLException e) {
                last = e;
            }
        }
        throw new SQLException("Could not find tournaments table", last);
    }

    private boolean isRegisterableStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return "planned".equals(normalized) || "open".equals(normalized) || "pending".equals(normalized);
    }

    private TournamentRegistration mapResultSetToEntity(ResultSet rs) throws SQLException {
        TournamentRegistration tr = new TournamentRegistration();
        tr.setId(rs.getInt("id"));
        tr.setPlayerName(rs.getString("player_name"));
        tr.setTeamName(rs.getString("team_name"));
        tr.setTeamMembers("");  // Not used
        tr.setContactInfo("");  // Not used
        
        tr.setTournamentId(rs.getInt("tournament_id"));
        tr.setTournamentName(rs.getString("tournament_name"));
        tr.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        tr.setStatus(rs.getString("status"));
        String rejection = rs.getString("rejection_reason");
        if (rejection != null) {
            tr.setRejectionReason(rejection);
        }
        return tr;
    }

    private Tournament mapTournamentResultSet(ResultSet rs) throws SQLException {
        Tournament tournament = new Tournament();
        ResultSetMetaData meta = rs.getMetaData();
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columns.add(meta.getColumnLabel(i).toLowerCase(Locale.ROOT));
        }

        tournament.setId(rs.getInt("id"));
        if (columns.contains("tournament_name")) {
            tournament.setName(rs.getString("tournament_name"));
            tournament.setGameType(rs.getString("game_type"));
            tournament.setMaxTeams(columns.contains("max_teams") ? rs.getInt("max_teams") : 0);
            tournament.setStatus(rs.getString("status"));
        } else {
            tournament.setName(rs.getString("name"));
            tournament.setGameType(columns.contains("game_type") ? rs.getString("game_type") : null);
            tournament.setMaxTeams(columns.contains("max_teams") ? rs.getInt("max_teams") : 0);
            tournament.setStatus(rs.getString("status"));
        }
        if (columns.contains("start_date")) {
            tournament.setStartDate(rs.getDate("start_date").toLocalDate());
        }
        if (columns.contains("end_date")) {
            tournament.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        return tournament;
    }
}
