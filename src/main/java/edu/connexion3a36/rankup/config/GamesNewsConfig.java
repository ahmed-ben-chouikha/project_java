package edu.connexion3a36.rankup.config;

/**
 * Configuration for Games News API integration.
 * 
 * ⚠️⚠️⚠️ IMPORTANT: HOW TO PASTE YOUR GAMES NEWS API KEY ⚠️⚠️⚠️
 * 
 * STEP 1: Get your API key
 *   1. Go to your news provider (e.g., NewsAPI.org, etc.)
 *   2. Copy the generated API key
 * 
 * STEP 2: Paste it in THIS FILE
 *   Replace the text "PASTE_YOUR_GAMES_NEWS_API_KEY_HERE" on line 18
 *   with your actual API key
 *   
 * STEP 3: Save the file and restart the application
 */
public class GamesNewsConfig {
    
    // ⬇️⬇️⬇️ PASTE YOUR GAMES NEWS API KEY BETWEEN THE QUOTES BELOW ⬇️⬇️⬇️
    public static final String API_KEY = "aa928959155a4a51b2afff25c10435d1";
    
    // API URL for Games News
    public static final String API_URL = "https://newsapi.org/v2/everything?q=gaming&language=fr&sortBy=publishedAt";
    
    /**
     * Check if API key is configured
     */
    public static boolean isConfigured() {
        return API_KEY != null && 
               !API_KEY.isEmpty() && 
               !API_KEY.equals("PASTE_YOUR_GAMES_NEWS_API_KEY_HERE");
    }
}
