package edu.connexion3a36.rankup.controllers.tournaments;

import edu.connexion3a36.entities.Tournament;

public final class TournamentFormState {

    private static Tournament editingTournament;

    private TournamentFormState() {
    }

    public static void setEditingTournament(Tournament tournament) {
        editingTournament = tournament;
    }

    public static Tournament getEditingTournament() {
        return editingTournament;
    }

    public static void clear() {
        editingTournament = null;
    }
}

