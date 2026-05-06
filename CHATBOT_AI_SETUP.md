# AI-Powered Ban Recommendation Chatbot - Setup Guide

## 🚀 What's New

Your punition/ban recommendation chatbot has been upgraded with the following improvements:

### ✅ Improvements Made

1. **Real AI Integration** - Now uses Mistral AI for intelligent conversations
2. **Fixed Text Colors** - Chat messages are now readable with proper contrast:
   - Bot messages: Light indigo (#e0e7ff)
   - User messages: Light gray (#f3f4f6)
   - Headers: Bright cyan/purple tones
3. **Conversation History** - The AI maintains context across messages, so it won't give the same answer repeatedly
4. **Better Response Quality** - Uses "mistral-large-latest" model for more detailed recommendations
5. **Enhanced System Prompt** - More detailed instructions for the AI to provide better guidance

---

## 📍 WHERE TO PASTE YOUR API KEY (EXACTLY!)

### **File Location:**
```
C:\Users\DIDA\Desktop\esportsnew\project_java\src\main\java\edu\connexion3a36\rankup\config\AIConfig.java
```

### **Line Number:**
**Line 20** - Look for this line:
```java
public static final String API_KEY = "PASTE_YOUR_API_KEY_HERE_WITHOUT_QUOTES";
```

### **How to Paste:**

1. **Open the file** `AIConfig.java` in your IDE
2. **Go to line 20** (Ctrl+G in most IDEs)
3. **Replace the placeholder** with your actual API key:
   ```java
   // BEFORE:
   public static final String API_KEY = "PASTE_YOUR_API_KEY_HERE_WITHOUT_QUOTES";
   
   // AFTER (with example key):
   public static final String API_KEY = "abc123def456ghi789jkl012mno345pqr";
   ```
4. **Save the file** (Ctrl+S)
5. **Restart the application**

---

## 🔑 Getting Your Mistral AI API Key

### Step-by-Step:

1. Go to **https://console.mistral.ai/api-keys**
2. **Sign up** or **log in** to your Mistral account
3. Click **"Create API key"** button
4. **Copy** the generated key (it's long, usually 30+ characters)
5. **Paste it** in `AIConfig.java` line 20 as shown above
6. **Save and restart** the application

---

## ⚙️ Configuration Details

### Current Settings (in AIConfig.java):

```java
// Model: mistral-large-latest (newest, best quality)
public static final String MODEL = "mistral-large-latest";

// Temperature: 0.7 (balanced - not too creative, not too strict)
public static final double TEMPERATURE = 0.7;

// Max tokens: 800 (longer, more detailed responses)
public static final int MAX_TOKENS = 800;
```

### Should You Change These?

**Model Options:**
- `"mistral-large-latest"` - Best quality, recommended ✅
- `"mistral-medium-latest"` - Balanced speed/quality
- `"mistral-small-latest"` - Faster but less detailed

**Temperature:**
- `0.0` - Precise, consistent (for exact recommendations)
- `0.7` - Balanced (current, recommended) ✅
- `1.0` - Creative, varied

---

## 🧪 Testing the Chatbot

### Before Pasting API Key:
The chatbot will use **rule-based recommendations** (fallback mode)
- Still works! But uses pre-defined punishment guidelines
- No AI conversation

### After Pasting API Key:
The chatbot will use **Mistral AI** for real conversations
- Type your message about any violation
- Get intelligent, context-aware recommendations
- Have a real discussion about suitable punishments

### Example Questions to Try:
```
"What punishment for cheating?"
"Which is worse - cussing or toxicity?"
"Is match fixing serious?"
"First offense vs repeat offender?"
"What about account sharing?"
"Can I ban someone temporarily?"
```

---

## 🎨 What Changed in the UI

### Text Colors (Fixed!)

| Element | Before | After |
|---------|--------|-------|
| Bot messages | White (hard to read) | Light indigo #e0e7ff ✅ |
| User messages | White (hard to read) | Light gray #f3f4f6 ✅ |
| Bot header | Cyan #4dd0ff | Bright cyan #64d8ff ✅ |
| User header | Purple #e0aaff | Purple #e0aaff (same) |

### Conversation Flow

**Before:** Same answer every time
**After:** Maintains conversation history for context-aware responses

---

## 🐛 Troubleshooting

### Problem: "API not configured"
**Solution:** Check that your API key is pasted correctly in AIConfig.java line 20

### Problem: "API Error: Invalid API key"
**Solution:** Your key might be:
- Incomplete (copy the entire key)
- Expired (get a new one from console.mistral.ai)
- Surrounded by extra spaces (remove them)

### Problem: "Chatbot still using rule-based responses"
**Solution:**
1. Make sure AIConfig.isConfigured() returns true
2. Your API key isn't the default placeholder
3. Check internet connection
4. Restart the application

### Problem: "Text is still white/unreadable"
**Solution:**
1. Files were edited, but cache might be old
2. Delete IDE cache (IntelliJ: File > Invalidate Caches)
3. Rebuild project
4. Restart IDE

---

## 📝 Files Modified

### Code Changes:
1. **ChatbotPaneController.java** - Added conversation history tracking
2. **AIApiClient.java** - Added context maintenance for conversations
3. **BanRecommendationChatbot.java** - Improved AI integration
4. **AIConfig.java** - Clear instructions and better configuration

### Style Changes:
1. **esports.css** - Fixed text colors for readability

### FXML Files:
- No changes needed (already set up correctly)

---

## 🔒 Security Notes

⚠️ **IMPORTANT:**
- Never commit `AIConfig.java` with your API key to GitHub
- Keep your API key private
- API key in AIConfig.java is only stored locally
- Each API call costs credits - monitor your usage at console.mistral.ai

---

## ✨ How to Use the Chatbot

1. Go to **Punitions page** (sidebar menu)
2. On the **right side**, you'll see the chatbot
3. Type your question about violations and punishments
4. The AI will respond with intelligent recommendations
5. Continue the conversation for more details

### Quick Actions (Buttons):
- **Cheating** - Instant question about cheating
- **Cussing** - Instant question about cussing  
- **Toxicity** - Instant question about toxicity
- **Harassment** - Instant question about harassment
- **Match Fixing** - Instant question about match fixing

---

## 📞 Support

If you have issues:
1. Check the debug console (View > Debug in IDE)
2. Look for "DEBUG: " messages
3. Make sure all files are saved
4. Rebuild the project (Ctrl+Shift+F9)
5. Restart the application

---

## 🎯 Expected Behavior

### With AI Configured:
```
You: What should I do about a player cheating?

Bot: 🤖 Cheating is a critical violation in eSports...
     Recommended: Game Ban (Permanent)
     Duration: Permanent
     Reasoning: Using unauthorized tools undermines competition...
     [continued conversation with context]
```

### Without AI (Rule-based):
```
You: What should I do about a player cheating?

Bot: 🎯 PUNISHMENT RECOMMENDATION
     Violation: Cheating
     Recommended: Game Ban (Permanent)
     [static recommendation from rules database]
```

---

## ✅ Checklist Before Using

- [ ] API key copied from console.mistral.ai
- [ ] Pasted into AIConfig.java line 20
- [ ] File saved (Ctrl+S)
- [ ] Application restarted
- [ ] Can see colorful, readable chat messages
- [ ] Typing in chatbot input field works
- [ ] Getting varied responses (not same answer every time)

---

**Last Updated:** April 30, 2026
**Chatbot Version:** 2.0 (AI-Powered)
**Mistral Model:** mistral-large-latest

