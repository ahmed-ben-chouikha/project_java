# 🚀 QUICK REFERENCE CARD

## WHERE TO PASTE API KEY - THE ANSWER

```
File:    AIConfig.java
Path:    src/main/java/edu/connexion3a36/rankup/config/AIConfig.java
Line:    20
Pattern: public static final String API_KEY = "YOUR_KEY_HERE";
```

---

## 3 STEPS TO ACTIVATE

### Step 1: Copy API Key
Go to: **https://console.mistral.ai/api-keys**
- Sign in → Create API key → Copy

### Step 2: Paste in Code
Open: **AIConfig.java** (line 20)
```java
public static final String API_KEY = "PASTE_HERE";
```

### Step 3: Restart App
Save (Ctrl+S) → Close App → Open App

---

## TEXT COLORS - FIXED ✅

| What | Before | After |
|------|--------|-------|
| Bot messages | ❌ White | ✅ Light indigo |
| User messages | ❌ White | ✅ Light gray |
| Readability | ❌ Hard | ✅ Perfect |

---

## AI MODEL - CORRECT ✅

```
Model: mistral-large-latest ✅
Temperature: 0.7 ✅
Max Tokens: 800 ✅
```

This is the RIGHT model for Mistral AI.

---

## ANSWERS TO YOUR QUESTIONS

### Q: Where to paste API key exactly?
**A: AIConfig.java, Line 20**

### Q: Why text was white/unreadable?
**A: FIXED! Now light indigo & gray**

### Q: Is the model right for Mistral?
**A: YES! mistral-large-latest is correct**

### Q: Why same answer every time?
**A: FIXED! Now has conversation history**

---

## VERIFICATION CHECKLIST

```
□ API key obtained from console.mistral.ai
□ API key pasted in AIConfig.java line 20
□ File saved (Ctrl+S)
□ App restarted
□ Punitions page opens
□ Chatbot visible on right side
□ Text is readable (light colors)
□ Responses are different each time
```

---

## IF SOMETHING DOESN'T WORK

```
Still white text?
→ Ctrl+Shift+F9 (rebuild) + Restart

API not configured?
→ Check line 20, replace placeholder, save, restart

Same answer always?
→ API key needs to be correct, restart app

Chatbot not responding?
→ Check internet, restart app, try again
```

---

## FILES MODIFIED

✅ ChatbotPaneController.java
✅ AIApiClient.java
✅ BanRecommendationChatbot.java
✅ AIConfig.java
✅ SideNavController.java
✅ esports.css

All changes compiled successfully! ✅

---

## QUICK TEST

1. Go to: Sidebar → Punitions
2. Find chatbot on right side
3. Type: "What about cheating?"
4. Should get: AI response (not same answer as before)
5. Type: "First offense?"
6. Should get: Context-aware response (different!)

---

**You're All Set!** 🎉
**Just paste your key in AIConfig.java line 20 and restart!**

