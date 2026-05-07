package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.config.AIConfig;
import java.util.*;

/**
 * Chatbot service for recommending appropriate bans based on violation types.
 * Now with AI support! Uses OpenAI API for intelligent responses.
 * Falls back to rule-based recommendations if AI not configured.
 */
public class BanRecommendationChatbot {

    private static boolean USE_AI = AIConfig.isConfigured();

    private static final Map<String, BanRecommendation> VIOLATION_RECOMMENDATIONS = new LinkedHashMap<>();

    static {
        // Initialize violation recommendations
        VIOLATION_RECOMMENDATIONS.put("cheating", new BanRecommendation(
                "Cheating",
                "Game Ban (Permanent or Extended)",
                "3-12 months",
                "Using unauthorized tools, hacks, exploits, or unfair advantages. This is the most serious violation.",
                "⚠️ Consider permanent ban for repeat offenders or severe cases. Evidence must be clear and documented."
        ));

        VIOLATION_RECOMMENDATIONS.put("aimbot", new BanRecommendation(
                "Aimbot/Hacks",
                "Game Ban (Permanent)",
                "Permanent",
                "Using aim assistance tools or game hacks.",
                "⚠️ Typically results in permanent ban. Check anti-cheat logs for confirmation."
        ));

        VIOLATION_RECOMMENDATIONS.put("wallhack", new BanRecommendation(
                "Wallhack/Vision Cheats",
                "Game Ban (Permanent)",
                "Permanent",
                "Seeing through walls or unfair map vision.",
                "⚠️ Usually permanent. Requires strong evidence from replay analysis."
        ));

        VIOLATION_RECOMMENDATIONS.put("cussing", new BanRecommendation(
                "Cussing/Offensive Language",
                "Match/Tournament Ban (Progressive)",
                "1-7 days (first), 7-30 days (repeat)",
                "Using profanity, slurs, or offensive language in chat or voice.",
                "🟡 First offense: 1-7 day ban. Repeat offenders: escalate to 7-30 days or longer."
        ));

        VIOLATION_RECOMMENDATIONS.put("toxicity", new BanRecommendation(
                "Toxic Behavior",
                "Match/Tournament Ban (Progressive)",
                "3-14 days",
                "Harassment, bullying, or toxic conduct toward other players.",
                "🟡 Escalate based on severity. Collect chat logs as evidence. Consider team ban for team harassment."
        ));

        VIOLATION_RECOMMENDATIONS.put("harassment", new BanRecommendation(
                "Player Harassment",
                "Tournament Ban (Medium-Long)",
                "7-30 days",
                "Targeting, threatening, or persistently harassing another player.",
                "🔴 Serious violation. Document all evidence. Consider longer bans for repeat offenders."
        ));

        VIOLATION_RECOMMENDATIONS.put("throwing", new BanRecommendation(
                "Match Throwing/Intentional Feeding",
                "Match Ban (Short-Medium)",
                "1-3 days",
                "Intentionally losing matches or feeding opponents.",
                "🟡 First offense: 1 day. Repeat: 3+ days. Requires chat/gameplay evidence."
        ));

        VIOLATION_RECOMMENDATIONS.put("alt account", new BanRecommendation(
                "Banned Alt Account Use",
                "Game Ban (Extended)",
                "3-12 months",
                "Playing on alternative account while main account is banned.",
                "🔴 Circumventing bans. Should match or exceed original ban duration."
        ));

        VIOLATION_RECOMMENDATIONS.put("scripting", new BanRecommendation(
                "Scripting/Macro Use",
                "Game Ban (Permanent)",
                "Permanent",
                "Using scripts or macros for automated advantages.",
                "⚠️ Usually permanent. Similar severity to cheating."
        ));

        VIOLATION_RECOMMENDATIONS.put("account sharing", new BanRecommendation(
                "Account Sharing",
                "Match/Tournament Ban (Medium)",
                "3-7 days",
                "Playing on someone else's account or letting others play yours.",
                "🟡 Can affect tournament eligibility. Verify with account owner."
        ));

        VIOLATION_RECOMMENDATIONS.put("abusive behavior", new BanRecommendation(
                "Abusive Behavior",
                "Tournament Ban (Medium-Long)",
                "7-30 days",
                "Threatening, defaming, or creating hostile environment.",
                "🔴 Serious violation. Escalate for repeat offenders. Consider permanent ban for severe threats."
        ));

        VIOLATION_RECOMMENDATIONS.put("match fixing", new BanRecommendation(
                "Match Fixing / Collusion",
                "Game Ban (Permanent)",
                "Permanent",
                "Intentional collaboration to fix match outcomes for betting or advantage.",
                "⚠️ Most serious violation. Involves integrity. Usually permanent with possible league-wide blacklist."
        ));

        VIOLATION_RECOMMENDATIONS.put("spam", new BanRecommendation(
                "Chat Spam",
                "Match Ban (Short)",
                "6 hours - 1 day",
                "Repeated spam messages in game chat.",
                "🟡 First offense: warning. Repeat: short ban. Automatic mute may be better than ban."
        ));

        VIOLATION_RECOMMENDATIONS.put("exploiting bug", new BanRecommendation(
                "Exploiting Game Bug",
                "Match/Tournament Ban (Short-Medium)",
                "1-7 days",
                "Intentionally exploiting bugs for competitive advantage.",
                "🟡 Depends on severity of bug. Lesser severity than cheating if bug is accidental discovery."
        ));

        VIOLATION_RECOMMENDATIONS.put("unsportsmanlike", new BanRecommendation(
                "Unsportsmanlike Conduct",
                "Match Ban (Short-Medium)",
                "1-3 days",
                "Disrespectful behavior, unnecessary taunting, or poor sportsmanship.",
                "🟡 First offense: 1 day. Escalate based on severity and frequency."
        ));
    }

