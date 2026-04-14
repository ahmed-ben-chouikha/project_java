package edu.connexion3a36.entities;

public class Match {

    private final int id;
    private final int team1Id;
    private final int team2Id;
    private final int tournamentId;
    private final int score1;
    private final int score2;
    private final String matchDate;
    private final String team1;
    private final String team2;
    private final String status;

    public Match(int id,
                 int team1Id,
                 int team2Id,
                 int tournamentId,
                 int score1,
                 int score2,
                 String matchDate,
                 String team1,
                 String team2,
                 String status) {
        this.id = id;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.tournamentId = tournamentId;
        this.score1 = score1;
        this.score2 = score2;
        this.matchDate = matchDate;
        this.team1 = team1;
        this.team2 = team2;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getTeam1Id() {
        return team1Id;
    }

    public int getTeam2Id() {
        return team2Id;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public String getTeam1() {
        return team1;
    }

    public String getScore() {
        return score1 + " - " + score2;
    }

    public String getTeam2() {
        return team2;
    }

    public String getDate() {
        return matchDate;
    }

    public String getStatus() {
        return status;
    }
}

