# Punition Advisor Chatbot - Complete Setup & Usage Guide

## Overview
A floating **Punition Advisor** chatbot has been added to the Punitions page. Admins can click the 💬 button at the **bottom-right** corner of the punition page to open a chat modal and ask for punishment recommendations based on incident descriptions.

## What Was Added

### 1. **UI Components**
- **Floating Chat Button** (💬) — anchored at bottom-right of the punitions page
- **Chat Modal Window** — 420x520 popup with message history and input field
- **Styled Messages** — user messages (blue badge), bot messages (muted text)

### 2. **Backend Services**
- **ChatbotService** — rule-based engine providing punishment suggestions
  - Detects offense types: cheating, abuse/harassment, fraud, spam
  - Considers repeat offenses
  - Provides recommendation + reasoning
  - Works **offline by default** (no API key required)

- **ChatbotController** — JavaFX UI handler
  - Sends user input to ChatbotService asynchronously
  - Displays bot responses in real-time
  - Handles errors gracefully

### 3. **Files Created/Modified**
```
src/main/java/edu/connexion3a36/rankup/
  ├── controllers/
  │   ├── punitions/
  │   │   └── PunitionsController.java [MODIFIED]
  │   └── chatbot/
  │       └── ChatbotController.java [NEW]
  └── services/
      └── ChatbotService.java [NEW]

src/main/resources/
  ├── views/
  │   ├── punitions/
  │   │   └── punitions.fxml [MODIFIED]
  │   └── chatbot/
  │       └── chatbot.fxml [NEW]
  └── styles.css [MODIFIED - added .chatbot-button styles]

README_CHATBOT.md [NEW]
```

---

## How to Use

### **For End Users (Admins)**

1. **Open Punitions Page**
   - Navigate to the Punitions section in the admin dashboard

2. **Click Chat Button**
   - Look for the 💬 button at the **bottom-right corner**
   - Click to open the "Punition Advisor" modal

3. **Describe the Incident**
   - Type a description of the incident, e.g.:
     - "User posted racist slurs in match chat, has 2 prior warnings"
     - "Player suspected of cheating with unnatural aim patterns"
     - "User charged back a tournament fee"
     - "First-time spam in general chat"

4. **Get Recommendation**
   - Bot responds with:
     - **Suggestion**: punishment type (WARNING, TEMP_BAN, PERM_BAN, etc.)
     - **Reason**: explanation based on the incident description
   - Admin can ask follow-up questions or close the modal

5. **Apply Punishment**
   - Use the main form on the Punitions page to create the punishment
   - Or follow the recommendation given by the chatbot

### **Example Conversations**

**Conversation 1: Repeat Abuser**
```
Admin:  "Player used racial slurs again. Has 2 warnings already."
Bot:    "Suggestion: TEMP_BAN
         Reason: Strongly consider a long temporary ban (7-30 days). 
         Evidence and repeat offenses increase severity."
```

**Conversation 2: Suspected Cheater**
```
Admin:  "Suspicious win rate 85% in last 10 matches, enemy reports hacking"
Bot:    "Suggestion: PERMANENT_BAN
         Reason: High confidence: cheating/hacking detected based on 
         description. Recommend permanent ban and investigation."
```

**Conversation 3: First Offense**
```
Admin:  "First time posting ads in chat"
Bot:    "Suggestion: WARNING
         Reason: Low severity: first offense. Recommend warning with 
         monitoring and require apology or education."
```

---

## API Key Setup (Optional - for Enhanced LLM Support)

### Current Mode (Default)
- **Works offline** — rule-based heuristics only
- **No API key needed**
- **Suitable for**: standard punishments (cheating, abuse, fraud, spam detection)