    public static class BanRecommendation {
        public final String violationType;
        public final String recommendedBan;
        public final String suggestedDuration;
        public final String description;
        public final String notes;

        BanRecommendation(String violationType, String recommendedBan, String suggestedDuration,
                         String description, String notes) {
            this.violationType = violationType;
            this.recommendedBan = recommendedBan;
            this.suggestedDuration = suggestedDuration;
            this.description = description;
            this.notes = notes;
        }

        @Override
        public String toString() {
            return String.format(
                    "Violation: %s\n" +
                    "Recommended Ban: %s\n" +
                    "Duration: %s\n" +
                    "Description: %s\n" +
                    "Notes: %s",
                    violationType, recommendedBan, suggestedDuration, description, notes
            );
        }
    }

    /**
     * Get a ban recommendation for a specific violation type.
     * @param violationType The type of violation (e.g., "cheating", "cussing")
     * @return BanRecommendation with guidance, or null if not found
     */
    public static BanRecommendation getRecommendation(String violationType) {
        if (violationType == null || violationType.trim().isEmpty()) {
            return null;
        }

        String normalized = violationType.toLowerCase().trim();

        // Direct match
        if (VIOLATION_RECOMMENDATIONS.containsKey(normalized)) {
            return VIOLATION_RECOMMENDATIONS.get(normalized);
        }

        // Fuzzy matching for partial words
        for (String key : VIOLATION_RECOMMENDATIONS.keySet()) {
            if (normalized.contains(key) || key.contains(normalized)) {
                return VIOLATION_RECOMMENDATIONS.get(key);
            }
        }

        return null;
    }

    /**
     * Get all available violation types.
     * @return List of violation types
     */
    public static List<String> getAvailableViolationTypes() {
        return new ArrayList<>(VIOLATION_RECOMMENDATIONS.keySet());
    }
    
    /**
     * Clear conversation history (start fresh)
     */
    public static void clearHistory() {
        AIApiClient.clearHistory();
    }

