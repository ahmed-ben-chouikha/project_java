# ✅ Punition Chatbot - Setup & Verification Checklist

## Phase 1: Pre-Setup (Before Running)

- [ ] **Read the docs** (in order):
  - [ ] CHATBOT_QUICK_START.md (2 min overview)
  - [ ] PUNITION_CHATBOT_GUIDE.md (detailed guide)
  - [ ] CHATBOT_ARCHITECTURE.md (technical deep-dive) — *optional*

- [ ] **Verify Java is installed**:
  ```powershell
  java -version
  javac -version
  ```
  Expected: Java 17+

- [ ] **Verify project structure**:
  ```powershell
  cd C:\Users\DIDA\Desktop\esportsnew\project_java
  dir src\main\java\edu\connexion3a36\rankup\controllers\chatbot\ChatbotController.java
  dir src\main\resources\views\chatbot\chatbot.fxml
  ```

---

## Phase 2: Build & Compile

- [ ] **Clean build** (in your IDE):
  - IntelliJ: Build → Clean Project
  - Then: Build → Compile Module

- [ ] **Check for compile errors**:
  ```
  Expected: 0 errors, some warnings (OK)
  Warnings about unused parameters are safe to ignore
  ```

- [ ] **Verify resources are included**:
  - [ ] `/styles.css` updated (contains `.chatbot-button` style)
  - [ ] `/views/chatbot/chatbot.fxml` exists and valid XML

---

## Phase 3: Runtime Setup (Before Running App)

### API Key Setup (OPTIONAL - only if using external LLM)

- [ ] **Current mode** (default): No API key needed ✓

