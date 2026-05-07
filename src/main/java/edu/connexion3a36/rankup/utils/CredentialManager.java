package edu.connexion3a36.rankup.utils;

import java.util.prefs.Preferences;
import java.util.Base64;

/**
 * Manages secure storage of user credentials using Java Preferences API.
 * This class handles "Remember Me" functionality for the login screen.
 */
public class CredentialManager {

    private static final Preferences preferences = Preferences.userNodeForPackage(CredentialManager.class);
    private static final String EMAIL_KEY = "rankup_email";
    private static final String PASSWORD_KEY = "rankup_password";
    private static final String REMEMBER_ME_KEY = "rankup_remember_me";

    /**
     * Saves user credentials if "Remember Me" is checked
     * @param email User email
     * @param password User password
     * @param rememberMe Whether to save credentials
     */
    public static void saveCredentials(String email, String password, boolean rememberMe) {
        if (rememberMe && !email.isBlank() && !password.isBlank()) {
            // Encode password in base64 for basic protection
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            preferences.put(EMAIL_KEY, email);
            preferences.put(PASSWORD_KEY, encodedPassword);
            preferences.putBoolean(REMEMBER_ME_KEY, true);
        } else {
            clearCredentials();
        }
    }

    /**
     * Retrieves saved credentials if available
     * @return Array [email, password] or null if not saved
     */
    public static String[] getCredentials() {
        boolean isRemembered = preferences.getBoolean(REMEMBER_ME_KEY, false);
        if (!isRemembered) {
            return null;
        }

        String email = preferences.get(EMAIL_KEY, null);
        String encodedPassword = preferences.get(PASSWORD_KEY, null);

        if (email != null && encodedPassword != null) {
            try {
                String decodedPassword = new String(Base64.getDecoder().decode(encodedPassword));
                return new String[]{email, decodedPassword};
            } catch (IllegalArgumentException e) {
                System.err.println("Error decoding saved password: " + e.getMessage());
                clearCredentials();
            }
        }
        return null;
    }

    /**
     * Checks if credentials are saved
     * @return true if credentials are saved
     */
    public static boolean hasRememberedCredentials() {
        return preferences.getBoolean(REMEMBER_ME_KEY, false);
    }

    /**
     * Clears all saved credentials
     */
    public static void clearCredentials() {
        preferences.remove(EMAIL_KEY);
        preferences.remove(PASSWORD_KEY);
        preferences.putBoolean(REMEMBER_ME_KEY, false);
    }
}

