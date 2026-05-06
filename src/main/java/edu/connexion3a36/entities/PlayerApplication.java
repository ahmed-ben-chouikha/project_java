package edu.connexion3a36.entities;

public class PlayerApplication {
    private final int id;
    private final String nickname;
    private final String firstName;
    private final String lastName;
    private final String birthDate;
    private final String role;
    private final String createdAt;
    private final String updatedAt;
    private final Integer teamId;
    private final String playerStatus;

    public PlayerApplication(int id,
                             String nickname,
                             String firstName,
                             String lastName,
                             String birthDate,
                             String role,
                             String createdAt,
                             String updatedAt,
                             Integer teamId,
                             String playerStatus) {
        this.id = id;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.teamId = teamId;
        this.playerStatus = playerStatus;
    }

    public int getId() { return id; }
    public String getNickname() { return nickname; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBirthDate() { return birthDate; }
    public String getRole() { return role; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public Integer getTeamId() { return teamId; }
    public String getPlayerStatus() { return playerStatus; }
}