     /**
      * Process user input and generate response - supports conversational interaction
      * Uses Mistral AI API if configured, otherwise falls back to rule-based responses
      */
     public static String chat(String userInput) {
         if (userInput == null || userInput.trim().isEmpty()) {
             return "Tell me about the violation or behavior you want to discuss. I can help you find the right punishment! 🎯";
         }

         // Check if input is esports-related first
         String lowerInput = userInput.toLowerCase();
         if (!isEsportsRelated(lowerInput)) {
             return "❌ I'm specialized in eSports punishments only. Please ask me about:\n\n" +
                     "🎮 eSports violations like:\n" +
                     "  • Cheating (aimbots, hacks, exploits)\n" +
                     "  • Cussing & toxicity\n" +
                     "  • Match fixing & collusion\n" +
                     "  • Harassment & abuse\n" +
                     "  • Account sharing\n" +
                     "  • Match throwing\n\n" +
                     "For example: 'What about cheating?' or 'Which punishment for cussing?'\n\n" +
                     "Type 'help' to see all violations I can help with! 🎯";
         }

         // Try to use AI API if configured
         if (USE_AI) {
            try {
                System.out.println("DEBUG: Attempting to call Mistral AI API...");
                System.out.println("DEBUG: API Key configured: " + AIConfig.isConfigured());
                System.out.println("DEBUG: User input: " + userInput);
                System.out.println("DEBUG: Model: " + AIConfig.MODEL);
                
                String aiResponse = AIApiClient.sendMessage(userInput);
                System.out.println("DEBUG: Response received: " + aiResponse);
                
                // If AI API works, return its response
                if (aiResponse != null && !aiResponse.isEmpty() && !aiResponse.contains("❌")) {
                    System.out.println("DEBUG: Mistral AI API successful, returning response");
                    return aiResponse;
                }
                
                // If API returns error, show it and fall back
                System.out.println("DEBUG: API returned error, falling back to rule-based");
                System.out.println("DEBUG: Error message: " + aiResponse);
                return "⚠️ AI API Issue: " + aiResponse + "\n\nFalling back to manual recommendations:\n\n" + chatRuleBased(userInput);
                
            } catch (Exception e) {
                // Log error and show it to user
                System.err.println("DEBUG: Exception calling Mistral AI API: " + e.getMessage());
                e.printStackTrace();
                return "❌ AI Connection Error: " + e.getMessage() + "\n\nPlease check:\n" +
                       "1. Internet connection\n" +
                       "2. Mistral API key in AIConfig.java line 20\n" +
                       "3. Mistral API is accessible\n" +
                       "4. Your account has sufficient credits\n\n" +
                       "Falling back to recommendations:\n\n" + chatRuleBased(userInput);
            }
        }

        // Fall back to rule-based responses (original logic)
        System.out.println("DEBUG: Using rule-based responses (AI not configured)");
        return chatRuleBased(userInput);
    }

    /**
     * Rule-based responses (fallback when AI is not available)
     */
    private static String chatRuleBased(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "Tell me about the violation or behavior you want to discuss. I can help you find the right punishment! 🎯";
        }

        String input = userInput.toLowerCase().trim();
        
        // Check if the question is about esports violations
        if (!isEsportsRelated(input)) {
            return "❌ I'm specialized in eSports punishments only. Please ask me about:\n\n" +
                    "🎮 eSports violations like:\n" +
                    "  • Cheating (aimbots, hacks, exploits)\n" +
                    "  • Cussing & toxicity\n" +
                    "  • Match fixing & collusion\n" +
                    "  • Harassment & abuse\n" +
                    "  • Account sharing\n" +
                    "  • Match throwing\n\n" +
                    "For example: 'What about cheating?' or 'Which punishment for cussing?'\n\n" +
                    "Type 'help' to see all violations I can help with! 🎯";
        }

