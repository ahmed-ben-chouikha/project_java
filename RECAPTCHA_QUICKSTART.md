# Quick Setup: reCAPTCHA Configuration

## ONE-TIME SETUP

### 1. Get Your Keys from Google
Visit: https://www.google.com/recaptcha/admin

Create a new site with:
- Type: **reCAPTCHA v2** - "I'm not a robot" Checkbox
- Domains: 
  - For development: `localhost`
  - For production: `yourdomain.com`

### 2. Copy Your Keys
After creating the site, you'll see:
- **Site Key**: [Copy this]
- **Secret Key**: [Copy this]

### 3. Update Configuration
Open: `src/main/resources/recaptcha.properties`

Replace with your keys:
```properties
recaptcha.site.key=PASTE_YOUR_SITE_KEY_HERE
recaptcha.secret.key=PASTE_YOUR_SECRET_KEY_HERE
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify
```

### 4. Rebuild Project
```bash
mvn clean install
```

### 5. Run Application
The reCAPTCHA checkbox will now appear on the login page!

---

## TESTING

### Local Development
- Use the keys you created with `localhost` domain
- reCAPTCHA will verify automatically
- Mock tokens are used for faster testing

### Production
- Create separate reCAPTCHA site with your production domain
- Update keys in `recaptcha.properties`
- Deploy with new configuration

---

## IMPORTANT SECURITY NOTES

⚠️ **NEVER commit your Secret Key to Git!**

If accidentally committed:
1. Regenerate keys in Google reCAPTCHA console
2. Update all instances with new keys

Add to `.gitignore`:
```
recaptcha.properties
*.key
secrets/
```

---

## FEATURES

✅ Displays "I'm not a robot" checkbox
✅ Google branding and privacy links
✅ Server-side token verification
✅ Integrates with existing login flow
✅ Remember Me checkbox (already implemented)
✅ Error handling and user feedback
✅ Production-ready security

---

## TROUBLESHOOTING

| Problem | Solution |
|---------|----------|
| Checkbox not appearing | Check FXML file has `recaptchaContainer` VBox |
| Verification fails | Verify Secret Key is correct in properties file |
| Keys don't work | Make sure domain matches in reCAPTCHA console |
| "Properties not found" | Run `mvn clean install` to rebuild |

---

## NEXT STEPS (OPTIONAL)

1. **Rate Limiting**: Add login attempt throttling
2. **Logging**: Log verification attempts for security audits
3. **v3 Upgrade**: Switch to reCAPTCHA v3 for invisible verification
4. **Custom Theme**: Style the checkbox to match your brand
5. **Multi-language**: Translate error messages