- [ ] **If planning to add OpenAI/other LLM** (future):
  - [ ] Get API key from provider (e.g., https://platform.openai.com/api-keys)
  - [ ] Choose one option below:
    - [ ] **Option A (Environment Variable)**:
      ```powershell
      $env:CHATBOT_API_KEY = "sk-proj-your-actual-key-here"
      ```
      Then launch app from same PowerShell window
    
    - [ ] **Option B (System Variable)**:
      Win+X → System → Advanced system settings → Environment Variables
      New Variable: `CHATBOT_API_KEY` = `sk-proj-...`
      Restart IDE after adding
    
    - [ ] **Option C (Config File)** — development only:
      Create `src/main/resources/chatbot.properties`:
      ```properties
      chatbot.api.key=sk-proj-your-key
      ```
      Add to `.gitignore` (never commit)

---

## Phase 4: Run the Application

- [ ] **Start the application**:
  - IDE: Run → Run 'Main' (or use green play button)
  - Or: `java -jar app.jar` (if compiled)

- [ ] **Wait for startup**:
  - Login screen appears
  - Login with your credentials
  - Dashboard loads

- [ ] **Navigate to Punitions page**:
  - Click "Punitions" in sidebar
  - Page loads with punishment list and filter form

---

## Phase 5: Functional Testing

### Test 1: Chat Button Visibility
- [ ] **Button appears**:
  - Look at **bottom-right corner** of Punitions page
  - See 💬 emoji button (floating, styled)
  - Button is not covered by other elements

### Test 2: Open Chatbot
- [ ] **Click the 💬 button**:
  - Modal window opens (420x520 px)
  - Title says "Punition Advisor"
  - Modal is centered on screen
  - Greeting message displays: "Hello — I am the Punition Advisor..."

### Test 3: Send Message (Cheating Detection)
- [ ] **Type**: "User cheated with wallhack"
- [ ] **Click Send**:
  - Message appears in blue badge (user style)
  - Send button temporarily disables
  - Bot message appears in muted text with suggestion

- [ ] **Expected response contains**:
  ```
  Suggestion: PERMANENT_BAN
  Reason: High confidence: cheating/hacking detected...
  ```

### Test 4: Send Message (Abusive - Repeat)
- [ ] **Type**: "Player used racist slurs, already has 2 warnings"
- [ ] **Expected response contains**:
  ```
  Suggestion: TEMP_BAN
  Reason: Strongly consider a long temporary ban (7-30 days)...
  ```

### Test 5: Send Message (Abusive - First Time)
- [ ] **Type**: "User said 'idiot' once in chat"
- [ ] **Expected response contains**:
  ```
  Suggestion: TEMP_BAN
  Reason: Medium confidence: abusive language/harassment. 
          Recommend temporary ban (3-7 days)...
  ```

### Test 6: Send Message (Fraud)
- [ ] **Type**: "Payment fraud - user charged back tournament fee"
- [ ] **Expected response contains**:
  ```
  Suggestion: TEMP_BAN_INVESTIGATE
  Reason: Potential fraud/payment issue...
  ```

### Test 7: Send Message (Spam)
- [ ] **Type**: "User spamming ads for external site"
- [ ] **Expected response contains**:
  ```
  Suggestion: WARNING_OR_SHORT_BAN
  Reason: Spam/advertising: recommend warning or short suspension...
  ```

### Test 8: Send Message (First-Time Offense)
- [ ] **Type**: "First time posting ads"
- [ ] **Expected response contains**:
  ```
  Suggestion: WARNING
  Reason: Low severity: first offense...
  ```

### Test 9: Send Message (Unclassified)
- [ ] **Type**: "Random text that doesn't match any rule"
- [ ] **Expected response contains**:
  ```
  I couldn't classify the incident confidently. Please provide:
  offense type (cheating/abuse/fraud), whether this is a repeat offense...
  ```

### Test 10: Multiple Queries
- [ ] **Send 3+ different queries** in same session:
  - All messages persist in chat history
  - No errors occur
  - Send button re-enables after each response

### Test 11: Close & Reopen
- [ ] **Close modal**:
  - Click X button or outside modal
  - Modal closes smoothly
  - 💬 button still visible

- [ ] **Click 💬 button again**:
  - New modal opens
  - Chat history is fresh (new conversation)
  - Greeting message displays again

### Test 12: UI Styling
- [ ] **User messages**:
  - Blue gradient background
  - Dark text
  - Padded, rounded corners

- [ ] **Bot messages**:
  - Light background (semi-transparent)
  - Light blue text
  - Padded, rounded corners

- [ ] **Chat button**:
  - Circular (emoji centered)
  - Cyan→purple gradient
  - Drop shadow visible
  - Stays in bottom-right even after scrolling

---

## Phase 6: Integration Testing

- [ ] **After getting suggestion, can I create punishment?**:
  - [ ] Close chatbot modal
  - [ ] Use main Punitions form (fill date, status, reclamation)
  - [ ] Click "➤" to create punishment
  - [ ] Punishment appears in list ✓

- [ ] **Can I navigate to linked reclamation?**:
  - [ ] Click "Reclamation" button on punishment card
  - [ ] Reclamations page loads with focus on linked reclamation ✓

- [ ] **Can I navigate to admin response?**:
  - [ ] Click "Response" button on punishment card
  - [ ] Admin responses page loads ✓

---

## Phase 7: Error Handling Tests

- [ ] **Test empty input**:
  - [ ] Click Send without typing anything
  - No error; button remains enabled

- [ ] **Test rapid clicks**:
  - [ ] Type message and click Send multiple times
  - Button disables (only one request processes)
  - No duplicate messages

- [ ] **Test long input**:
  - [ ] Paste very long text (1000+ chars)
  - Message displays correctly, bot responds
  - No crashes or UI issues

- [ ] **Test special characters**:
  - [ ] Type: "User said '@#$%^&*()'"
  - Bot responds normally (special chars don't crash system)

---

## Phase 8: Performance Testing

- [ ] **Response time**:
  - [ ] Send message → should get response < 500ms
  - (All processing is local, no network latency)

- [ ] **Memory stability**:
  - [ ] Send 20+ messages in one session
  - App doesn't slow down or crash
  - All messages remain visible

- [ ] **UI responsiveness**:
  - [ ] While bot is "thinking" (processing), can I scroll the chat?
  - [ ] Can I close the modal?
  - (Should be responsive even during async call)

---

## Phase 9: Documentation & Knowledge

- [ ] **Read full guide**:
  - PUNITION_CHATBOT_GUIDE.md (all sections)
  
- [ ] **Understand customization**:
  - [ ] How to add new heuristic rules (editing ChatbotService.java)
  - [ ] Where to put API key if adding LLM (see Phase 3)
  - [ ] How to change button style (editing styles.css)

- [ ] **Know the architecture**:
  - [ ] How messages flow (ChatbotController → Service → Response)
  - [ ] Why it's async (user experience)
  - [ ] How to extend it later (add LLM, add DB)

---

## Phase 10: Final Verification

- [ ] **All files exist**:
  ```powershell
  ls C:\Users\DIDA\Desktop\esportsnew\project_java\src\main\java\edu\connexion3a36\rankup\controllers\chatbot\ChatbotController.java
  ls C:\Users\DIDA\Desktop\esportsnew\project_java\src\main\java\edu\connexion3a36\rankup\services\ChatbotService.java
  ls C:\Users\DIDA\Desktop\esportsnew\project_java\src\main\resources\views\chatbot\chatbot.fxml
  ```

- [ ] **All docs exist**:
  - CHATBOT_QUICK_START.md
  - PUNITION_CHATBOT_GUIDE.md
  - CHATBOT_ARCHITECTURE.md
  - README_CHATBOT.md

- [ ] **No compilation errors**:
  - Compile and check IDE error panel
  - Warnings are OK (unused parameters, etc.)
  - No error highlighting in editors

---

## Troubleshooting Quick Guide

| Issue | Solution |
|-------|----------|
| 💬 button not visible | Rebuild project; check punitions.fxml modified correctly |
| Modal won't open | Check chatbot.fxml path is `/views/chatbot/chatbot.fxml` |
| Bot doesn't respond | Check ChatbotService.java file exists and is valid |
| Messages not displaying | Verify ChatbotController.appendBotMessage() is correct |
| Null pointer error | Check styles.css URL is handled with null check (fixed) |
| Chat button styled oddly | Rebuild to pick up latest styles.css changes |
| App crashes on chatbot send | Check Java stack trace; likely FXML resource loading issue |

---

## Success Criteria ✅

You're done when:
- [x] 💬 button appears at bottom-right of Punitions page
- [x] Click button → modal opens with greeting
- [x] Type "cheating" → bot suggests PERMANENT_BAN
- [x] Type "first ad" → bot suggests WARNING
- [x] Chat history persists during session
- [x] No errors in console
- [x] Can create punishment using chatbot suggestion
- [x] Modal closes cleanly
- [x] All 3 docs read and understood
- [x] Ready to customize or add LLM integration (optional)

---

## Next Steps (After Verification)

### Short Term (Do Now):
1. Run through all tests above ✓
2. Show 💬 button to team
3. Start using for actual punishments

### Medium Term (This Week):
1. Gather feedback from admins using chatbot
2. Identify missing rules (e.g., "griefing", "account selling")
3. Add those rules to ChatbotService

### Long Term (This Month):
1. Log all chatbot interactions to database
2. Add ML model or LLM integration for better suggestions
3. Create analytics dashboard showing acceptance rates
4. Train model on actual admin decisions

---

**Questions?** Check PUNITION_CHATBOT_GUIDE.md or ask for "implement LLM" to add external AI.

**Ready to test?** Follow Phase 4 above and click that 💬 button! 🎮⚖️

