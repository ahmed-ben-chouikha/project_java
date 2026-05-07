# Chatbot Feature - Guide for New Players

## Overview

The RankUp E-Sports platform now includes an intelligent **Chatbot Assistant** that helps new players get started and navigate the platform. The chatbot is accessible via a floating button in the bottom-right corner of the screen.

## Features

### 🤖 AI-Powered Responses
- **Smart Keyword Matching**: Understands player questions and provides relevant responses
- **Comprehensive Knowledge Base**: Covers all major platform features
- **Context-Aware Suggestions**: Quick buttons for common topics

### 💬 Interactive Interface
- **Real-time Chat**: Send and receive messages instantly
- **Message History**: View entire conversation
- **Quick Suggestions**: One-click access to popular topics
- **Auto-scroll**: Chat automatically scrolls to latest messages

### 📚 Topics Covered

The chatbot can help with:

#### Getting Started
- `help` / `get started` - Complete onboarding guide
- Platform overview and first steps

#### User Profile
- `profile` - How to view and edit your profile
- `edit profile` - Profile customization options
- `profile picture` - Adding a profile photo
- `password` - Changing your password
- `forgot password` - Password recovery process

#### Teams
- `teams` / `join team` - How to join existing teams
- `create team` - How to create your own team
- `team captain` - Responsibilities of team leaders

#### Tournaments
- `tournaments` / `register tournament` - How to participate in tournaments
- `my tournament` - Checking your tournament schedules
- Tournament types and rules

#### Matches & Statistics
- `matches` - Viewing match history
- `statistics` - Understanding your stats
- `kda` - What KDA means and how to improve
- `ranking` - Ranking system explanation
- `leaderboard` - Checking global rankings

#### Communication
- `notifications` - Staying updated with platform news
- `messages` - Direct messaging with other players
- `dashboard` - Accessing your personal dashboard

#### Troubleshooting
- `problem` / `error` / `help me` - Getting help with issues

## How to Use

### Opening the Chatbot

1. Look for the floating **💬 button** in the bottom-right corner
2. Click the button to open the chatbot window
3. A chat interface will appear with suggestions

### Asking Questions

**Method 1: Type Your Question**
1. Click in the text input field
2. Type your question or topic (e.g., "How do I join a team?")
3. Press **Enter** or click **Send**
4. The chatbot will respond with helpful information

**Method 2: Use Quick Suggestions**
1. Click on any suggested topic button:
   - Getting Started
   - Join a Team
   - Find Tournaments
   - My Profile
   - Statistics
   - Leaderboard
2. The chatbot will provide detailed information

### Example Conversations

**Conversation 1: New Player Setup**
```
User: hello
Bot: 👋 Welcome to RankUp E-Sports! I'm your assistant here to help new players.
     I can help you with: How to get started, Understanding profiles, 
     Joining teams, Participating in tournaments, And much more!

User: get started
Bot: 🚀 **Getting Started Guide:**
     1. Complete Your Profile - Add a profile picture and bio
     2. Find Your Stats - Check your match history and rankings
     ... (detailed guide)

User: join team
Bot: 📝 To join a team: 
     1. Navigate to the Teams section
     2. Browse teams looking for members
     ... (step-by-step instructions)
```

**Conversation 2: Tournament Questions**
```
User: How do I register for tournaments?
Bot: ✅ **To Register for a Tournament:**
     1. Browse the Tournaments section
     2. Find a tournament that interests you
     3. Check the details (date, rules, entry fee)
     4. Click 'Register'
     5. Select your team (if applicable)
     6. Confirm registration
```

## Chatbot Window Controls

| Button | Function |
|--------|----------|
| **💬** | Open/Close chatbot |
| **−** | Minimize chatbot window |
| **✕** | Close chatbot |
| **Send** | Send your message |

## Features & Benefits

### For New Players
✅ **Instant Help** - Get answers without leaving the platform
✅ **Easy Navigation** - Quick links to platform sections
✅ **Confidence Building** - Learn platform features step-by-step
✅ **24/7 Availability** - Help anytime you need it

### For Returning Players
✅ **Quick Reference** - Fast reminders on how features work
✅ **Feature Discovery** - Learn about features you might have missed
✅ **Troubleshooting** - Solve common issues quickly

## Technical Details

### Architecture

```
ChatbotService (Backend)
    ├── Response Database (Map of 100+ Q&A pairs)
    ├── Keyword Matching Engine
    └── Suggestion Generator

ChatbotController (UI Logic)
    ├── Message Display
    ├── User Input Handling
    └── Suggestion Management

ChatbotFloatingButton (Integration)
    ├── Floating Button UI
    ├── Popup Management
    └── Scene Integration

chatbot.fxml (User Interface)
    ├── Chat Messages Display
    ├── Input Field
    ├── Send Button
    └── Quick Suggestions
```

### Files Created

```
src/main/java/edu/connexion3a36/services/
    └── ChatbotService.java

src/main/java/edu/connexion3a36/rankup/controllers/
    ├── ChatbotController.java
    └── ChatbotFloatingButton.java

src/main/resources/views/common/
    └── chatbot.fxml

src/main/resources/views/
    └── base.fxml (UPDATED)
```

### Key Features

1. **Keyword Matching Algorithm**
   - Exact match check
   - Partial keyword matching
   - Case-insensitive search

2. **Message Formatting**
   - User messages (blue, right-aligned)
   - Bot messages (gray, left-aligned)
   - Emoji support for visual appeal
   - Auto-text wrapping

3. **Responsive Design**
   - Mobile-friendly chat interface
   - Auto-scrolling to latest message
   - Collapsible suggestions
   - Resizable popup window

## Future Enhancements

Possible improvements for version 2.0:
- 🎯 Machine Learning for better understanding
- 🌍 Multi-language support
- 📊 Analytics dashboard for popular questions
- 🔗 Deep links to specific platform pages
- 👥 Human handoff to support team
- 💾 Chat history persistence
- 🎨 Customizable themes

## Troubleshooting

### Chatbot won't open
1. Check if the floating button is visible in bottom-right corner
2. Try refreshing the page
3. Clear browser cache and reload

### Messages not displaying
1. Check that JavaScript is enabled
2. Verify FXML files are in correct location
3. Check console for error messages

### Button not responding
1. Click directly on the emoji icon
2. Wait for the window to load
3. Try opening again after a few seconds

## Support

For issues or feature requests:
1. **Contact Support**: Click the help/support button in the app
2. **Report Issues**: Use the feedback section
3. **Documentation**: Check the FAQ page

---

**Version**: 1.0  
**Last Updated**: April 30, 2026  
**Status**: ✅ Active and Ready to Use

Enjoy your RankUp E-Sports experience! 🚀

