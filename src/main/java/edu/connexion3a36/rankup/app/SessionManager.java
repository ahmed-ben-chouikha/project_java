package edu.connexion3a36.rankup.app;

public final class SessionManager {

    private static String currentPlayerName = "DefaultPlayer";
    private static int currentUserId = -1;
    private static String currentRole = "player";

    private SessionManager() {
    }

    public static void setCurrentPlayerName(String playerName) {
        currentPlayerName = (playerName == null || playerName.isBlank()) ? "DefaultPlayer" : playerName.trim();
    }

    public static String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentRole(String role) {
        currentRole = (role == null || role.isBlank()) ? "player" : role.trim().toLowerCase();
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static boolean isAdmin() {
        return "admin".equalsIgnoreCase(currentRole);
    }

    public static void clear() {
        currentPlayerName = "DefaultPlayer";
        currentUserId = -1;
        currentRole = "player";
    }
}

