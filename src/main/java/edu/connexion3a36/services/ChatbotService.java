package edu.connexion3a36.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chatbot Service for guiding new players through the platform
 */
public class ChatbotService {

    private final Map<String, String> responses;
    private final List<String> suggestions;

    public ChatbotService() {
        this.responses = initializeResponses();
        this.suggestions = initializeSuggestions();
    }

    /**
     * Initialize chatbot responses for common questions
     */
    private Map<String, String> initializeResponses() {
        Map<String, String> map = new HashMap<>();

        // Welcome and general info
        map.put("hello", "👋 Welcome to RankUp E-Sports! I'm your assistant here to help new players.\n\nI can help you with:\n• How to get started\n• Understanding profiles\n• Joining teams\n• Participating in tournaments\n• And much more!\n\nWhat would you like to know?");
        map.put("hi", map.get("hello"));
        map.put("hey", map.get("hello"));
        map.put("help", map.get("hello"));

        // Getting started
        map.put("get started", "🚀 **Getting Started Guide:**\n\n1. **Complete Your Profile** - Add a profile picture and bio\n2. **Find Your Stats** - Check your match history and rankings\n3. **Join a Team** - Browse teams and request to join\n4. **Enter Tournaments** - Participate in competitive events\n5. **Check Leaderboard** - See where you rank globally\n\nWould you like detailed info on any of these?");
        map.put("start", map.get("get started"));
        map.put("getting started", map.get("get started"));

        // Profile information
        map.put("profile", "👤 **Your Profile:**\n\n• Click on your username in the top-right corner\n• Edit your information (username, email, password)\n• Add a profile picture and biography\n• View your statistics (matches, wins, KDA)\n• See your tournament history\n• Manage your teams\n\nYour profile is your identity in RankUp!");

        map.put("edit profile", map.get("profile"));
        map.put("my profile", map.get("profile"));
        map.put("profile picture", "📸 To add a profile picture:\n1. Go to your Profile\n2. Click on the avatar placeholder\n3. Select an image from your computer\n4. Save changes\n\nNote: Profile pictures help teammates recognize you!");

        // Teams
        map.put("teams", "🏆 **Teams Guide:**\n\n**Joining a Team:**\n1. Go to Teams section\n2. Browse available teams\n3. Click 'Request to Join'\n4. Wait for team captain approval\n\n**Creating a Team:**\n1. Go to Teams section\n2. Click 'Create New Team'\n3. Enter team name and description\n4. Invite players to join\n5. Start competing together!\n\nTeams unlock exclusive tournaments!");

        map.put("join team", "📝 To join a team:\n\n1. Navigate to the **Teams** section\n2. Browse teams looking for members\n3. Click **'Request to Join'** on a team\n4. Wait for the team captain to approve\n5. Once approved, you're part of the team!\n\nTip: Read team descriptions to find teams that match your skill level!");

        map.put("create team", "🏅 To create a team:\n\n1. Go to **Teams** section\n2. Click **'Create New Team'**\n3. Enter:\n   • Team name\n   • Description\n   • Game focus\n4. Set team rules/requirements\n5. Invite players or wait for join requests\n6. You become the team captain!\n\nResponsibility: Manage team members and organize tournaments!");

        map.put("team captain", "⚡ **Team Captain Responsibilities:**\n\n• Approve/reject join requests\n• Manage team members\n• Register for tournaments\n• Organize practice sessions\n• Communicate team goals\n• Handle team settings\n\nBe a great leader to build a strong team!");

        // Tournaments
        map.put("tournaments", "🎮 **Tournaments Guide:**\n\n**How to Participate:**\n1. Go to **Tournaments** section\n2. Browse upcoming tournaments\n3. Click 'Register'\n4. Choose your team (if required)\n5. Wait for tournament start\n\n**Tournament Types:**\n• Solo: Individual competition\n• Team: Play with your squad\n• Ranked: Competitive ranking\n• Casual: For fun\n\nWin matches, earn points, climb rankings!");

        map.put("tournament", map.get("tournaments"));
        map.put("register tournament", "✅ **To Register for a Tournament:**\n\n1. Browse the **Tournaments** section\n2. Find a tournament that interests you\n3. Check the details (date, rules, entry fee)\n4. Click **'Register'**\n5. Select your team (if applicable)\n6. Confirm registration\n\nYou'll receive notifications about match schedules!");

        map.put("my tournament", "📅 To check your tournaments:\n\n1. Go to **Tournaments** section\n2. Click **'My Tournaments'** tab\n3. See:\n   • Upcoming matches\n   • Tournament standings\n   • Match results\n   • Your team's progress\n\nStay updated on all your competitions!");

        // Matches and statistics
        map.put("matches", "⚽ **Matches Information:**\n\n• View your recent matches\n• Check match statistics (KDA, performance)\n• See opponent information\n• Review match replays (if available)\n• Track your win rate\n\nYour match history helps you improve!");

        map.put("statistics", "📊 **Your Statistics:**\n\nView at your **Profile** or **Dashboard**:\n• Total matches played\n• Wins / Losses\n• Win rate percentage\n• Average KDA (Kill/Death/Assist)\n• MVP count\n• Ranking points\n• Favorite games\n\nTrack your progress over time!");

        map.put("kda", "🎯 **What is KDA?**\n\nKDA = Kill / Death / Assist ratio\n\n• **Kills**: Opponents you eliminated\n• **Deaths**: Times you were eliminated\n• **Assists**: Times you helped teammates get kills\n\n**Good KDA**: 1.5+ is excellent\n**How to improve**: Better positioning, team communication");

        map.put("ranking", "🏅 **Ranking System:**\n\n• Matches contribute to your ranking\n• Win = +ranking points\n• Loss = -ranking points\n• Draw = minimal points\n• Bonus points for MVP\n• Check **Leaderboard** to see your position\n\nClimb the ranks by playing well!");

        map.put("leaderboard", "🌟 **Leaderboard:**\n\n1. Go to **Leaderboard** section\n2. View top players globally\n3. See your rank position\n4. Filter by:\n   • Game type\n   • Region\n   • Time period\n5. Click on players to see their stats\n\nBe inspired by top players!");

        // Notifications and communication
        map.put("notifications", "🔔 **Stay Updated:**\n\nYou'll receive notifications for:\n• Tournament registration openings\n• Match schedule confirmations\n• Team invitations\n• Ranking changes\n• Important platform announcements\n\nCheck Notifications section regularly!");

        map.put("messages", "💬 **Direct Messages:**\n\nIn **Messages** section:\n• Message teammates\n• Communicate with opponents\n• Chat with friends\n• Discuss strategies\n\nGood communication = better teamwork!");

        // Dashboard
        map.put("dashboard", "📱 **Dashboard Overview:**\n\nYour personal hub showing:\n• Recent matches\n• Upcoming tournaments\n• Team notifications\n• Ranking progress\n• Quick statistics\n• Important events\n\nCheck your dashboard regularly for updates!");

        // Account security
        map.put("password", "🔐 **Change Your Password:**\n\n1. Go to **Profile** → **Settings**\n2. Click **'Change Password'**\n3. Enter current password\n4. Enter new password (min. 6 characters)\n5. Confirm new password\n6. Save\n\nKeep your account secure!");

        map.put("forgot password", "🔑 **Forgot Your Password?**\n\n1. Click **'Forgot Password?'** on login page\n2. Enter your email address\n3. Check your email for OTP code\n4. Enter the 6-digit code\n5. Create a new password\n6. Login with new password\n\nCheck your spam folder if email doesn't arrive!");

        // Troubleshooting
        map.put("problem", "😞 **Having Issues?**\n\nCommon problems:\n• **Can't join team?** - Check team requirements\n• **Registration failed?** - Refresh and try again\n• **Match not showing?** - Check notifications\n• **Stats not updating?** - Wait a moment, refresh page\n\nTry refreshing the page or contact support!");

        map.put("error", map.get("problem"));
        map.put("help me", map.get("problem"));

        // Budget and expenses (if applicable)
        map.put("budget", "💰 **Budget & Expenses:**\n\nTrack tournament and team expenses:\n• Entry fees\n• Equipment costs\n• Travel expenses\n• Prize distributions\n\nGo to **Budget** section for details!");

        // Default response
        map.put("default", "I'm not sure about that. Try asking me about:\n• Getting started\n• Profiles\n• Teams\n• Tournaments\n• Matches\n• Rankings\n• Or just say 'help'!");

        return map;
    }

