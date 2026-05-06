# OpenAI Integration - Quick Start (2 Minutes)

## Install Your API Key

1. **Get API Key:**
   - Go to https://platform.openai.com/api/keys
   - Click "Create new secret key"
   - Copy the key

2. **Paste Key:**
   - Open: `src/main/java/edu/connexion3a36/rankup/config/AIConfig.java`
   - Find: Line 20 with `public static final String API_KEY = "PASTE_YOUR_OPENAI_API_KEY_HERE";`
   - Replace with your key: `public static final String API_KEY = "sk-proj-...";`
   - Save file (Ctrl+S)

3. **Restart App**
   - Close and reopen your JavaFX application
   - Done! ✅

## Test It

Open the chatbot pane and ask: **"What about cheating?"**

The bot should respond with an AI-powered recommendation (takes ~2 seconds).

## That's It!

Your chatbot now uses OpenAI API instead of hardcoded responses.

### Key Features
- ✅ Real AI responses (GPT-3.5-turbo)
- ✅ Multi-turn conversation history
- ✅ Automatically falls back to rule-based if API fails
- ✅ Free tier: $5 credit per new account
- ✅ Cheap: ~2 cents per 100 conversations

## See Also
- Full guide: [OPENAI_INTEGRATION_GUIDE.md](OPENAI_INTEGRATION_GUIDE.md)
- Troubleshooting: Check console output (look for "DEBUG:" messages)
