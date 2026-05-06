package edu.connexion3a36.entities;

public class TeamInvitation {
    private final int id;
    private final int teamId;
    private final int playerId;
    private final int invitedById;
    private final String status;
    private final String message;
    private final String createdAt;

    public TeamInvitation(int id, int teamId, int playerId, int invitedById, String status, String message, String createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.playerId = playerId;
        this.invitedById = invitedById;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getInvitedById() {
        return invitedById;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