    /**
     * Initialize quick suggestion buttons for easy navigation
     */
    private List<String> initializeSuggestions() {
        List<String> list = new ArrayList<>();
        list.add("Getting Started");
        list.add("Join a Team");
        list.add("Find Tournaments");
        list.add("My Profile");
        list.add("Statistics");
        list.add("Leaderboard");
        return list;
    }

    /**
     * Get chatbot response based on user input
     */
    public String getResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "Please type something! 😊";
        }

        String input = userInput.toLowerCase().trim();

        // Check for exact matches
        if (responses.containsKey(input)) {
            return responses.get(input);
        }

        // Check for partial matches (keywords)
        for (String key : responses.keySet()) {
            if (input.contains(key) || key.contains(input)) {
                return responses.get(key);
            }
        }

        // Default response
        return responses.get("default");
    }

    /**
     * Get list of quick suggestions
     */
    public List<String> getSuggestions() {
        return new ArrayList<>(suggestions);
    }

    /**
     * Get welcome message
     */
    public String getWelcomeMessage() {
        return "👋 Welcome to RankUp E-Sports!\n\nI'm your friendly guide here to help you get started.\n\nWhat would you like to know about?";
    }

    /**
     * Get list of all topics
     */
    public List<String> getAvailableTopics() {
        return new ArrayList<>(responses.keySet());
    }
}