        // Conversational responses
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "👋 Hello! I'm here to help you discuss and decide on appropriate punishments for eSports violations.\n\n" +
                    "You can:\n" +
                    "• Ask about specific violations (e.g., 'what about cheating?')\n" +
                    "• Compare punishments (e.g., 'match ban or tournament ban for cussing?')\n" +
                    "• Discuss severity (e.g., 'is this serious?')\n" +
                    "• Ask for alternatives (e.g., 'what are other options?')\n" +
                    "• Type 'help' to see all violation types\n\n" +
                    "What would you like to discuss?";
        }

        if (input.contains("help") || input.contains("list") || input.contains("all")) {
            return formatAvailableViolations();
        }

        // Check for comparative questions (which is better)
        if (input.contains("which") && (input.contains("better") || input.contains("suitable") || input.contains("appropriate"))) {
            BanRecommendation rec = findRecommendationFromContext(input);
            if (rec != null) {
                return String.format(
                        "⚖️ COMPARING PUNISHMENTS\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Violation: %s\n\n" +
                        "✅ RECOMMENDED: %s\n" +
                        "Duration: %s\n\n" +
                        "Why this choice:\n%s\n\n" +
                        "Alternative options:\n%s\n\n" +
                        "Summary: The recommended ban (%s) is best because it balances\n" +
                        "impact and fairness for this type of violation.",
                        rec.violationType,
                        rec.recommendedBan,
                        rec.suggestedDuration,
                        rec.description,
                        getAlternatives(rec),
                        rec.recommendedBan.split("\\(")[0].trim()
                );
            }
        }

        // Check for questions about severity
        if (input.contains("serious") || input.contains("severity") || input.contains("how serious")) {
            BanRecommendation rec = findRecommendationFromContext(input);
            if (rec != null) {
                return String.format(
                        "📊 SEVERITY ASSESSMENT\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Violation: %s\n\n" +
                        "This is %s\n\n" +
                        "Recommended Action: %s\n" +
                        "Duration: %s\n\n" +
                        "%s",
                        rec.violationType,
                        getSeverityDescription(rec),
                        rec.recommendedBan,
                        rec.suggestedDuration,
                        rec.notes
                );
            }
        }

        // Check for questions about alternatives
        if (input.contains("alternative") || input.contains("instead of") || input.contains("other option")) {
            BanRecommendation rec = findRecommendationFromContext(input);
            if (rec != null) {
                return String.format(
                        "🔄 ALTERNATIVE ACTIONS\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "For: %s\n\n" +
                        "Primary: %s (%s)\n\n" +
                        "Alternatives to consider:\n" +
                        "%s\n\n" +
                        "Choose based on:\n" +
                        "• Severity of violation\n" +
                        "• Player history\n" +
                        "• Impact on competition\n" +
                        "• Evidence strength",
                        rec.violationType,
                        rec.recommendedBan,
                        rec.suggestedDuration,
                        getAlternatives(rec)
                );
            }
        }

        // Check for questions about guidelines
        if (input.contains("guide") || input.contains("guideline") || input.contains("how to")) {
            return "📋 PUNISHMENT GUIDELINES\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "When deciding on punishments:\n\n" +
                    "1️⃣ FIRST OFFENSE\n" +
                    "   • Use lower end of suggested duration\n" +
                    "   • Consider if it was accidental\n\n" +
                    "2️⃣ REPEAT OFFENDER\n" +
                    "   • Escalate to middle range\n" +
                    "   • Consider pattern of behavior\n\n" +
                    "3️⃣ SERIOUS/EVIDENCE\n" +
                    "   • Use upper end of range\n" +
                    "   • Document all evidence\n\n" +
                    "4️⃣ CONTEXT MATTERS\n" +
                    "   • Tournament tier level\n" +
                    "   • Player impact\n" +
                    "   • Team reputation\n\n" +
                    "What violation are you dealing with?";
        }

        // Direct violation lookup with recommendation
        BanRecommendation rec = getRecommendation(input);
        if (rec != null) {
            return String.format(
                    "🎯 PUNISHMENT RECOMMENDATION\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "Violation: %s\n" +
                    "Recommended: %s\n" +
                    "Duration: %s\n\n" +
                    "Description:\n%s\n\n" +
                    "📝 ADMIN GUIDANCE:\n%s\n\n" +
                    "Want to discuss? Ask about:\n" +
                    "• Which is better? (match or tournament ban?)\n" +
                    "• Is this serious?\n" +
                    "• What are other options?",
                    rec.violationType, rec.recommendedBan, rec.suggestedDuration,
                    rec.description, rec.notes
            );
        }

        return "I'm not sure about that. Could you:\n" +
                "• Describe the violation more clearly?\n" +
                "• Tell me what behavior occurred?\n" +
                "• Ask 'Which punishment is suitable?' to compare options\n" +
                "• Ask for 'help' to see all violation types?\n\n" +
                "For example: 'What about cheating?' or 'Which is better for cussing - match ban or tournament ban?'";
    }

    /**
     * Find recommendation from natural language input
     */
    private static BanRecommendation findRecommendationFromContext(String input) {
        for (String key : VIOLATION_RECOMMENDATIONS.keySet()) {
            if (input.contains(key)) {
                return VIOLATION_RECOMMENDATIONS.get(key);
            }
        }
        return null;
    }

    /**
     * Get severity description for a violation
     */
    private static String getSeverityDescription(BanRecommendation rec) {
        if (rec.suggestedDuration.contains("Permanent")) {
            return "a CRITICAL violation - the most serious category";
        } else if (rec.suggestedDuration.contains("12") || rec.suggestedDuration.contains("30")) {
            return "a SERIOUS violation - requires strong action";
        } else if (rec.suggestedDuration.contains("7") || rec.suggestedDuration.contains("14")) {
            return "a MODERATE violation - needs attention but scalable";
        } else {
            return "a MINOR violation - smaller impact but still important";
        }
    }

    /**
     * Get alternative punishments for a violation
     */
    private static String getAlternatives(BanRecommendation rec) {
        if (rec.recommendedBan.contains("Game Ban")) {
            return "• Tournament Ban (shorter, tests reform)\n" +
                    "• Match Ban (least restrictive, multiple minor bans)\n" +
                    "• Probation + monitoring (if first offense)";
        } else if (rec.recommendedBan.contains("Tournament Ban")) {
            return "• Match Bans (multiple, escalating)\n" +
                    "• Game Ban (if repeated)\n" +
                    "• Warnings + fines (if minor)";
        } else if (rec.recommendedBan.contains("Match Ban")) {
            return "• Warning + monitoring\n" +
                    "• Tournament Ban (if serious)\n" +
                    "• Mandatory training/reform";
        }
        return "• Shorter duration with probation\n" +
                "• Longer duration with monitoring\n" +
                "• Combination with education/training";
    }

    /**
     * Check if the user input is related to eSports violations
     */
    private static boolean isEsportsRelated(String input) {
        // Keywords related to esports violations
        String[] esportsKeywords = {
            "cheating", "hack", "aimbbot", "wallhack", "cheat", "exploit", "unfair",
            "cuss", "toxic", "toxicity", "harassment", "abuse", "ban", "punish", "violation",
            "match", "tournament", "game", "throwing", "feed", "alt account", "script", "macro",
            "sharing", "account", "fixing", "collusion", "spam", "bug", "unsportsmanlike",
            "esport", "player", "admin", "punishment", "reclamation", "severity", "guidelines",
            "help", "list", "all", "which", "better", "suitable", "appropriate", "alternatives",
            "serious", "serious?", "option", "options", "how", "what", "why"
        };
        
        for (String keyword : esportsKeywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        
        // Also check if it matches any violation type
        return getRecommendation(input) != null;
    }

    private static String formatAvailableViolations() {
        StringBuilder sb = new StringBuilder("📋 AVAILABLE VIOLATIONS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        List<String> types = getAvailableViolationTypes();
        
        // Group by severity
        sb.append("🔴 CRITICAL (Permanent/Long):\n");
        for (String type : types) {
            if (VIOLATION_RECOMMENDATIONS.get(type).suggestedDuration.contains("Permanent")) {
                sb.append("  • ").append(type).append("\n");
            }
        }
        
        sb.append("\n🟠 SERIOUS (Medium-Long):\n");
        for (String type : types) {
            String duration = VIOLATION_RECOMMENDATIONS.get(type).suggestedDuration;
            if ((duration.contains("7") || duration.contains("30")) && !duration.contains("Permanent")) {
                sb.append("  • ").append(type).append("\n");
            }
        }
        
        sb.append("\n🟡 MODERATE (Short-Medium):\n");
        for (String type : types) {
            String duration = VIOLATION_RECOMMENDATIONS.get(type).suggestedDuration;
            if (duration.contains("1-") || duration.contains("3-")) {
                sb.append("  • ").append(type).append("\n");
            }
        }
        
        sb.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Ask about any of these or use:\n");
        sb.append("• 'severity' - discuss how serious\n");
        sb.append("• 'alternatives' - other options\n");
        sb.append("• 'guidelines' - punishment rules");
        return sb.toString();
    }
}





