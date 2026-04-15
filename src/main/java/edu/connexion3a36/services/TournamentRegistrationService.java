package edu.connexion3a36.services;

import edu.connexion3a36.entities.Tournament;
import edu.connexion3a36.entities.TournamentRegistration;
import edu.connexion3a36.interfaces.ITournamentRegistration;
import edu.connexion3a36.tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentRegistrationService implements ITournamentRegistration {

    @Override
    public void addEntity(TournamentRegistration registration) throws SQLException {
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

        // Check if tournament is open
        TournamentService tournamentService = new TournamentService();
        Tournament tournament = tournamentService.getTournamentById(registration.getTournamentId());
        if (tournament == null || !isOpenStatus(tournament.getStatus())) {
            throw new SQLException("Cannot register for a closed or finished tournament");
        }

        // Insert registration with pending status
        String query = "INSERT INTO tournament_registrations (player_name, team_name, tournament_id, status) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, registration.getPlayerName().trim());
        pst.setString(2, registration.getTeamName().trim());
        pst.setInt(3, registration.getTournamentId());
        pst.setString(4, "pending");

        pst.executeUpdate();
        System.out.println("Registration added successfully");
    }

    @Override
    public void deleteEntity(TournamentRegistration registration) throws SQLException {
        if (registration == null || registration.getId() <= 0) {
            throw new SQLException("Invalid registration for deletion");
        }

        // Only allow deletion if status is pending
        TournamentRegistration existing = getRegistrationById(registration.getId());
        if (existing != null && !"pending".equalsIgnoreCase(existing.getStatus())) {
            throw new SQLException("Can only cancel pending registrations");
        }

        String query = "DELETE FROM tournament_registrations WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
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
        if (registration == null) {
            throw new SQLException("Registration cannot be null");
        }

        String query = "UPDATE tournament_registrations SET team_name = ?, status = ? WHERE id = ?";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
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
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT tr.*, t.name as tournament_name FROM tournament_registrations tr " +
                "LEFT JOIN tournaments t ON tr.tournament_id = t.id " +
                "WHERE tr.player_name = ? ORDER BY tr.registration_date DESC";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, playerName.trim());
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public boolean isDuplicateRegistration(String playerName, int tournamentId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM tournament_registrations " +
                "WHERE player_name = ? AND tournament_id = ?";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
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
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT tr.*, t.name as tournament_name FROM tournament_registrations tr " +
                "LEFT JOIN tournaments t ON tr.tournament_id = t.id " +
                "ORDER BY tr.registration_date DESC";

        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByStatus(String status) throws SQLException {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT tr.*, t.name as tournament_name FROM tournament_registrations tr " +
                "LEFT JOIN tournaments t ON tr.tournament_id = t.id " +
                "WHERE tr.status = ? ORDER BY tr.registration_date DESC";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setString(1, status);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByTournament(int tournamentId) throws SQLException {
        List<TournamentRegistration> registrations = new ArrayList<>();
        String query = "SELECT tr.*, t.name as tournament_name FROM tournament_registrations tr " +
                "LEFT JOIN tournaments t ON tr.tournament_id = t.id " +
                "WHERE tr.tournament_id = ? ORDER BY tr.registration_date DESC";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, tournamentId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            registrations.add(mapResultSetToEntity(rs));
        }
        return registrations;
    }

    @Override
    public void acceptRegistration(int registrationId) throws SQLException {
        // Get the registration
        TournamentRegistration registration = getRegistrationById(registrationId);
        if (registration == null) {
            throw new SQLException("Registration not found");
        }

        // Update status to confirmed
        String updateQuery = "UPDATE tournament_registrations SET status = 'confirmed' WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(updateQuery);
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
        String updateQuery = "UPDATE tournament_registrations SET status = 'rejected', rejection_reason = ? WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(updateQuery);
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
        String query = "SELECT COUNT(*) as count FROM tournament_registrations WHERE status = 'pending'";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(query);

        if (rs.next()) {
            return rs.getLong("count");
        }
        return 0;
    }

    @Override
    public TournamentRegistration getRegistrationById(int id) throws SQLException {
        String query = "SELECT tr.*, t.name as tournament_name FROM tournament_registrations tr " +
                "LEFT JOIN tournaments t ON tr.tournament_id = t.id WHERE tr.id = ?";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return mapResultSetToEntity(rs);
        }
        return null;
    }

    @Override
    public long getConfirmedCountForTournament(int tournamentId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM tournament_registrations " +
                "WHERE tournament_id = ? AND status = 'confirmed'";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, tournamentId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getLong("count");
        }
        return 0;
    }

    @Override
    public void autoRejectRemaining(int tournamentId, String reason) throws SQLException {
        String query = "UPDATE tournament_registrations " +
                "SET status = 'rejected', rejection_reason = ? " +
                "WHERE tournament_id = ? AND status = 'pending'";

        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
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
        String query = "UPDATE tournaments SET status = 'closed' WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
        pst.setInt(1, tournamentId);
        pst.executeUpdate();
        System.out.println("Tournament closed due to max_teams reached");
    }

    private boolean isOpenStatus(String status) {
        if (status == null) {
            return false;
        }
        String normalized = status.trim().toLowerCase();
        return "pending".equals(normalized) || "open".equals(normalized) || "ongoing".equals(normalized);
    }

    private TournamentRegistration mapResultSetToEntity(ResultSet rs) throws SQLException {
        TournamentRegistration tr = new TournamentRegistration();
        tr.setId(rs.getInt("id"));
        tr.setPlayerName(rs.getString("player_name"));
        tr.setTeamName(rs.getString("team_name"));
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
}
