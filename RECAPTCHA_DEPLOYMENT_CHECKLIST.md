# reCAPTCHA Production Deployment Checklist

## Pre-Deployment Checklist

### 1. Google reCAPTCHA Setup
- [ ] Visited https://www.google.com/recaptcha/admin
- [ ] Created new reCAPTCHA v2 site
- [ ] Selected "I'm not a robot" Checkbox type
- [ ] Added production domain(s) to the site
- [ ] Received Site Key and Secret Key
- [ ] Saved keys in secure location (password manager)
- [ ] Backed up keys in case of emergency

### 2. Code Verification
- [ ] `RecaptchaCheckBox.java` is present in UI components
- [ ] `RecaptchaUtil.java` is present in utils
- [ ] `AuthController.java` has reCAPTCHA integration
- [ ] `login.fxml` includes reCAPTCHA container
- [ ] No test/mock tokens in production code
- [ ] Error handling is comprehensive
- [ ] Logging is enabled for verification attempts

### 3. Configuration
- [ ] `recaptcha.properties` file exists
- [ ] Production Site Key is configured
- [ ] Production Secret Key is configured
- [ ] Verify URL is set to Google's API endpoint
- [ ] Configuration file is NOT in version control
- [ ] `recaptcha.properties` is added to `.gitignore`
- [ ] No hardcoded keys in source code

### 4. Dependencies
- [ ] `pom.xml` includes JSON library (org.json)
- [ ] reCAPTCHA library is in `pom.xml` (already present)
- [ ] All dependencies resolve without errors
- [ ] `mvn clean install` completes successfully
- [ ] No version conflicts in dependency tree

### 5. Security
- [ ] Secret Key is never exposed in client code
- [ ] Secret Key is never committed to Git
- [ ] HTTPS is enabled on production server
- [ ] API keys are stored securely on server
- [ ] Environment variables can be used for keys
- [ ] Key rotation plan is documented
- [ ] Access logs will be monitored

### 6. Testing
- [ ] Tested login with reCAPTCHA verified
- [ ] Tested login without reCAPTCHA verification
- [ ] Tested with correct credentials after verification
- [ ] Tested with incorrect credentials after verification
- [ ] Tested Remember Me feature with reCAPTCHA
- [ ] Tested Forgot Password with reCAPTCHA
- [ ] Tested Sign Up flow
- [ ] Tested error messages display correctly
- [ ] Tested on multiple browsers (Chrome, Firefox, Safari, Edge)
- [ ] Tested on mobile devices (if applicable)
- [ ] Tested network timeout scenarios
- [ ] Tested with VPN/proxy (if applicable)

### 7. Database
- [ ] User table structure is intact
- [ ] Authentication queries work correctly
- [ ] Ban status checks work
- [ ] OTP functionality still works
- [ ] Password reset flow still works
- [ ] No database errors in logs

### 8. Error Handling
- [ ] Network errors are handled gracefully
- [ ] Google API timeouts are handled
- [ ] Invalid tokens are rejected
- [ ] Malformed responses are handled
- [ ] User gets helpful error messages
- [ ] Error logging is enabled
- [ ] No stack traces shown to users

### 9. Performance
- [ ] Login page loads quickly
- [ ] reCAPTCHA widget doesn't impact UX
- [ ] Verification response time is acceptable
- [ ] No memory leaks in reCAPTCHA component
- [ ] Server handles high verification volume
- [ ] Database queries are optimized

### 10. Monitoring & Logging
- [ ] Application logs are being written
- [ ] reCAPTCHA verification attempts are logged
- [ ] Failed verifications are logged
- [ ] Login attempts are logged
- [ ] Server errors are captured
- [ ] Alert system is configured
- [ ] Log rotation is set up

### 11. Documentation
- [ ] README includes reCAPTCHA setup instructions
- [ ] RECAPTCHA_SETUP_GUIDE.md is complete
- [ ] RECAPTCHA_QUICKSTART.md is accessible
- [ ] Code comments explain reCAPTCHA flow
- [ ] Team members understand the implementation
- [ ] Disaster recovery procedures are documented

