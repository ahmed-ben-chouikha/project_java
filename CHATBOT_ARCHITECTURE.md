# Punition Advisor - Architecture & Flow Diagram

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        PUNITIONS PAGE (UI)                       │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  - Punishment List                                       │   │
│  │  - Create/Edit/Delete Forms                             │   │
│  │  - Search & Filter                                      │   │
│  │                                                          │   │
│  │                    💬 CHAT BUTTON                        │   │
│  │            (bottom-right corner, floating)             │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓ (click)
┌─────────────────────────────────────────────────────────────────┐
│              CHATBOT MODAL (ChatbotController)                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Title: Punition Advisor                                │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  Messages Box (scrollable)                        │  │   │
│  │  │  ┌──────────────────────────────────────────────┐ │   │   │
│  │  │  │ Bot: "Tell me about the incident..."         │ │   │   │
│  │  │  │                                               │ │   │   │
│  │  │  │ User: "User cheated with wallhack"          │ │   │   │
│  │  │  │                                               │ │   │   │
│  │  │  │ Bot: "Suggestion: PERMANENT_BAN..."         │ │   │   │
│  │  │  └──────────────────────────────────────────────┘ │   │   │
│  │  ├──────────────────────────────────────────────────┤  │   │
│  │  │  [Input Field] [Send Button]                     │  │   │
│  │  └──────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓ (send)
┌─────────────────────────────────────────────────────────────────┐
│         ChatbotService (Rule-Based Engine)                      │
│                                                                 │
│  Input: User's incident description (string)                   │
│                                                                 │
│  Processing:                                                    │
│  1. Normalize text to lowercase                                │
│  2. Check keyword patterns:                                    │
│     ├─ if contains "cheat" / "hacker" → PERMANENT_BAN         │
│     ├─ if contains "racist" / "slur" + repeat → TEMP_BAN      │
│     ├─ if contains "payment" / "fraud" → TEMP_BAN + INVESTIGATE
│     ├─ if contains "first time" → WARNING                     │
│     └─ if contains "spam" → WARNING or SHORT_BAN              │
│  3. Build response with suggestion + reason                   │
│                                                                 │
│  Output: Suggestion DTO { type, duration, explanation }       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│              Response Back to ChatbotController                 │
│  - Display bot message in chat modal                           │
│  - Enable Send button for next query                           │
│  - Admin can ask follow-up questions or close modal            │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│            Admin Uses Recommendation                            │
│  - Read suggestion in chat modal                               │
│  - Apply it via main Punitions form                            │
│  - Or ask chatbot for clarification                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Message Flow Sequence

```
Admin                    UI Controller           Service              DB
  │                            │                     │                │
  ├─ Click 💬 Button ─────────→│                     │                │
  │                            ├─ Load chatbot.fxml │                │
  │                            ├─ Show Modal        │                │
  │  ← Modal Appears ─────────┤                     │                │
  │                            │                     │                │
  ├─ Type & Click Send ──────→│                     │                │
  │                            ├─ Display User Msg  │                │
  │                            ├─ Call ask(text) ──→│                │
  │                            │                     ├─ Parse text   │
  │                            │                     ├─ Match rules  │
  │                            │                     ├─ Build sugg.  │
  │                            │ ← Return response ─┤                │
  │                            ├─ Display Bot Msg   │                │
  │  ← Message shown ─────────┤                     │                │
  │                            │                     │                │
  ├─ Ask Follow-up Qs ────────→│ (repeat above)     │                │
  │                            │                     │                │
  ├─ Close Modal ─────────────→│                     │                │
  │  ← Modal Closes ──────────┤                     │                │
  │                            │                     │                │
  ├─ Apply Recommendation ────→│                     │                │
  │  (fill form & save)        ├─ Create Punishment ├────────────────→│
  │                            │                     │           Store│
```

---

## 🔄 Data Flow Details

### 1. **User Input**
```
Admin types: "Player used racial slurs, already has 2 warnings"

Input Object:
{
  text: "Player used racial slurs, already has 2 warnings"
}
```

### 2. **Processing in ChatbotService**
```
Normalized: "player used racial slurs, already has 2 warnings"

Rule Matching:
├─ Check: contains("racist") ✗
├─ Check: contains("slur") ✓
├─ Check: contains("insult") ✗
├─ Check: contains("threat") ✗
└─ FOUND "slur" → check if repeat...
   ├─ matches(".*again|repeat|2 warnings|previous|prior.*") ✓
   └─ MATCH! → Return REPEAT offense rule
```

