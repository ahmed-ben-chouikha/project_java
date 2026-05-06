package edu.connexion3a36.rankup.config;

/**
 * Configuration for Mistral AI API integration.
 * 
 * ⚠️⚠️⚠️ IMPORTANT: HOW TO PASTE YOUR MISTRAL AI API KEY ⚠️⚠️⚠️
 * 
 * STEP 1: Get your API key
 *   1. Go to https://console.mistral.ai/api-keys
 *   2. Login with your Mistral account (create one if needed)
 *   3. Click "Create new API key"
 *   4. Copy the generated API key
 * 
 * STEP 2: Paste it in THIS FILE
 *   Replace the text "PASTE_YOUR_MISTRAL_API_KEY_HERE" on line 20
 *   with your actual Mistral API key
 *   
 *   EXAMPLE:
 *   Before: public static final String API_KEY = "PASTE_YOUR_MISTRAL_API_KEY_HERE";
 *   After:  public static final String API_KEY = "abc123def456ghi789jkl012mno345pqr";
 * 
 * STEP 3: Save the file (Ctrl+S or Cmd+S)
 * 
 * STEP 4: Restart the application
 * 
 * 🔒 SECURITY NOTE: Never commit this file with your API key to version control!
 */
public class AIConfig {
    
    // ⬇️⬇️⬇️ PASTE YOUR MISTRAL AI API KEY BETWEEN THE QUOTES BELOW ⬇️⬇️⬇️
    public static final String API_KEY = "dUDzbw5v8KyT1ODzelryCH9FZlGq8yjM";
    
    // Mistral AI API endpoint
    public static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    
    // Model to use - Latest Mistral models
    // Recommended options:
    // - "mistral-large-latest" (newest, most capable)
    // - "mistral-medium-latest" (balanced)
    // - "mistral-small-latest" (fast, cheaper)
    public static final String MODEL = "mistral-large-latest";
    
    // Temperature (0-1, where:
    // 0 = deterministic, precise answers
    // 1 = creative, varied answers
    // Default 0.7 = balanced
    public static final double TEMPERATURE = 0.7;
    
    // Maximum tokens for response (higher = longer responses)
    public static final int MAX_TOKENS = 1000;
    
    // System prompt for the chatbot
    public static final String SYSTEM_PROMPT = 
        "You are an expert eSports punishment and ban recommendation assistant. " +
        "Your role is to help admins determine appropriate bans and punishments for eSports violations.\n\n" +
        "You ONLY answer questions about:\n" +
        "• eSports and gaming topics\n" +
        "• Video games and competitive gaming\n" +
        "• Bans, punishments, and disciplinary actions in games\n" +
        "• eSports violations and rule enforcement\n" +
        "• Gaming terminology and eSports concepts\n\n" +
        "Violation types you can help with:\n" +
        "• Cheating (aimbots, wallhacks, hacks, exploits)\n" +
        "• Cussing and offensive language in gaming\n" +
        "• Toxic behavior and harassment in games\n" +
        "• Match fixing and collusion in eSports\n" +
        "• Account sharing in games\n" +
        "• Intentional feeding/throwing in competitive games\n" +
        "• Spam and disruptive behavior in gaming\n" +
        "• Unsportsmanlike conduct in eSports\n\n" +
        "IMPORTANT RULES:\n" +
        "- If a question is NOT about eSports, gaming, games, or bans, respond:\n" +
        "  \"I can only help with eSports, gaming, and punishment-related questions. Please ask about gaming topics, bans, or violations.\"\n" +
        "- Do NOT answer questions about unrelated topics like weather, politics, food, sports (non-eSports), etc.\n" +
        "- Stay focused on your role as an eSports moderation and ban recommendation specialist\n\n" +
        "For each violation:\n" +
        "1. Provide a clear recommendation with ban type and duration\n" +
        "2. Explain the reasoning\n" +
        "3. Consider context (first offense vs repeat, severity, evidence)\n" +
        "4. Suggest alternatives if appropriate\n" +
        "5. Always maintain a professional, fair tone\n\n" +
        "Format your responses clearly with emojis and sections for readability.";

    // Prompt for Urgency Detection Agent
    public static final String URGENCY_PROMPT = 
        "You are an AI sentiment and urgency analyst for eSports reclamations.\n" +
        "Analyze the provided reclamation description and determine:\n" +
        "1. URGENCY LEVEL (Low, Medium, High, Critical)\n" +
        "2. SENTIMENT/EMOTIONAL STATE (Calm, Annoyed, Furious, etc.)\n" +
        "3. SUMMARY (One sentence summarizing the issue)\n" +
        "4. RECOMMENDATION (One sentence on how the admin should prioritize this)\n\n" +
        "FORMATTING RULES:\n" +
        "- Use Emojis to make it readable.\n" +
        "- Be professional but concise.\n" +
        "- If the user uses all caps or profanity, mark as CRITICAL/FURIOUS.";
    
    /**
     * Check if API key is configured (not the default placeholder)
     */
    public static boolean isConfigured() {
        return API_KEY != null && 
               !API_KEY.isEmpty() && 
               !API_KEY.equals("PASTE_YOUR_MISTRAL_API_KEY_HERE");
    }
    
    /**
     * Get API key with validation
     */
    public static String getApiKey() {
        if (!isConfigured()) {
            throw new IllegalStateException(
                "❌ Mistral AI API key is NOT configured.\n\n" +
                "To enable the AI chatbot:\n" +
                "1. Go to https://console.mistral.ai/api-keys\n" +
                "2. Create and copy your API key\n" +
                "3. Open AIConfig.java file\n" +
                "4. Find line 20 with: public static final String API_KEY = \"...\"\n" +
                "5. Replace 'PASTE_YOUR_MISTRAL_API_KEY_HERE' with your actual key\n" +
                "6. Save the file and restart the application\n\n" +
                "Until then, the chatbot will use rule-based recommendations."
            );
        }
        return API_KEY;
    }
}



