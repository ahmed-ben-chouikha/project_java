package edu.connexion3a36.rankup.controllers.matches;

import edu.connexion3a36.entities.Match;

public final class MatchFormState {

    private static Match editingMatch;

    private MatchFormState() {
    }

    public static void setEditingMatch(Match match) {
        editingMatch = match;
    }

    public static Match getEditingMatch() {
        return editingMatch;
    }

    public static void clear() {
        editingMatch = null;
    }
}

