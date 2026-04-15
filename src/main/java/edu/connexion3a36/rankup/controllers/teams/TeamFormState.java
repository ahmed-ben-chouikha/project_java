package edu.connexion3a36.rankup.controllers.teams;

import edu.connexion3a36.entities.Team;

public final class TeamFormState {

    private static Team editingTeam;

    private TeamFormState() {
    }

    public static void setEditingTeam(Team team) {
        editingTeam = team;
    }

    public static Team getEditingTeam() {
        return editingTeam;
    }

    public static void clear() {
        editingTeam = null;
    }
}