### 12. Backup & Recovery
- [ ] Configuration is backed up
- [ ] Keys are securely backed up
- [ ] Recovery procedure is documented
- [ ] Key rotation procedure is ready
- [ ] Emergency contacts are listed

### 13. Compliance
- [ ] Google's Terms of Service are reviewed
- [ ] reCAPTCHA Privacy Policy is understood
- [ ] GDPR compliance is verified
- [ ] CCPA compliance is verified
- [ ] Other relevant regulations are checked
- [ ] Privacy policy mentions reCAPTCHA

### 14. Performance Testing
- [ ] Load test: 1000 simultaneous login attempts
- [ ] Stress test: 10,000 verification requests
- [ ] Endurance test: 24-hour continuous operation
- [ ] Recovery test: API returns to normal after failure
- [ ] Latency test: Verification completes in <2 seconds

### 15. Final Review
- [ ] Code review completed by team member
- [ ] Security review completed
- [ ] Configuration review completed
- [ ] All tests passing
- [ ] All checklist items completed
- [ ] Approval from project lead obtained
- [ ] Ready for production deployment

---

## Pre-Production Server Setup

### SSH into Server
```bash
ssh user@production-server.com
```

### Clone Repository
```bash
cd /var/www/rankup
git clone https://github.com/your-org/rankup.git
cd rankup
```

### Install Dependencies
```bash
# Install Java if not present
sudo apt-get install openjdk-17-jdk-headless

# Install Maven if not present
sudo apt-get install maven

# Build project
mvn clean package -DskipTests
```

### Configure reCAPTCHA
```bash
# Create config directory
sudo mkdir -p /etc/rankup

# Copy recaptcha.properties
sudo cp src/main/resources/recaptcha.properties /etc/rankup/

# Set correct permissions
sudo chmod 600 /etc/rankup/recaptcha.properties

# Edit with your production keys
sudo nano /etc/rankup/recaptcha.properties
```

### Create Systemd Service
```bash
sudo nano /etc/systemd/system/rankup.service
```

Add:
```ini
[Unit]
Description=RankUp E-Sports Platform
After=network.target

[Service]
Type=simple
User=rankup
WorkingDirectory=/var/www/rankup
ExecStart=/usr/bin/java -jar target/rankup-1.0-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Enable and Start Service
```bash
sudo systemctl daemon-reload
sudo systemctl enable rankup
sudo systemctl start rankup

# Check status
sudo systemctl status rankup
```

---

## Post-Deployment Verification

### 1. Service Health Check
```bash
# Check if service is running
sudo systemctl status rankup

# Check logs
sudo journalctl -u rankup -f

# Check port is listening
sudo netstat -tulpn | grep :8080
```

### 2. Smoke Tests
```bash
# Test login page loads
curl -I https://yourdomain.com/login

# Check reCAPTCHA loads in response
curl https://yourdomain.com/login | grep -i recaptcha
```

### 3. Manual Testing
- [ ] Open login page in browser
- [ ] Verify reCAPTCHA checkbox appears
- [ ] Verify Remember Me checkbox appears
- [ ] Verify all form fields are present
- [ ] Verify styling looks correct
- [ ] Check browser console for errors (F12)
- [ ] Test login with correct credentials
- [ ] Test login with incorrect credentials
- [ ] Test with VPN/Proxy if applicable

### 4. Monitoring Setup
```bash
# Monitor logs in real-time
sudo tail -f /var/log/rankup/app.log

