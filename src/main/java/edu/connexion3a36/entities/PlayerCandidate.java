package edu.connexion3a36.entities;

public class PlayerCandidate {
    private final int id;
    private final String displayName;
    private final String nickname;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String status;
    private final Integer teamId;
    private final String birthDate;
    private final String createdAt;

    public PlayerCandidate(int id,
                           String displayName,
                           String nickname,
                           String firstName,
                           String lastName,
                           String role,
                           String status,
                           Integer teamId,
                           String birthDate,
                           String createdAt) {
        this.id = id;
        this.displayName = displayName;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.teamId = teamId;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
