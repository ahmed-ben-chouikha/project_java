# OpenAI API Integration Guide for eSports Chatbot

## Overview

Your **BanRecommendationChatbot** has been updated to use **OpenAI's GPT API** instead of static/hardcoded responses. This guide walks you through setting it up.

## What Changed

✅ **Replaced:** Mistral AI integration  
✅ **With:** OpenAI API (GPT-3.5-turbo or GPT-4)  
✅ **HTTP Client:** Java's built-in `java.net.http.HttpClient` (no external dependencies added)  
✅ **New Feature:** Multi-turn conversation history support  

## Files Modified/Created

- **`AIConfig.java`** - Updated to use OpenAI endpoints and configuration
- **`OpenAIApiClient.java`** - **NEW** API client using Java's HttpClient with conversation history support
- **`BanRecommendationChatbot.java`** - Updated to call OpenAI instead of Mistral

## Setup Instructions

### Step 1: Get an OpenAI API Key

1. Go to **https://platform.openai.com/api/keys**
2. Sign up or log in with your OpenAI account
3. Click **"Create new secret key"**
4. Copy the generated key (you won't be able to see it again after closing!)
5. Keep it safe - treat it like a password

**Note:** Free trial accounts get $5 in credits. GPT-3.5-turbo costs ~$0.0005 per conversation.

### Step 2: Paste the API Key

1. Open **`AIConfig.java`** in your IDE
   - Path: `src/main/java/edu/connexion3a36/rankup/config/AIConfig.java`

2. Find **line 20** with:
   ```java
   public static final String API_KEY = "PASTE_YOUR_OPENAI_API_KEY_HERE";
   ```

3. Replace `"PASTE_YOUR_OPENAI_API_KEY_HERE"` with your actual API key:
   ```java
   public static final String API_KEY = "sk-proj-abc123...xyz";
   ```

4. **Save the file** (Ctrl+S / Cmd+S)

### Step 3: Restart Your Application

Simply restart the JavaFX application. The chatbot will now use OpenAI API calls.

## Configuration Options

All settings are in `AIConfig.java`:

```java
// The model to use
public static final String MODEL = "gpt-3.5-turbo";  // Fast & cheap (recommended)
// Alternative: "gpt-4" (more capable, more expensive)

// Temperature: 0-2
// Lower = more deterministic, Higher = more creative
public static final double TEMPERATURE = 0.7;

// Max response length (tokens)
public static final int MAX_TOKENS = 1000;
```

## How It Works

### Conversation History
The chatbot now remembers previous messages in the conversation:
- **System remembers:** Last 10 user/assistant exchanges + system instructions
- **Token efficient:** Doesn't send entire history with each message (max ~10 exchanges)
- **Multi-turn support:** You can have natural back-and-forth conversations

### Request Flow
```
User: "What about cheating?"
  ↓
ChatbotPaneController.onSendMessage()
  ↓
BanRecommendationChatbot.chat(userInput)
  ↓
OpenAIApiClient.sendMessage(userInput)
  ↓
HTTP POST → https://api.openai.com/v1/chat/completions
  ↓
OpenAI processes with conversation history
  ↓
Response → Display in UI
```

## Fallback Behavior

If OpenAI API is unavailable:
1. **Missing API Key** → Shows config instructions & uses rule-based responses
2. **API Error** → Shows error details & uses rule-based responses
3. **Network Error** → Shows error & uses rule-based responses

The chatbot gracefully degrades to rule-based recommendations (pre-coded for each violation type).

## Testing the Integration

1. Start the application
2. Open the Chatbot pane
3. Try asking: **"What about cheating?"**

Expected behavior:
- ✅ First response comes from OpenAI (slow, ~2 seconds)
- ✅ Follow-up questions reference previous conversation
- ✅ Debug messages in console show API calls

### Debug Output Example
```
DEBUG: Attempting to call OpenAI API...
DEBUG: API Key configured: true
DEBUG: User input: What about cheating?
DEBUG: Model: gpt-3.5-turbo
DEBUG: Response received: [response from OpenAI]
DEBUG: OpenAI API successful, returning response
```

## Security Best Practices

⚠️ **DO NOT:**
- Commit `AIConfig.java` with your API key to Git
- Share your API key with anyone
- Add your key to version control

✅ **DO:**
- Add `AIConfig.java` to `.gitignore` if sharing code
- Regenerate the key if compromised
- Use environment variables for production

## Cost Estimates

Using **GPT-3.5-turbo** (default):
- **~$0.0005 per message** (input)
- **~$0.0015 per response** (output)
- **~2 cents per 100 conversations**

Using **GPT-4**:
- ~10-15x more expensive than GPT-3.5

## Troubleshooting

### Error: "Invalid API key"
- Check if your key is correctly pasted in `AIConfig.java`
- Ensure no extra spaces or quotes
- Verify key still exists on https://platform.openai.com/api/keys

### Error: "Quota exceeded" or "No credits"
- Your OpenAI account is out of credits
- Add a payment method on https://platform.openai.com/account/billing
- Free trials get $5 credit (valid 3 months)

### OpenAI returns error 429 (Rate Limited)
- You're sending requests too fast
- Wait a moment before sending more messages
- Consider using the cheaper `gpt-3.5-turbo` model

### Chatbot only gives generic responses
- Your API key might not be configured
- Check console for error messages
- Verify `AIConfig.isConfigured()` returns `true`

## Code Example: Using the Chatbot Programmatically

```java
// Simple usage
String response = BanRecommendationChatbot.chat("What's appropriate for toxicity?");
System.out.println(response);

// Clear conversation history for fresh chat
BanRecommendationChatbot.clearHistory();

// Check if AI is available
if (AIConfig.isConfigured()) {
    System.out.println("OpenAI is enabled!");
}
```

## API Documentation References

- **OpenAI API Docs:** https://platform.openai.com/docs/api-reference/chat
- **Model Pricing:** https://openai.com/pricing
- **API Usage:** https://platform.openai.com/account/usage

## Support

If you encounter issues:
1. Check the debug messages in the console
2. Verify your API key is correct
3. Ensure internet connectivity
4. Check your OpenAI account status at https://status.openai.com

## Next Steps

🎯 **Optional Enhancements:**
- Add typing indicators while waiting for API response
- Implement request timeout handling
- Cache responses for common questions
- Add conversation export feature
- Implement rate limiting for cost control

Enjoy your AI-powered chatbot! 🚀
