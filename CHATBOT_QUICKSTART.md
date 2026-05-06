# Ban Recommendation Chatbot - Quick Reference

## What's New?
A chatbot has been added to the **Punitions page at the right bottom** to help admins decide which bans are suitable for violations like cheating and cussing.

## How to Use

### Quick Actions (Fastest Way)
Click one of the quick action buttons for instant recommendations:
- **Cheating** → Permanent Game Ban
- **Cussing** → 1-7 days ban (progressive)
- **Toxicity** → 3-14 day Tournament Ban
- **Harassment** → 7-30 day Tournament Ban
- **Match Fixing** → Permanent Game Ban

### Text Input (Flexible)
Type any violation name in the input field:
```
Type: "cheating" → Get full recommendation with duration and notes
Type: "cuss" → Fuzzy match, understands variations
Type: "toxic behavior" → Works with descriptive phrases
```

### Get Help
Type "help" to see all 15+ supported violation types.

## Violation Types Covered

| Violation | Ban Type | Duration |
|-----------|----------|----------|
| Cheating | Game Ban | Permanent or 3-12 months |
| Aimbot | Game Ban | Permanent |
| Wallhack | Game Ban | Permanent |
| Cussing | Match/Tournament Ban | 1-7 days (progressive) |
| Toxicity | Tournament Ban | 3-14 days |
| Harassment | Tournament Ban | 7-30 days |
| Account Throwing | Match Ban | 1-3 days |
| Alt Account | Game Ban | 3-12 months |
| Scripting | Game Ban | Permanent |
| Account Sharing | Match Ban | 3-7 days |
| Abusive Behavior | Tournament Ban | 7-30 days |
| Match Fixing | Game Ban | Permanent |
| Spam | Match Ban | 6 hours - 1 day |
| Exploiting Bug | Match Ban | 1-7 days |
| Unsportsmanlike | Match Ban | 1-3 days |

## Key Features

✨ **Fuzzy Matching** - Understands partial names and variations
✨ **Context-Aware** - Provides notes and escalation guidance
✨ **Quick Actions** - Fast access to common violations
✨ **Help System** - Discover all available violation types
✨ **Professional Styling** - Matches app theme perfectly

## Example Conversation

```
👨‍💼 You: cheating

🤖 Bot: 🎯 BAN RECOMMENDATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Violation: Cheating
Recommended Ban: Game Ban (Permanent or Extended)
Suggested Duration: 3-12 months
Description: Using unauthorized tools, hacks, exploits, or unfair advantages. 
This is the most serious violation.

📝 ADMIN NOTES:
⚠️ Consider permanent ban for repeat offenders or severe cases. 
Evidence must be clear and documented.
```

## Location in App
The chatbot is positioned at the **right side of the Punitions page**, below the form and next to the punishments list.

## Remember
- Chatbot provides **recommendations only**
- You must manually apply bans in the form
- Use provided notes to guide your decision
- Consider context and previous offenses when deciding

## Files Added/Modified

**New Files:**
- `BanRecommendationChatbot.java` - Core logic
- `ChatbotPaneController.java` - UI controller
- `chatbot-pane.fxml` - Chatbot interface

**Modified Files:**
- `punitions.fxml` - Added chatbot panel
- `PunitionsController.java` - Integrated chatbot
- `esports.css` - Added styling

## Support
For issues or feature requests, check:
- `BAN_CHATBOT_IMPLEMENTATION.md` - Full documentation
- Project console for error messages
- ChatbotPaneController logs for debugging

