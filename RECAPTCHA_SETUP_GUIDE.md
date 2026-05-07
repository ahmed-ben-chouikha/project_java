# reCAPTCHA Integration Guide

## Overview
This document describes the reCAPTCHA v2 implementation in the RankUp login page to protect against bot attacks and automated account takeover attempts.

## What is reCAPTCHA?
Google reCAPTCHA is a free service that protects your website from spam and abuse. reCAPTCHA v2 uses a simple "I'm not a robot" checkbox to verify that users are human.

## Files Added/Modified

### New Files Created:
1. **RecaptchaUtil.java** - Utility class for server-side token verification
   - Location: `src/main/java/edu/connexion3a36/rankup/utils/RecaptchaUtil.java`
   - Handles communication with Google's reCAPTCHA servers
   - Verifies reCAPTCHA tokens

2. **RecaptchaCheckBox.java** - Custom JavaFX component
   - Location: `src/main/java/edu/connexion3a36/rankup/ui/components/RecaptchaCheckBox.java`
   - Displays the reCAPTCHA checkbox UI
   - Manages user verification state

3. **recaptcha.properties** - Configuration file
   - Location: `src/main/resources/recaptcha.properties`
   - Contains API keys (Site Key and Secret Key)

### Modified Files:
1. **pom.xml** - Added JSON dependency for response parsing
2. **AuthController.java** - Integrated reCAPTCHA validation
3. **login.fxml** - Added reCAPTCHA container to the UI

## Setup Instructions

### Step 1: Get Your reCAPTCHA Keys
1. Go to https://www.google.com/recaptcha/admin
2. Sign in with your Google account
3. Click on the "+" button to create a new site
4. Fill in the form:
   - Label: "RankUp - E-Sports Platform"
   - reCAPTCHA Type: **reCAPTCHA v2** → "I'm not a robot" Checkbox
   - Domains: Your domain (e.g., localhost for testing, your.domain.com for production)
5. Accept the terms and submit
6. You'll receive:
   - **Site Key** (public) - Shows on your website
   - **Secret Key** (private) - Server-side verification

### Step 2: Configure Your Keys
Edit `src/main/resources/recaptcha.properties`:

```properties
recaptcha.site.key=YOUR_SITE_KEY_HERE
recaptcha.secret.key=YOUR_SECRET_KEY_HERE
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify
```

⚠️ **IMPORTANT**: Never commit your Secret Key to version control!

### Step 3: Update Dependencies
The pom.xml has been updated with the JSON library. Run:
```bash
mvn clean install
```

## How It Works

### User Flow:
1. User visits the login page
2. reCAPTCHA checkbox appears with Google branding
3. User clicks the checkbox
4. Google's verification challenges may appear (image selection, etc.)
5. If verified, user can proceed with login
6. Upon login submission:
   - AuthController validates reCAPTCHA was checked
   - Server makes POST request to Google's API
   - Google responds with success/failure and score
   - If verified, login proceeds normally
   - If not verified, error message is shown

### Code Flow:

```
Login Page (login.fxml)
    ↓
AuthController.initialize()
    ↓
initializeRecaptcha() 
    ↓
RecaptchaCheckBox component added to UI
    ↓
User clicks checkbox → Token generated
    ↓
User submits form
    ↓
AuthController.onSignIn()
    ↓
recaptchaCheckBox.isVerified() check
    ↓
If verified: Proceed with authentication
If not verified: Show error & return
    ↓
(if passing verification) RecaptchaUtil.verifyToken()
    ↓
POST to Google's reCAPTCHA API
    ↓
Response parsed, score checked
    ↓
Login completed or rejected
```

## reCAPTCHA Verification Response

Google's API returns JSON like:
```json
{
  "success": true,
  "challenge_ts": "2024-05-06T12:34:56Z",
  "hostname": "example.com",
  "error-codes": []
}
```

For reCAPTCHA v3 (if upgraded), additional fields:
```json
{
  "success": true,
  "score": 0.9,
  "action": "LOGIN"
}
```

## Security Best Practices

1. **Keep Secret Key Private**
   - Never expose it in frontend code
   - Never commit to version control
   - Use environment variables in production

2. **Verify on Server-Side**
   - Always verify tokens on the server
   - Never trust client-side verification alone
   - Client-side check is only for UX

3. **Rate Limiting**
   - Implement rate limiting on login attempts
   - Combine with reCAPTCHA for defense-in-depth
   - Consider after multiple failed attempts

4. **Logging**
   - Log failed reCAPTCHA attempts
   - Monitor unusual patterns
   - Alert on suspicious activity

## Troubleshooting

### Issue: reCAPTCHA not showing
- Check that `RecaptchaCheckBox` is being instantiated in `AuthController.initialize()`
- Verify `recaptchaContainer` VBox is properly defined in FXML
- Check browser console for errors

### Issue: Verification always fails
- Verify your Secret Key is correct in `recaptcha.properties`
- Check that token is being generated when checkbox is clicked
- Ensure you're using correct domain in Google reCAPTCHA console

### Issue: "recaptcha.properties not found"
- File should be in `src/main/resources/` directory
- After rebuilding, should be available in classpath
- Run `mvn clean install`

## Testing

### Local Testing:
1. Use `localhost` as domain in Google reCAPTCHA console
2. Key will work for testing on http://localhost:8080, etc.
3. Mock tokens are used for testing (no real Google API calls)

### Production Deployment:
1. Create new reCAPTCHA site with your production domain
2. Get new keys for production
3. Update `recaptcha.properties` with production keys
4. Remove mock token generation when using real API

## Advanced Configuration

### For reCAPTCHA v3 (future upgrade):
Change the score threshold in `RecaptchaUtil.verifyToken()`:

```java
// Current (v2): Just check success flag
return success && score > 0.5;

// Future (v3): More granular control
if (score > 0.9) return true;      // Very likely human
if (score > 0.7 && !suspiciousIP) return true;  // Likely human
return false;                       // Likely bot
```

### Custom Error Handling:
Extend `RecaptchaCheckBox` to handle different error types:

```java
public class AdvancedRecaptchaCheckBox extends RecaptchaCheckBox {
    public List<String> getErrorCodes() {
        // Return specific error codes from Google
    }
}
```

## References
- Google reCAPTCHA: https://www.google.com/recaptcha/about/
- Admin Console: https://www.google.com/recaptcha/admin
- reCAPTCHA v2 Documentation: https://developers.google.com/recaptcha/docs/v2
- API Verification Endpoint: https://www.google.com/recaptcha/api/siteverify

## Support
For issues or questions:
1. Check Google reCAPTCHA documentation
2. Review error logs in application console
3. Verify domain settings in reCAPTCHA admin console
4. Test with mock tokens first, then real tokens

