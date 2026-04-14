package edu.connexion3a36.rankup.session;

import edu.connexion3a36.rankup.entities.UserAccount;

public final class SessionContext {

    private static UserAccount currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }
}

