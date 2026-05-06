# 🔑 API KEY SETUP - Visual Guide

## The EXACT Steps to Enable AI Chatbot

---

## Step 1: Get Your API Key from Mistral

### Go Here:
```
https://console.mistral.ai/api-keys
```

### Screenshot Guide:
```
┌─────────────────────────────────────────────┐
│ Mistral Console - API Keys                  │
├─────────────────────────────────────────────┤
│                                             │
│  [+ Create API key]  [Copy]  [Revoke]      │
│                                             │
│  My API Keys:                               │
│  • abc123def456ghi789jkl012mno345pqr       │
│  • xyz789uvw456rst123opq890mno123pqr       │
│                                             │
└─────────────────────────────────────────────┘
```

### What to Do:
1. Click **"Create API key"** (or use existing one)
2. A new key appears
3. Click **"Copy"** button
4. Key is now in your clipboard

---

## Step 2: Open AIConfig.java

### Location:
```
C:\Users\DIDA\Desktop\esportsnew\project_java\
  └─ src\
     └─ main\
        └─ java\
           └─ edu\
              └─ connexion3a36\
                 └─ rankup\
                    └─ config\
                       └─ AIConfig.java
```

### In Your IDE (JetBrains):
```
1. Press: Ctrl+O (Open file)
2. Type: AIConfig.java
3. Press: Enter
```

---

## Step 3: Go to Line 20

### Using Keyboard Shortcut:
```
1. Open file (see above)
2. Press: Ctrl+G (Go to Line)
3. Type: 20
4. Press: Enter
```

### You'll See This:
```java
Line 18: public class AIConfig {
Line 19:    
Line 20: public static final String API_KEY = "PASTE_YOUR_API_KEY_HERE_WITHOUT_QUOTES";
         ↑
         THIS LINE - Replace the text in quotes
```

---

## Step 4: Replace the Placeholder

### Before (❌ Wrong):
```java
public static final String API_KEY = "PASTE_YOUR_API_KEY_HERE_WITHOUT_QUOTES";
```

### After (✅ Correct):
```java
public static final String API_KEY = "abc123def456ghi789jkl012mno345pqr";
```

### HOW TO REPLACE:

**Option A: Manual**
```
1. Click after first quote mark "
2. Select all text: Ctrl+A (or drag)
3. Delete
4. Paste your key: Ctrl+V
5. Make sure it looks like: "your-key-here"
```

**Option B: Quick Replace**
```
1. Double-click on PASTE_YOUR_API_KEY_HERE_WITHOUT_QUOTES
2. Press: Ctrl+H (Find & Replace)
3. In "Find": paste the placeholder
4. In "Replace": paste your key
5. Click "Replace All"
```

---

## Step 5: Save the File

### Keyboard Shortcut:
```
Ctrl + S
```

### Visual Confirmation:
```
Before saving: AIConfig.java (with white dot or *)
After saving:  AIConfig.java (no dot or *)
```

---

## Step 6: Restart the Application

### Complete Restart:
```
1. Close the application completely
   (Not just minimize, actually CLOSE it)
2. Wait 2 seconds
3. Open the application again
4. Wait for it to fully load
```

---

## Step 7: Go to Punitions Page

### In the App:
```
Sidebar (Left) → Punitions
                   ↓
              [Opens Punitions Page]
                   ↓
              [Chatbot appears on RIGHT side]
```

### Visual Layout:
```
┌────────────────────────────────────────────┐
│ Punitions Page                             │
├──────────────────────────┬─────────────────┤
│                          │                 │
│  Punition List           │  CHATBOT HERE   │
│  (Left side)             │  (Right side)   │
│                          │                 │
│  • Item 1                │ 🤖 Assistant    │
│  • Item 2                │ Hello! I'm your │
│  • Item 3                │ punishment...   │
│                          │                 │
│                          │ [Input field]   │
│                          │ [Send] [Help]   │
└──────────────────────────┴─────────────────┘
```

---

## Step 8: Test the Chatbot

### Try Typing:
```
You: "What about cheating?"
Bot: "Cheating is a critical violation..."
     [Detailed response from AI]

You: "First offense?"
Bot: "For first offense, consider..."
     [Context-aware response]
```

### Signs It's Working ✅
- Text is readable (not white)
- Bot responds to your messages
- Responses are different each time
- No error messages

### If It's Not Working ❌
- Check: API key correct?
- Check: File saved?
- Check: App restarted?
- See troubleshooting below

---

## 🎯 Quick Verification Checklist

Use this checklist to verify each step:

