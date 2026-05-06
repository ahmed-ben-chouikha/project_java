package edu.connexion3a36.rankup.controllers.manager;

import edu.connexion3a36.entities.Team;

public final class TeamScoutingState {

    private static Team selectedTeam;

    private TeamScoutingState() {
    }

    public static void setSelectedTeam(Team team) {
        selectedTeam = team;
    }

    public static Team getSelectedTeam() {
        return selectedTeam;
    }

    public static void clear() {
        selectedTeam = null;
    }
}