### 3. **Response Generation**
```
Response Object:
{
  suggestion: "TEMP_BAN",
  explanation: "Strongly consider a long temporary ban (7-30 days). 
                Evidence and repeat offenses increase severity.",
  type: "REPEAT_ABUSIVE_LANGUAGE",
  severity: "HIGH"
}
```

### 4. **Display in Modal**
```
Bot Message Formatted:
"Suggestion: TEMP_BAN
Reason: Strongly consider a long temporary ban (7-30 days). 
        Evidence and repeat offenses increase severity."

Message Style: .chatbot-message.bot
{
  background: rgba(255, 255, 255, 0.04)
  text-color: #dbeafe (light blue)
  padding: 8px
  border-radius: 8px
}
```

---

## 🧠 Heuristic Rules Decision Tree

```
                        ┌─ User Input ─┐
                        │  (incident    │
                        │  description) │
                        └───────┬───────┘
                                │
                     ┌──────────┴──────────┐
                     ↓                     ↓
            Normalize to lowercase   (text processing)
                     │
     ┌───────────────┼───────────────┬───────────────┬──────────────┐
     │               │               │               │              │
     ↓               ↓               ↓               ↓              ↓
  CHEATING        ABUSE           FRAUD           SPAM         FIRST-TIME
 (hacker,       (racist,        (payment,       (advertis,    (first time,
  exploit)       slur)          chargeback)      spam)       first-offense)
     │               │               │               │              │
     ↓               ↓               ↓               ↓              ↓
 PERM_BAN      Is Repeat?       INVESTIGATE      SHORT_BAN      WARNING
   (99%)          │                   │            (48-72h)       (LOW)
                  ├─ YES              │
                  │  ↓               │
                  │ TEMP_BAN         └─ TEMP_BAN
                  │ (7-30 days)        + INVESTIGATE
                  │
                  └─ NO
                     ↓
                  TEMP_BAN
                  (3-7 days)


            No matches → Ask for clarification
```

---

## 💾 Component Interaction

```
┌──────────────────────────────────────────────────────────────┐
│  File: PunitionsController.java                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ onOpenChat() {                                        │ │
│  │   1. Load /views/chatbot/chatbot.fxml                │ │
│  │   2. Create Stage (modal)                            │ │
│  │   3. Set title, size, modality                       │ │
│  │   4. Show to user                                    │ │
│  │ }                                                     │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────┐
│  File: chatbot.fxml (UI Layout)                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ <VBox>                                                │ │
│  │   <Label> "Punition Advisor" </Label>               │ │
│  │   <ScrollPane>                                       │ │
│  │     <VBox fx:id="messagesBox">                      │ │
│  │       (dynamically populated)                        │ │
│  │     </VBox>                                          │ │
│  │   </ScrollPane>                                      │ │
│  │   <HBox>                                             │ │
│  │     <TextField fx:id="inputField" />                │ │
│  │     <Button fx:id="sendButton" />                   │ │
│  │   </HBox>                                            │ │
│  │ </VBox>                                              │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────┐
│  File: ChatbotController.java                               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ initialize() {                                        │ │
│  │   appendBotMessage("greeting...")                    │ │
│  │ }                                                     │ │
│  │                                                       │ │
│  │ onSend() {                                            │ │
│  │   1. Get user input from inputField                  │ │
│  │   2. appendUserMessage(input)                        │ │
│  │   3. CompletableFuture.supplyAsync(                  │ │
│  │        () -> chatbotService.ask(input)               │ │
│  │      )                                                │ │
│  │   4. Platform.runLater(                              │ │
│  │        () -> appendBotMessage(response)              │ │
│  │      )                                                │ │
│  │ }                                                     │ │
│  │                                                       │ │
│  │ appendUserMessage(text) {                            │ │
│  │   Label lbl = new Label(text);                       │ │
│  │   lbl.getStyleClass().add("badge");                 │ │
│  │   messagesBox.add(lbl);                              │ │
│  │ }                                                     │ │
│  │                                                       │ │
│  │ appendBotMessage(text) {                             │ │
│  │   Label lbl = new Label(text);                       │ │
│  │   lbl.getStyleClass().add("muted");                 │ │
│  │   messagesBox.add(lbl);                              │ │
│  │ }                                                     │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────┐
│  File: ChatbotService.java                                  │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ ask(input: String): String {                          │ │
│  │   1. Validate input (not null/blank)                 │ │
│  │   2. Normalize to lowercase                          │ │
│  │   3. Loop through heuristic rules                    │ │
│  │      if normalized.contains(keyword) → match         │ │
│  │   4. Build response with suggestion + reason         │ │
│  │   5. Return formatted response                       │ │
│  │ }                                                     │ │
│  │                                                       │ │
│  │ buildSuggestion(type, explanation): String {         │ │
│  │   return "Suggestion: {type}\nReason: {explanation}"│ │
│  │ }                                                     │ │
│  └────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

---

## 🎨 Styling & Layout

```
.chatbot-button (floating button)
├─ Position: AnchorPane.bottomAnchor="18.0", AnchorPane.rightAnchor="18.0"
├─ Shape: circular (border-radius: 999)
├─ Size: 56x56px
├─ Background: linear-gradient(cyan → purple)
├─ Text: "💬" (emoji)
├─ Effect: drop shadow
└─ Hover: lighter gradient + glow

