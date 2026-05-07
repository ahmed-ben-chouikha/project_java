# 🧪 REMEMBER ME FEATURE - CONSOLE TESTING GUIDE

## Run Application to Test

### Windows PowerShell
```powershell
# Navigate to project directory
cd C:\Users\ghass\OneDrive\Desktop\project_java

# Run the application
mvn clean javafx:run
```

### Windows Command Prompt
```cmd
cd C:\Users\ghass\OneDrive\Desktop\project_java
mvn clean javafx:run
```

### Quick Run (if mvn is in PATH)
```bash
mvn javafx:run
```

---

## 📋 Manual Testing Steps

### Test 1: First Login with Remember Me
```
TIME: ~2 minutes

STEPS:
1. Application starts → Login screen appears
2. Enter Email: test@example.com
3. Enter Password: password123
4. ✓ CHECK the "Remember me" checkbox
5. Click "Sign In" button
6. Verify: Login successful, Dashboard appears

EXPECTED RESULT:
✅ Credentials saved to system preferences
✅ "Remember me" checkbox was checked
✅ Login succeeds
```

### Test 2: App Restart - Verify Auto-Fill
```
TIME: ~1 minute

STEPS:
1. Close application completely (use File→Exit or X button)
2. Wait 2 seconds
3. Re-launch: mvn javafx:run
4. Wait for login screen to appear
5. CHECK the fields:
   - Email field: Should show "test@example.com"
   - Password field: Should show "password123" (masked)
   - Remember me: Should be ✓ CHECKED

EXPECTED RESULT:
✅ Email auto-filled: test@example.com
✅ Password auto-filled: password123 (masked)
✅ Checkbox is checked
✅ Can click Sign In directly without retyping
```

### Test 3: Uncheck Remember Me
```
TIME: ~2 minutes

STEPS:
1. With auto-filled credentials on screen
2. UNCHECK the "Remember me" checkbox
3. Click "Sign In" button
4. Login successfully
5. Close application
6. Restart: mvn javafx:run
7. Observe login screen

EXPECTED RESULT:
✅ Credentials do NOT auto-fill (fields are empty)
✅ "Remember me" checkbox is unchecked
✅ Must manually enter credentials again
```

### Test 4: Different User Credentials
```
TIME: ~2 minutes

STEPS:
1. Clear the email field
2. Enter Email: admin@rankup.com
3. Enter Password: adminpass456
4. ✓ CHECK "Remember me"
5. Click "Sign In"
6. Login succeeds
7. Close application
8. Restart: mvn javafx:run

EXPECTED RESULT:
✅ NEW credentials saved (admin@rankup.com)
✅ OLD credentials replaced
✅ Auto-fill shows: admin@rankup.com
✅ Only latest remembered credentials are saved
```

### Test 5: Clear Fields Manually
```
TIME: ~1 minute

STEPS:
1. Application starts with auto-filled credentials
2. Click in email field
3. Select All (Ctrl+A) and Delete
4. Click in password field
5. Select All (Ctrl+A) and Delete
6. Click "Sign In"
7. Observe error message

EXPECTED RESULT:
✅ Error message: "Email and password are required"
✅ Fields are now empty
✅ Note: Credentials still saved in system
✅ (To truly forget, uncheck "Remember me" and sign in with different creds)
```

---

## 🔍 Console Output Monitoring

### What You Should See

```
[INFO] Scanning for projects...
[INFO] Building rankup 1.0-SNAPSHOT
[INFO] --- javafx-maven-plugin:0.0.x:run (default-cli) @ rankup ---
[INFO] Launching JavaFX application...

✅ Application window opens
✅ Login screen displays
✅ CheckBox visible below password field
✅ No error messages

---

[Expected Console Output]
Load resources...
Initialize controllers...
AuthController initialized
Checking for saved credentials...
✅ Credentials loaded successfully
TextField values set
CheckBox selected
---

[After Sign In]
Authentication attempt...
User found in database
Credentials saved to preferences
Login successful!
Loading dashboard...
```

### What You Should NOT See

```
❌ NullPointerException
❌ ClassNotFoundException  
❌ Cannot find symbol: rememberMeCheckBox
❌ Cannot find symbol: CredentialManager
❌ Failed to initialize AuthController
❌ SQLException during authentication
```

If you see any ❌ errors, check `REMEMBER_ME_IMPLEMENTATION.md` troubleshooting section.

---

## 💾 Verify Credentials Storage

### Windows - Check Registry
```powershell
# Open Registry Editor
regedit

# Navigate to:
HKEY_CURRENT_USER\Software\JavaSoft\Prefs\edu\connexion3a36\rankup\utils

# Look for:
- rankup_email (value: your email)
- rankup_password (value: base64 encoded password)
- rankup_remember_me (value: true/false)
```

### Alternative - Check via Java Code
```java
// Add to AuthController.java temporarily for testing
@FXML
void onDebugShowCredentials(ActionEvent event) {
    String[] creds = CredentialManager.getCredentials();
    if (creds != null) {
        System.out.println("Saved Email: " + creds[0]);
        System.out.println("Saved Password Length: " + creds[1].length());
        System.out.println("Has Credentials: " + CredentialManager.hasRememberedCredentials());
    } else {
        System.out.println("No saved credentials found");
    }
}
```

---

## ⏱️ Expected Performance

| Operation | Expected Time | Actual Time |
|-----------|---|---|
| App startup (cold) | < 5 sec | ___ |
| Load credentials | < 100 ms | ___ |
| Sign In | < 2 sec | ___ |
| App restart (warm) | < 3 sec | ___ |
| Dashboard load | < 2 sec | ___ |

If any operation takes longer, check console for errors.

