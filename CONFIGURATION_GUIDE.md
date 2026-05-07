# RankUp E-Sports Configuration Guide

## Email Service Setup (REQUIRED)

The email service is used for password reset and verification notifications.

### Step 1: Choose Your Email Provider

#### Option A: Gmail (Recommended)

1. Go to https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Go to https://myaccount.google.com/apppasswords
4. Select "Mail" and "Windows Computer"
5. Copy the generated 16-character password

**Configuration:**
```
SENDER_EMAIL = your-email@gmail.com
SENDER_PASSWORD = your-16-char-app-password
SMTP_HOST = smtp.gmail.com
SMTP_PORT = 587
```

#### Option B: Outlook

1. Use your Outlook email
2. For password, use your account password (2FA must be disabled)

**Configuration:**
```
SENDER_EMAIL = your-email@outlook.com
SENDER_PASSWORD = your-outlook-password
SMTP_HOST = smtp.outlook.com
SMTP_PORT = 587
```

#### Option C: Yahoo Mail

1. Go to https://login.yahoo.com
2. Generate an app password

**Configuration:**
```
SENDER_EMAIL = your-email@yahoo.com
SENDER_PASSWORD = your-app-password
SMTP_HOST = smtp.mail.yahoo.com
SMTP_PORT = 587
```

### Step 2: Update EmailService.java

Open `src/main/java/edu/connexion3a36/services/EmailService.java` and update:

```java
private static final String SENDER_EMAIL = "esports.rankup@gmail.com";  // Your email
private static final String SENDER_PASSWORD = "your-app-password";     // Your app password
```

### Step 3: Test Email Configuration

Run the application and test the "Forgot Password" feature:
1. Go to login page
2. Click "Forgot Password"
3. Enter an email
4. If configured correctly, you'll receive an email with OTP

---

## API Configuration

The API uses JWT tokens for authentication. No additional setup needed.

**Token Details:**
- **Expiration:** 24 hours
- **Algorithm:** Base64 encoding + HMAC signature
- **Secret Key:** "rankup-esports-secret-key-2026" (change in production)

---

## Database Configuration

Current configuration in `MyConnection.java`:
- **Host:** localhost
- **Port:** 3306
- **Database:** rankup_esports
- **User:** root
- **Password:** (check your setup)

---

## Security Recommendations

### For Production:

1. **Email Service:**
   - Store credentials in environment variables, not hardcoded
   - Use a proper email service like SendGrid or AWS SES
   - Enable TLS/SSL for email transport

2. **API Security:**
   - Store JWT secret in environment variable
   - Use HTTPS instead of HTTP
   - Implement rate limiting
   - Add CORS restrictions

3. **Database:**
   - Use strong database passwords
   - Enable database user password hashing
   - Use connection pooling
   - Regular backups

4. **Passwords:**
   - Implement bcrypt hashing (already done)
   - Require strong passwords
   - Implement account lockout after failed attempts
   - Add password expiration policy

---

## Troubleshooting

### "Failed to send email"

**Causes:**
1. Incorrect email credentials
2. Gmail 2-Step Verification not enabled
3. Firewall blocking port 587
4. SMTP server not responding

**Solutions:**
- Verify credentials are correct
- Enable 2-Step Verification for Gmail
- Check firewall/antivirus settings
- Test SMTP connection: `telnet smtp.gmail.com 587`

### "Token invalid or expired"

**Causes:**
1. Token format corrupted
2. Token older than 24 hours
3. Wrong secret key

**Solutions:**
- Request a new login to get fresh token
- Check system time is correct
- Verify JWT secret key matches

### "Password reset OTP not received"

**Causes:**
1. Email configuration not working
2. Email marked as spam
3. User entered wrong email

**Solutions:**
- Check email service configuration
- Check spam/junk folder
- Verify email address in database

---

## Performance Tips

1. **Database:**
   - Create indexes on email, userId columns
   - Use connection pooling

2. **API:**
   - Cache token validation results (5 minutes)
   - Implement pagination for large queries
   - Use prepared statements (already done)

3. **Email:**
   - Use async email sending for better UX
   - Batch multiple emails if needed
   - Implement retry logic for failed emails

