# Mistral AI Setup - 1 Minute

## Your API Key Setup

1. **Find Your Mistral API Key:**
   - Go to: https://console.mistral.ai/api-keys
   - Copy your existing API key

2. **Paste It In:**
   - Open: `src/main/java/edu/connexion3a36/rankup/config/AIConfig.java`
   - Find **Line 20**:
     ```java
     public static final String API_KEY = "PASTE_YOUR_MISTRAL_API_KEY_HERE";
     ```
   - Replace with:
     ```java
     public static final String API_KEY = "your-mistral-key-here";
     ```
   - Save (Ctrl+S)

3. **Restart App**
   - Close and reopen the JavaFX application
   - Done! ✅

## Test It

Open the Chatbot and ask: **"What about cheating?"**

You should get a **real response from Mistral AI** (not static).

## Debug Info

Check your console for messages like:
```
DEBUG: Attempting to call Mistral AI API...
DEBUG: API Key configured: true
DEBUG: Mistral AI API successful, returning response
```

If you see errors, check:
- API key is pasted correctly (no extra spaces/quotes)
- Internet connection is working
- Your Mistral account has credits