---

## 🧩 Integration Testing

### Test with Dashboard Logout
```
STEPS:
1. Sign In with "Remember me" checked
2. Dashboard appears
3. Look for "Logout" button
4. Click Logout (should call AuthController.logout())
5. Should return to login screen
6. Restart application

EXPECTED RESULT:
✅ Credentials were cleared by logout()
✅ Login screen shows empty fields
✅ "Remember me" is unchecked
```

**Note:** If Logout button doesn't exist yet, you'll need to implement it.

### Test Multiple Rapid Restarts
```
STEPS:
1. Sign In with "Remember me"
2. Close app
3. Restart app
4. Close app immediately
5. Restart app
6. Close app (repeat 5 times total)
7. Restart and verify

EXPECTED RESULT:
✅ Credentials persist through all cycles
✅ No data corruption
✅ Consistent behavior
```

---

## 🐛 Known Behaviors

### Behavior 1: Password is Masked
```
EXPECTED:
When password auto-fills, it shows as: ••••••••••
But internally contains: password123

This is CORRECT. JavaFX masks passwords for security.
```

### Behavior 2: Only Latest Credentials Saved
```
EXPECTED:
If User A signs in with "Remember me"
Then User B signs in with "Remember me"
Only User B's credentials are saved

The system stores ONE set of credentials (not multiple users)
```

### Behavior 3: Checkbox Reflects Saved State
```
EXPECTED:
If credentials are saved:
- App starts with checkbox CHECKED
- Fields are pre-filled

If no credentials saved:
- App starts with checkbox UNCHECKED
- Fields are empty

If checkbox is unchecked at startup:
- Application auto-loads credentials but shows checkbox unchecked
- This is a display inconsistency (minor UX issue for future)
```

---

## 📊 Testing Results Template

Copy and fill out for your testing:

```markdown
# Testing Results - Remember Me Feature

## Test Date: _______________
## Tester Name: _______________

### Environment
- OS: Windows 10/11
- Java Version: 11/17/21
- Maven Version: _______________
- IDE: _______________

### Test Results

#### Test 1: First Login with Remember Me
Status: [ ] PASS [ ] FAIL [ ] PARTIAL
Notes: _________________________________________________

#### Test 2: App Restart - Verify Auto-Fill
Status: [ ] PASS [ ] FAIL [ ] PARTIAL
Notes: _________________________________________________

#### Test 3: Uncheck Remember Me
Status: [ ] PASS [ ] FAIL [ ] PARTIAL
Notes: _________________________________________________

#### Test 4: Different User Credentials
Status: [ ] PASS [ ] FAIL [ ] PARTIAL
Notes: _________________________________________________

#### Test 5: Clear Fields Manually
Status: [ ] PASS [ ] FAIL [ ] PARTIAL
Notes: _________________________________________________

### Console Output
[ ] No errors shown
[ ] Credentials loaded message shown
[ ] All imports resolved
[ ] No warnings

### Issues Found
1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

### Recommendations
1. _______________________________________________
2. _______________________________________________

### Overall Status
[ ] READY FOR PRODUCTION
[ ] NEEDS FIXES
[ ] NEEDS ENHANCEMENTS

Signed: _________________ Date: _________________
```

---

## 🚀 Quick Test Command

### Run Full Test Cycle (5 minutes)
```bash
# Terminal 1: Run app
mvn clean javafx:run

# (In UI) Sign in with remember me
# Close app after successful login

# Terminal 1: Close previous run (Ctrl+C)
# Terminal 1: Run again
mvn clean javafx:run

# (In UI) Verify credentials auto-filled
# Success if email/password are pre-filled!
```

---

## 📝 Test Report Template

```
╔════════════════════════════════════════════════════════╗
║  REMEMBER ME FEATURE - TEST REPORT                    ║
╠════════════════════════════════════════════════════════╣
║  Date: ___________________                             ║
║  Tester: _________________                             ║
║  Build: mvn javafx:run                                 ║
╠════════════════════════════════════════════════════════╣
║                                                        ║
║  FEATURE CHECKLIST                                    ║
║  ☐ Checkbox displays                                   ║
║  ☐ Checkbox clickable                                  ║
║  ☐ Credentials save on login                           ║
║  ☐ Credentials load on restart                         ║
║  ☐ Password masked in field                            ║
║  ☐ Can sign in with auto-filled creds                  ║
║  ☐ Unchecking prevents save                            ║
║  ☐ No console errors                                   ║
║                                                        ║
║  OVERALL RESULT: [ ] PASS [ ] FAIL                    ║
║                                                        ║
║  Comments:                                             ║
║  ___________________________________________           ║
║  ___________________________________________           ║
║                                                        ║
╚════════════════════════════════════════════════════════╝
```

---

## 🎯 Success Criteria

Your testing is complete when:

✅ 5/5 tests pass
✅ No console errors
✅ Credentials persist across restarts
✅ Password remains masked
✅ Checkbox state matches saved credentials
✅ Performance is acceptable (< 3 sec startup)
✅ All documentation is understood

---

## 📞 Troubleshooting Commands

### If Maven fails:
```bash
mvn clean install
mvn javafx:run
```

### If javafx plugin missing:
```bash
# Check pom.xml has javafx-maven-plugin
# Then retry: mvn javafx:run
```

### If port conflicts:
```bash
# Close any other Java applications
# Ensure only one instance of the app runs
```

### If credentials not clearing:
```bash
# Delete Java preferences cache:
# Windows: Delete from Registry (see section above)
# Linux/Mac: rm -rf ~/.java/.userPrefs/
# Then restart app
```

---

**Happy Testing! 🎉**

Document your results and report any issues to the development team.