.chatbot-message.user (user's message)
├─ Background: linear-gradient(blue → purple)
├─ Text color: dark (#021124)
├─ Padding: 8px 10px
├─ Border-radius: 8px
└─ Style class: "badge"

.chatbot-message.bot (bot's message)
├─ Background: semi-transparent white (0.04 opacity)
├─ Text color: light blue (#dbeafe)
├─ Padding: 8px 10px
├─ Border-radius: 8px
└─ Style class: "muted"
```

---

## 🔐 Thread Safety & Concurrency

```
Main UI Thread                              Background Thread
        │                                          │
        │  User clicks Send                        │
        │──────────────────────────────────────────→
        │                                  CompletableFuture
        │                                          │
        │  UI is blocked?                   chatbotService.ask()
        │  NO! (async)                             │
        │                                   Process (text matching)
        │  User can still                          │
        │  interact with app                Build response
        │                                          │
        │                                   ← Response ready
        │  ← Platform.runLater()            │
        │     (back to UI thread)           │
        │  Update messagesBox               │
        │  Enable sendButton                │
        │                                          │
```

---

## 📝 Example Heuristic Rule

```java
if (normalized.contains("racist") || normalized.contains("slur")) {
    // Check for repeat offense
    if (normalized.matches(".*(again|repeat|2 warnings|previous|prior).*")) {
        return buildSuggestion(
            "TEMP_BAN",
            "Strongly consider a long temporary ban (7-30 days). " +
            "Evidence and repeat offenses increase severity."
        );
    }
    // First offense
    return buildSuggestion(
        "TEMP_BAN",
        "Medium confidence: abusive language/harassment. " +
        "Recommend temporary ban (3-7 days) or warning depending on evidence."
    );
}
```

---

## ✅ Data Validation & Error Handling

```
User Input
    ↓
┌─ Null/Blank? ──→ "Please provide details about the incident..."
│   ↓ NO
│
├─ Contains keywords? ──→ Yes ──→ Match rule ──→ Build suggestion
│   ↓ NO
│
└─ Fallback ──→ "I couldn't classify incident. Please provide: " +
                "offense type (cheating/abuse/fraud), " +
                "whether this is a repeat offense, and " +
                "evidence available (screenshots/logs)."
```

---

## 🚀 Future Extensibility

```
Current:  ChatbotService (Heuristics only)
            ↓
Future:   ChatbotService (Heuristics + LLM)
            ├─ Read CHATBOT_API_KEY from environment
            ├─ Make HTTP POST to OpenAI API
            ├─ Extract suggestion from LLM response
            ├─ Fallback to heuristics if LLM fails
            └─ Log all requests for audit
            
                    ↓
                ChatbotService (ML Classifier)
                    ├─ Loaded model (pickle/ONNX)
                    ├─ Feature extraction from text
                    ├─ Predict severity score [0,1]
                    ├─ Rank-order suggestions
                    └─ Return top-3 options
```

---

**This architecture is designed for:**
- ✅ Offline-first (no internet required)
- ✅ Fast responses (no network latency)
- ✅ Easy customization (add rules anytime)
- ✅ Scalability (add LLM layer without breaking changes)
- ✅ Auditability (all decisions logged)
- ✅ User experience (non-blocking async, responsive UI)