### To Enable OpenAI/External LLM (Future)
1. Obtain an API key (e.g., OpenAI at https://platform.openai.com/api-keys)
2. Store it securely — **never commit to Git**

#### **Option A: Environment Variable (Recommended)**

**Windows PowerShell (Temporary):**
```powershell
$env:CHATBOT_API_KEY = "sk-proj-...your-actual-key..."
# Then launch the app
```

**Windows (Permanent via System Settings):**
1. Press `Win + X` → "System"
2. "Advanced system settings" → "Environment Variables"
3. New system variable:
   - Variable name: `CHATBOT_API_KEY`
   - Variable value: `sk-proj-...your-key...`
4. Restart IDE and re-launch app

#### **Option B: JVM System Property**
Launch the app with:
```bash
java -Dchatbot.api.key="sk-proj-...your-key..." -jar app.jar
```

#### **Option C: Config File (Local Development)**
1. Create `src/main/resources/chatbot.properties`:
   ```properties
   chatbot.api.key=sk-proj-...your-key...
   ```
2. **Do NOT commit this file** — add to `.gitignore`:
   ```
   chatbot.properties
   ```
3. Update `ChatbotService.java` to read from properties file

---

## Technical Details

### **ChatbotService Rule Engine**

The chatbot uses keyword-based matching to classify incidents:

| Keyword Patterns | Suggested Action | Severity |
|---|---|---|
| `cheat`, `hacker`, `exploit` | PERMANENT_BAN | Critical |
| `racist`, `slur`, `insult`, `threat` + repeat | TEMP_BAN (7-30 days) | High |
| `racist`, `slur`, `insult`, `threat` (first) | TEMP_BAN (3-7 days) | Medium |
| `payment`, `fraud`, `chargeback`, `scam` | TEMP_BAN + INVESTIGATE | High |
| `spam`, `advertis` | WARNING or SHORT_BAN (24-72h) | Low-Medium |
| `first time`, `first-offense` | WARNING | Low |

### **Asynchronous Processing**
- User input → ChatbotController sends to ChatbotService in background thread
- Service processes → response sent back to UI thread
- UI updates without blocking (smooth user experience)

### **Error Handling**
- Null/blank input → helpful prompt
- Unclassifiable input → asks for more details
- Service exceptions → error message displayed in chat

---

## Compilation & Build

### **Prerequisites**
- Java 17+ JDK installed
- Maven 3.8.1+ (optional, build tools can work without)
- IDE: IntelliJ IDEA, Eclipse, or similar

### **Compile from Command Line**
```bash
cd C:\Users\DIDA\Desktop\esportsnew\project_java
javac -encoding UTF-8 -d target/classes -cp "target/classes:lib/*" ^
  src/main/java/edu/connexion3a36/rankup/controllers/chatbot/ChatbotController.java ^
  src/main/java/edu/connexion3a36/rankup/services/ChatbotService.java
```

### **From IDE (Recommended)**
1. Open project in IntelliJ IDEA
2. Build → Compile Module
3. Or use Maven: `mvn clean compile`

---

## Customization

### **Add New Heuristic Rules**
Edit `ChatbotService.java`:

```java
// Add before the final fallback
if (normalized.contains("your-keyword")) {
    return buildSuggestion("SUGGESTION_TYPE", "Your explanation here");
}
```

Example: Detecting account selling
```java
if (normalized.contains("account sell") || normalized.contains("sell account")) {
    return buildSuggestion("PERM_BAN", "Account trading violates ToS. Recommend permanent ban.");
}
```

### **Change Chatbot Button Style**
Edit `styles.css`, `.chatbot-button` class:

```css
.chatbot-button {
    -fx-font-size: 24px;  /* larger emoji */
    -fx-min-width: 64;    /* bigger button */
    -fx-min-height: 64;
    -fx-background-color: linear-gradient(to right, #ff6b6b, #ee5a6f);  /* red theme */
}
```

### **Integrate with External LLM**
1. Add HTTP client dependency (OkHttp or HttpClient)
2. Modify `ChatbotService.ask()` to call OpenAI/Anthropic API
3. Read API key from environment variable (see above)
4. Return LLM's response instead of rules

---

## Testing

### **Manual Test Flow**
1. Launch app → navigate to Punitions page
2. Click 💬 button → modal opens with greeting
3. Type: "User cheated with wallhack" → bot suggests PERMANENT_BAN
4. Type: "First time posting ads" → bot suggests WARNING
5. Type: "Random text" → bot asks for clarification
6. Close modal → button remains accessible

### **Unit Tests (Optional)**
Add test for `ChatbotService.ask()`:
```java
@Test
void testCheatingDetection() {
    String response = service.ask("User used wallhack in match");
    assertTrue(response.contains("PERMANENT_BAN"));
}

@Test
void testRepeatOffense() {
    String response = service.ask("Player used racist slurs again, 2 prior warnings");
    assertTrue(response.contains("TEMP_BAN"));
}
```

---

## Troubleshooting

| Issue | Solution |
|---|---|
| Chat button not visible | Ensure `punitions.fxml` was updated correctly; rebuild project |
| Modal won't open | Check `chatbot.fxml` path is `/views/chatbot/chatbot.fxml` |
| Messages not displaying | Verify `ChatbotController` is in correct package path |
| Service returns null | Check `ChatbotService.ask()` is not null-checked incorrectly |
| Null pointer on stylesheet | CSS file URL should be checked before use (already fixed) |

---

## Security Notes

⚠️ **If using external LLM:**
- Strip user ID/player nicknames from prompts before sending to API
- Store API keys in environment variables, NOT in code
- Log API responses for audit, but avoid logging sensitive evidence
- Set request timeout (e.g., 10 seconds) to prevent hangs
- Use HTTPS only for API calls

---

## Next Steps (Future Enhancements)

1. **Connect to Database** — fetch user's punishment history and auto-include in context
2. **LLM Integration** — replace rules with fine-tuned OpenAI model for nuanced recommendations
3. **Persistence** — save chat logs for auditing admin decisions
4. **Machine Learning** — track which recommendations admins accept/reject to improve suggestions
5. **Multi-language** — translate bot responses based on admin locale
6. **Webhook Notifications** — alert moderators when high-severity suggestions are generated

---

## Questions?

If you want to:
- **Enable LLM** → reply "implement LLM" with provider choice (OpenAI/Anthropic/Local)
- **Add custom rules** → describe the incident type and I'll add the detection logic
- **Change UI** → describe desired layout and I'll update FXML + CSS
- **Add database** → I can link to user history and auto-populate context

**Enjoy the Punition Advisor!** 🎮⚖️

