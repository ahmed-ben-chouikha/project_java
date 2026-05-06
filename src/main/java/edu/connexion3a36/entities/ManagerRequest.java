package edu.connexion3a36.entities;

public class ManagerRequest {
    private final int id;
    private final int playerId;
    private final Integer reviewedById;
    private final String teamName;
    private final String motivation;
    private final String status;
    private final String createdAt;
    private final String reviewedAt;
    private final String adminComment;
    private final String playerNickname;
    private final String playerFirstName;
    private final String playerLastName;

    public ManagerRequest(int id,
                          int playerId,
                          Integer reviewedById,
                          String teamName,
                          String motivation,
                          String status,
                          String createdAt,
                          String reviewedAt,
                          String adminComment,
                          String playerNickname,
                          String playerFirstName,
                          String playerLastName) {
        this.id = id;
        this.playerId = playerId;
        this.reviewedById = reviewedById;
        this.teamName = teamName;
        this.motivation = motivation;
        this.status = status;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
        this.adminComment = adminComment;
        this.playerNickname = playerNickname;
        this.playerFirstName = playerFirstName;
        this.playerLastName = playerLastName;
    }

    public int getId() { return id; }
    public int getPlayerId() { return playerId; }
    public Integer getReviewedById() { return reviewedById; }
    public String getTeamName() { return teamName; }
    public String getMotivation() { return motivation; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getReviewedAt() { return reviewedAt; }
    public String getAdminComment() { return adminComment; }
    public String getPlayerNickname() { return playerNickname; }
    public String getPlayerFirstName() { return playerFirstName; }
    public String getPlayerLastName() { return playerLastName; }

    public String getPlayerDisplayName() {
        String fullName = ((playerFirstName == null ? "" : playerFirstName) + " " + (playerLastName == null ? "" : playerLastName)).trim();
        if (!fullName.isBlank()) {
            return fullName + (playerNickname == null || playerNickname.isBlank() ? "" : " (@" + playerNickname + ")");
        }
        return playerNickname == null || playerNickname.isBlank() ? "Player #" + playerId : playerNickname;
    }
}
