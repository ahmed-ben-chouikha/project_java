package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;

public final class TournamentReviewState {

    private static Tournament selectedTournament;

    private TournamentReviewState() {
    }

    public static void setSelectedTournament(Tournament tournament) {
        selectedTournament = tournament;
    }

    public static Tournament getSelectedTournament() {
        return selectedTournament;
    }

    public static boolean hasSelectedTournament() {
        return selectedTournament != null;
    }

    public static void clear() {
        selectedTournament = null;
    }
}