```
□ Step 1: Got API key from https://console.mistral.ai/api-keys
          Key copied to clipboard

□ Step 2: Opened AIConfig.java
          File is visible in IDE

□ Step 3: Navigated to Line 20
          Can see: public static final String API_KEY = "...";

□ Step 4: Replaced placeholder with actual key
          Line looks like: public static final String API_KEY = "abc123def...";

□ Step 5: Saved the file
          No unsaved indicator (white dot/asterisk)

□ Step 6: Restarted application
          Application fully closed and reopened

□ Step 7: Opened Punitions page
          Chatbot visible on right side

□ Step 8: Text colors are readable
          Messages show light indigo and gray text

□ Step 9: Typed test message
          Bot responds with AI-generated text
```

---

## 🔍 Verification: How to Know It's Really Working

### In the Application:

**Text Colors Check:**
```
✅ Bot messages are LIGHT INDIGO (#e0e7ff)
✅ User messages are LIGHT GRAY (#f3f4f6)
❌ If text is still WHITE = cache issue
```

**Response Variation Check:**
```
Message 1: "What about cheating?"
Response:  "Cheating is a critical violation..."

Message 2: "What about cussing?"
Response:  "Cussing is offensive language..."

Message 3: "Cheating again?"
Response:  "For cheating, we discussed... [different angle]"

✅ Different responses = AI is working
❌ Same response = Using fallback rules
```

**Debug Console Check:**
```
Look for these messages:
✅ "DEBUG: Attempting to call Mistral AI API..."
✅ "DEBUG: Response received: [response text]"
✅ "DEBUG: AI API successful, returning response"

If you see:
❌ "DEBUG: Using rule-based responses" = API not configured
❌ "API Error: Invalid API key" = Key is wrong
```

---

## 🆘 Troubleshooting

### Problem: "API not configured"
```
SOLUTION:
1. Check AIConfig.java line 20
2. Is API_KEY still the placeholder?
3. Replace it with actual key
4. Save
5. Restart app
```

### Problem: "Invalid API key"
```
SOLUTION:
1. Go back to https://console.mistral.ai/api-keys
2. Double-check your key (copy again)
3. Paste into AIConfig.java line 20
4. Make sure NO extra spaces
5. Save and restart
```

### Problem: "Text is still white/hard to read"
```
SOLUTION:
1. Files were modified but cached
2. Clear cache: File → Invalidate Caches (JetBrains)
3. Rebuild: Ctrl+Shift+F9
4. Restart IDE
5. Restart application
```

### Problem: "Chatbot not responding"
```
SOLUTION:
1. Check internet connection
2. Verify API key is correct
3. Check debug console for errors
4. Try a different question
5. Restart application
```

### Problem: "Error about getNewReclamationsSince"
```
SOLUTION: This was a compilation error, now FIXED in updated code
No action needed - the fix has been applied
```

---

## 📋 Common Questions

### Q: Where exactly do I paste the key?
**A:** In file `AIConfig.java`, line 20, between the quotes:
```java
public static final String API_KEY = "PASTE_HERE";
```

### Q: What if I don't have the key yet?
**A:** Go to https://console.mistral.ai/api-keys and create one (takes 1 minute)

### Q: Does the chatbot work without the API key?
**A:** Yes! It uses fallback rules, but won't be AI-powered

### Q: Can I change the model?
**A:** Yes, edit line in AIConfig.java:
```java
public static final String MODEL = "mistral-large-latest";
```

Options: `mistral-large-latest`, `mistral-medium-latest`, `mistral-small-latest`

### Q: Why is my key showing in code?
**A:** It's only in your LOCAL copy, not on GitHub (if you don't commit it)

### Q: How much does it cost?
**A:** Check at https://console.mistral.ai/billing (varies by model/usage)

### Q: Can I use a different AI?
**A:** Yes, but would need to modify `AIApiClient.java`

---

## ✅ You're Done!

Once you've completed all 8 steps and verified:

```
✓ API key pasted in AIConfig.java line 20
✓ File saved
✓ Application restarted
✓ Punitions page opens
✓ Chatbot text is readable
✓ AI responds to messages
```

## You can now:

🎉 **Use the AI-powered punishment recommendation chatbot!**

- Ask about any violation type
- Get intelligent recommendations
- Discuss punishment scenarios
- See context-aware responses
- Have real conversations with AI

---

**Status: Ready to Use!** ✅
**Setup Time: ~10 minutes**
**Support: Check CHATBOT_AI_SETUP.md for detailed guide**


