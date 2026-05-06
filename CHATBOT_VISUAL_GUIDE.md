# Ban Recommendation Chatbot - Visual Guide & Examples

## 🎨 UI Appearance

### Chatbot Panel Location
The chatbot appears on the **right side** of the Punitions page in a dedicated 350px-wide panel:

```
┌─────────────────────────────────────────────────────────────┐
│                      PUNITIONS PAGE                          │
├──────────────────────────────┬──────────────────────────────┤
│    PUNITIONS LIST            │  CHATBOT PANEL (Right)       │
│    (Scrollable)              │  ───────────────────────     │
│                              │                              │
│  📋 Punishment #123          │  🤖 Ban Chatbot              │
│  Player: User #45            │  Ask me about bans for       │
│  Type: Match Ban             │  violations...              │
│  Duration: 7 days            │                              │
│  [Edit] [Delete]             │  ┌─────────────────────────┐ │
│                              │  │ Chat Messages Here       │ │
│  📋 Punishment #124          │  │ (Auto-scrolling)         │ │
│  Player: User #78            │  │                          │ │
│  Type: Game Ban              │  │ 🤖 Welcome message       │ │
│  Duration: 30 days           │  │                          │ │
│  [Edit] [Delete]             │  │ 👨‍💼 User: cheating        │ │
│                              │  │ 🤖 Bot: Recommendation   │ │
│  📋 Punishment #125          │  └─────────────────────────┘ │
│  Player: User #90            │                              │
│  Type: Tournament Ban        │  ┌─────────────────────────┐ │
│  Duration: 14 days           │  │ [Input Violation Type]  │ │
│  [Edit] [Delete]             │  │ [Send] [Help]           │ │
│                              │  └─────────────────────────┘ │
│                              │                              │
│                              │  Quick Actions:              │
│                              │  [Cheating] [Cussing]        │
│                              │  [Toxicity] [Harassment]     │
│                              │  [Match Fixing]              │
└──────────────────────────────┴──────────────────────────────┘
```

---

## 💬 Message Display

### System Messages (Bot)
```
┌─────────────────────────────────────────┐
│ 🤖 Bot (in cyan/blue)                   │
│ ─────────────────────────────────────── │
│                                         │
│ 🎯 BAN RECOMMENDATION                   │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━          │
│ Violation: Cussing                      │
│ Recommended Ban: Match/Tournament Ban   │
│ Suggested Duration: 1-7 days            │
│ Description: Using profanity or slurs   │
│                                         │
│ 📝 ADMIN NOTES:                         │
│ 🟡 First offense: 1-7 day ban...        │
└─────────────────────────────────────────┘
```

### Admin Messages (You)
```
┌─────────────────────────────────────────┐
│ 👨‍💼 You (in purple)                      │
│ ─────────────────────────────────────── │
│                                         │
│ "What ban for match fixing?"            │
└─────────────────────────────────────────┘
```

---

## 🖱️ Interaction Patterns

### Pattern 1: Quick Button Click
```
User:   [Clicks "Cussing" button]
         ↓
Bot:    Returns recommendation instantly
         ↓
Result: Chat message appears with full details
```

### Pattern 2: Text Input
```
User:   Types "cheating" → Presses Enter/Send
         ↓
Bot:    Processes input
         ↓
        Returns matching recommendation
         ↓
Result: Message displayed in chat
```

### Pattern 3: Fuzzy Matching
```
User:   Types "toxic" (partial match)
         ↓
Bot:    Recognizes as "Toxicity"
         ↓
Result: Returns full Toxicity recommendation
```

### Pattern 4: Help Request
```
User:   Types "help"
         ↓
Bot:    Lists all 15 violation types
         ↓
Result: Shows numbered list with all options
```

---

## 📝 Example Conversations

### Example 1: Cheating Violation
```
👨‍💼 You: 
   cheating

🤖 Bot:
   🎯 BAN RECOMMENDATION
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Violation: Cheating
   Recommended Ban: Game Ban (Permanent or Extended)
   Suggested Duration: 3-12 months
   Description: Using unauthorized tools, hacks, exploits, 
                or unfair advantages. This is the most serious violation.
   
   📝 ADMIN NOTES:
   ⚠️ Consider permanent ban for repeat offenders or severe cases. 
      Evidence must be clear and documented.
```

### Example 2: Cussing Violation
```
👨‍💼 You:
   cussing

🤖 Bot:
   🎯 BAN RECOMMENDATION
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Violation: Cussing/Offensive Language
   Recommended Ban: Match/Tournament Ban (Progressive)
   Suggested Duration: 1-7 days (first), 7-30 days (repeat)
   Description: Using profanity, slurs, or offensive language 
                in chat or voice.
   
   📝 ADMIN NOTES:
   🟡 First offense: 1-7 day ban. Repeat offenders: escalate 
      to 7-30 days or longer.
```

### Example 3: Toxicity Violation
```
👨‍💼 You:
   toxicity

🤖 Bot:
   🎯 BAN RECOMMENDATION
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Violation: Toxic Behavior
   Recommended Ban: Match/Tournament Ban (Progressive)
   Suggested Duration: 3-14 days
   Description: Harassment, bullying, or toxic conduct 
                toward other players.
   
   📝 ADMIN NOTES:
   🟡 Escalate based on severity. Collect chat logs as evidence. 
      Consider team ban for team harassment.
```

