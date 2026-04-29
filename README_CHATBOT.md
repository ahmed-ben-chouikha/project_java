Punition Advisor Chatbot

This project includes a simple rule-based Punition Advisor chatbot used by admins to get recommendation for punishments.

Where to put an external LLM API key (optional):

- By default the chatbot runs locally with built-in heuristic rules and requires no API key.
- To enable an external LLM (OpenAI or other) you can modify `src/main/java/edu/connexion3a36/rankup/services/ChatbotService.java` and implement an HTTP client call.

If you implement LLM integration, store your API key in one of these safe ways:

1) Environment variable (recommended):
   - Set an environment variable named `CHATBOT_API_KEY` on your system.
   - On Windows PowerShell (temporary):

```powershell
$env:CHATBOT_API_KEY = "sk-...your-key..."
```

   - Or set it permanently via System Settings > Environment Variables.

2) System property (less recommended):
   - Launch the app with `-Dchatbot.api.key=sk-...` JVM argument.

3) External config file (if you prefer):
   - Add a properties file `chatbot.properties` in the resources folder and read it in `ChatbotService` (ensure the file is NOT committed to VCS).

Security notes:
- Never commit API keys to source control.
- If you call an external LLM, strip PII from the prompt before sending it.
- Log LLM outputs and decisions for audit, but be cautious storing sensitive evidence.

If you want, I can implement a secure OpenAI client in `ChatbotService` that reads the key from `CHATBOT_API_KEY`. Reply "implement LLM" and I will add it and give instructions on installing any HTTP/JSON dependencies.