# Set up log rotation
sudo nano /etc/logrotate.d/rankup
```

Add:
```
/var/log/rankup/*.log {
    daily
    missingok
    rotate 7
    compress
    delaycompress
    notifempty
}
```

---

## Troubleshooting on Production

### Issue: reCAPTCHA Not Appearing
```bash
# Check logs for errors
sudo journalctl -u rankup -n 50

# Verify configuration file exists
sudo cat /etc/rankup/recaptcha.properties

# Check file permissions
sudo ls -la /etc/rankup/recaptcha.properties
```

### Issue: Verification Always Fails
```bash
# Verify keys are correct
sudo grep recaptcha.site.key /etc/rankup/recaptcha.properties
sudo grep recaptcha.secret.key /etc/rankup/recaptcha.properties

# Test API connectivity
curl https://www.google.com/recaptcha/api/siteverify

# Check firewall rules
sudo iptables -L -n
```

### Issue: High Latency
```bash
# Check server resources
free -h
top

# Check network connectivity to Google
traceroute google.com

# Monitor reCAPTCHA response times
sudo grep "verification" /var/log/rankup/app.log | tail -20
```

---

## Rollback Plan

### If Critical Issue Found
```bash
# Stop the service
sudo systemctl stop rankup

# Revert to previous version
git reset --hard HEAD~1

# Rebuild
mvn clean package -DskipTests

# Restart
sudo systemctl start rankup
```

### If Configuration Issue
```bash
# Stop service
sudo systemctl stop rankup

# Restore previous configuration
sudo cp /etc/rankup/recaptcha.properties.backup /etc/rankup/recaptcha.properties

# Restart
sudo systemctl start rankup
```

---

## Post-Deployment Monitoring

### Daily Checks
- [ ] Service is running
- [ ] No errors in logs
- [ ] Login success rate is normal
- [ ] Response times are acceptable
- [ ] CPU and memory usage are normal

### Weekly Checks
- [ ] Review verification statistics
- [ ] Check for unusual patterns
- [ ] Review security logs
- [ ] Verify backups are working
- [ ] Update documentation if needed

### Monthly Checks
- [ ] Performance review
- [ ] Security audit
- [ ] Update dependencies
- [ ] Review and update monitoring rules
- [ ] Plan for improvements

---

## Maintenance Tasks

### Key Rotation (Every 90 days)
1. Generate new keys in Google reCAPTCHA admin
2. Update development keys
3. Update staging keys
4. Update production keys
5. Verify all environments work
6. Archive old keys

### Dependency Updates
```bash
# Check for updates
mvn dependency:tree

# Update versions
mvn versions:use-latest-versions

# Test thoroughly
mvn clean test

# Deploy when ready
```

### Log Cleanup
```bash
# Archive old logs
tar -czf /backup/rankup-logs-$(date +%Y%m%d).tar.gz /var/log/rankup/

# Remove logs older than 30 days
find /var/log/rankup -name "*.log" -mtime +30 -delete
```

---

## Emergency Procedures

### Complete Service Failure
```bash
# Check what went wrong
sudo journalctl -u rankup -n 100

# Restart service
sudo systemctl restart rankup

# If still failing, investigate:
sudo systemctl status rankup -l
```

### Database Connection Lost
```bash
# Verify database is accessible
mysql -h localhost -u rankup_user -p

# Check network connectivity
telnet db.server.com 3306

# Restart application after DB recovers
sudo systemctl restart rankup
```

### Google reCAPTCHA API Down
- Service continues to work for already-verified users
- New login attempts will fail
- Display user-friendly error message
- Switch to alternative verification method (2FA)
- Monitor Google's status page
- Wait for API recovery

---

## Success Indicators

✅ Service is running without errors
✅ reCAPTCHA appears on login page
✅ Login succeeds with verification
✅ Error messages display correctly
✅ No 5xx errors in logs
✅ Response time < 2 seconds
✅ CPU usage < 50%
✅ Memory usage stable
✅ Database connections healthy
✅ Backup jobs running
✅ Monitoring alerts working
✅ Team is notified and ready

---

## Contact & Escalation

| Role | Contact | Availability |
|------|---------|--------------|
| DevOps Lead | devops@company.com | 24/7 |
| Security Team | security@company.com | 24/7 |
| Backend Team | backend@company.com | 9-5 |
| Google Support | support.google.com | 24/7 |

---

## Sign-Off

- [ ] Deployment approved by: _______________
- [ ] Date: _______________
- [ ] Deployed by: _______________
- [ ] Verified by: _______________
- [ ] Notes: _______________

---

Good luck with your production deployment! 🚀