### Example 4: Harassment Violation
```
👨‍💼 You:
   harassment

🤖 Bot:
   🎯 BAN RECOMMENDATION
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Violation: Player Harassment
   Recommended Ban: Tournament Ban (Medium-Long)
   Suggested Duration: 7-30 days
   Description: Targeting, threatening, or persistently 
                harassing another player.
   
   📝 ADMIN NOTES:
   🔴 Serious violation. Document all evidence. 
      Consider longer bans for repeat offenders.
```

### Example 5: Match Fixing Violation
```
👨‍💼 You:
   match fixing

🤖 Bot:
   🎯 BAN RECOMMENDATION
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Violation: Match Fixing / Collusion
   Recommended Ban: Game Ban (Permanent)
   Suggested Duration: Permanent
   Description: Intentional collaboration to fix match outcomes 
                for betting or advantage.
   
   📝 ADMIN NOTES:
   ⚠️ Most serious violation. Involves integrity. 
      Usually permanent with possible league-wide blacklist.
```

### Example 6: Help Request
```
👨‍💼 You:
   help

🤖 Bot:
   📋 AVAILABLE VIOLATION TYPES:
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   1. cheating
   2. aimbot
   3. wallhack
   4. cussing
   5. toxicity
   6. harassment
   7. throwing
   8. alt account
   9. scripting
   10. account sharing
   11. abusive behavior
   12. match fixing
   13. spam
   14. exploiting bug
   15. unsportsmanlike
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   Type any of these terms to get a ban recommendation.
```

---

## 🎯 Quick Action Buttons

### Available Quick Actions
```
┌──────────────────────────────────────────┐
│ Quick Actions:                           │
│ [Cheating] [Cussing] [Toxicity]          │
│ [Harassment] [Match Fixing]              │
└──────────────────────────────────────────┘
```

### Button Behavior
- **One-Click** - Click any button
- **Instant Response** - Bot immediately provides recommendation
- **No Typing Needed** - Perfect for quick lookups
- **Professional Styling** - Cyan text with outline design

---

## 🎨 Color Scheme

### Message Colors
```
🤖 Bot Messages:
   Background: Cyan tinted (rgba(56, 189, 248, 0.1))
   Border: Cyan (rgba(56, 189, 248, 0.3))
   Text: Light blue-white
   Header: #7dd3fc (bright cyan)

👨‍💼 Admin Messages:
   Background: Purple tinted (rgba(139, 92, 246, 0.15))
   Border: Purple (rgba(139, 92, 246, 0.4))
   Text: Light purple-white
   Header: #c4b5fd (bright purple)
```

### Button Colors
```
Primary Buttons:
   Text: Cyan (#38bdf8)
   Border: Cyan (outline)
   Background: Transparent
   Hover: Cyan background + stronger border

Secondary Buttons:
   Text: Gray
   Background: Gray with low opacity
   Hover: Gray with higher opacity
```

---

## 🔧 Customization Examples

### To Add a New Violation Type

1. Open `BanRecommendationChatbot.java`
2. Add to `VIOLATION_RECOMMENDATIONS` map:
```java
VIOLATION_RECOMMENDATIONS.put("new violation", new BanRecommendation(
    "New Violation",
    "Recommended Ban Type",
    "Duration",
    "Description of violation",
    "📝 Admin notes and guidance"
));
```
3. Recompile project
4. Chatbot automatically recognizes the new violation

### To Change Button Labels

1. Open `chatbot-pane.fxml`
2. Modify the Quick Actions buttons:
```xml
<Button text="New Violation Name" onAction="#onQuickViolation" styleClass="btn-small btn-outline" />
```
3. Make sure bot recognizes that term in the service

### To Change Styling

1. Open `esports.css`
2. Modify `.chatbot-*` classes
3. Example: Change message background color
```css
.chatbot-system-message {
    -fx-background-color: rgba(YOUR_COLOR_HERE, 0.1);
    /* ... other properties ... */
}
```

---

## 📊 Workflow Integration

### Typical Admin Workflow

1. **Admin opens Punitions page**
   - Sees punishment list on left
   - Sees chatbot on right

2. **Admin encounters a violation**
   - Needs to decide on appropriate ban
   
3. **Admin asks chatbot**
   - Either clicks quick button or types violation
   - Receives recommendation with guidance
   
4. **Admin reads guidance**
   - Gets ban type (Match/Tournament/Game)
   - Gets suggested duration
   - Gets admin notes for context

5. **Admin applies ban**
   - Uses form on left side
   - Enters punishment details
   - Follows chatbot guidance
   - Submits punishment

---

## ✅ Quality Indicators

- ✅ **Responsive**: Messages scroll automatically
- ✅ **Intuitive**: Quick buttons for fast access
- ✅ **Comprehensive**: 15+ violation types
- ✅ **Helpful**: Admin notes with each recommendation
- ✅ **Professional**: Matches app styling perfectly
- ✅ **Fast**: Instant recommendations (no delays)
- ✅ **Accessible**: Help system for discovery

---

## 🚀 Tips for Best Results

1. **Use quick buttons first** - Fastest way to get recommendations
2. **Type partial names** - "cuss", "toxic", "cheat" all work
3. **Read admin notes** - Contains escalation rules and context
4. **Consider severity** - Notes mention when to escalate
5. **Check previous violations** - Admin notes help with consistency
6. **Use help command** - See all available options anytime

---

**Designed for efficiency and ease of use!**

