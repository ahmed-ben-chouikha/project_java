package edu.connexion3a36.interfaces;

import edu.connexion3a36.entities.TournamentRegistration;

import java.sql.SQLException;
import java.util.List;

public interface ITournamentRegistration extends IService<TournamentRegistration> {

    // User-side methods
    List<TournamentRegistration> getPlayerRegistrations(String playerName) throws SQLException;

    boolean isDuplicateRegistration(String playerName, int tournamentId) throws SQLException;

    // Admin-side methods
    List<TournamentRegistration> getAllRegistrations() throws SQLException;

    List<TournamentRegistration> getRegistrationsByStatus(String status) throws SQLException;

    List<TournamentRegistration> getRegistrationsByTournament(int tournamentId) throws SQLException;

    void acceptRegistration(int registrationId) throws SQLException;

    void rejectRegistration(int registrationId, String reason) throws SQLException;

    long getPendingCount() throws SQLException;

    TournamentRegistration getRegistrationById(int id) throws SQLException;

    long getConfirmedCountForTournament(int tournamentId) throws SQLException;

    void autoRejectRemaining(int tournamentId, String reason) throws SQLException;
}
