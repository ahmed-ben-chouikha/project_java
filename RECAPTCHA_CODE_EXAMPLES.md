# reCAPTCHA Code Examples & Reference

## Configuration Examples

### Example 1: Local Development Setup
```properties
# For testing on localhost
recaptcha.site.key=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI
recaptcha.secret.key=6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify
```

### Example 2: Production Setup
```properties
# Replace with your actual production keys from Google
recaptcha.site.key=6Lexxxxxxxxxxxxxxxx...
recaptcha.secret.key=6Lexxxxxxxxxxxxxxxx...
recaptcha.verify.url=https://www.google.com/recaptcha/api/siteverify
```

---

## Usage Examples

### Example 1: Basic Verification Check
```java
@FXML
void onSignIn(ActionEvent event) {
    // Check if reCAPTCHA is verified
    if (!recaptchaCheckBox.isVerified()) {
        showError("Verification Failed", 
                  "Please verify that you are not a robot.");
        return;
    }
    
    // Continue with login...
    String email = emailField.getText();
    String password = passwordField.getText();
    // ... rest of login logic
}
```

### Example 2: With Server-Side Token Verification
```java
@FXML
void onSignIn(ActionEvent event) {
    String token = recaptchaCheckBox.getToken();
    
    // Verify token with Google's servers
    if (!RecaptchaUtil.verifyToken(token)) {
        showError("Security Check Failed", 
                  "reCAPTCHA verification failed. Please try again.");
        return;
    }
    
    // Proceed with authentication
    authenticateUser();
}
```

### Example 3: Adding Rate Limiting
```java
// Add to AuthController
private int failedAttempts = 0;
private long lastAttemptTime = 0;

@FXML
void onSignIn(ActionEvent event) {
    // Rate limiting
    long currentTime = System.currentTimeMillis();
    if (failedAttempts > 5 && 
        (currentTime - lastAttemptTime) < 300000) { // 5 minutes
        showError("Too Many Attempts", 
                  "Please wait 5 minutes before trying again.");
        return;
    }
    
    if (!recaptchaCheckBox.isVerified()) {
        failedAttempts++;
        lastAttemptTime = currentTime;
        showError("Verification Failed", 
                  "Please verify that you are not a robot.");
        return;
    }
    
    // Reset counter on successful verification
    failedAttempts = 0;
    authenticateUser();
}
```

### Example 4: Logging Verification Attempts
```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VerificationLogger {
    private static final String LOG_FILE = "verification_log.txt";
    
    public static void logVerificationAttempt(String email, boolean success) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String status = success ? "SUCCESS" : "FAILED";
        String logEntry = String.format("[%s] User: %s | Status: %s\n", 
                                        timestamp, email, status);
        
        // Write to log file
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }
}

// Usage in AuthController:
if (recaptchaCheckBox.isVerified()) {
    VerificationLogger.logVerificationAttempt(email, true);
    // Continue login...
} else {
    VerificationLogger.logVerificationAttempt(email, false);
    // Show error...
}
```

---

## Integration Examples

### Example 1: With Forgot Password
```java
@FXML
void onForgotPassword(ActionEvent event) {
    // Require reCAPTCHA verification first
    if (!recaptchaCheckBox.isVerified()) {
        showError("Security Check Required", 
                  "Please verify that you are not a robot first.");
        return;
    }
    
    // Reset reCAPTCHA for next attempt
    recaptchaCheckBox.reset();
    
    // Proceed with password recovery...
}
```

### Example 2: With Remember Me
```java
@FXML
void onSignIn(ActionEvent event) {
    String email = emailField.getText().trim();
    String password = passwordField.getText();
    boolean rememberMe = rememberMeCheckBox.isSelected();
    
    // Verify reCAPTCHA first
    if (!recaptchaCheckBox.isVerified()) {
        showError("Verification Failed", 
                  "Please verify that you are not a robot.");
        return;
    }
    
    // Authenticate user
    User user = userService.authenticate(email, password);
    
    if (user != null) {
        // Save credentials if Remember Me is checked
        CredentialManager.saveCredentials(email, password, rememberMe);
        
        // On next login, both Remember Me will be checked
        // AND reCAPTCHA will still be required
        showSuccess("Login Successful", "Welcome " + user.getUsername());
        RankUpApp.showBase();
    }
}
```

### Example 3: Error Recovery
```java
@FXML
void onSignIn(ActionEvent event) {
    String email = emailField.getText().trim();
    String password = passwordField.getText();
    
    if (email.isBlank() || password.isBlank()) {
        showError("Validation Error", 
                  "Email and password are required.");
        return;
    }
    
    // Verify reCAPTCHA
    if (!recaptchaCheckBox.isVerified()) {
        showError("Verification Failed", 
                  "Please verify that you are not a robot.");
        // Don't reset, let user retry
        return;
    }
    
    try {
        User user = userService.authenticate(email, password);
        
        if (user != null) {
            // Reset reCAPTCHA for next login
            recaptchaCheckBox.reset();
            showSuccess("Login Successful", "Welcome " + user.getUsername());
            RankUpApp.showBase();
        } else {
            // Reset for retry
            recaptchaCheckBox.reset();
            showError("Invalid Credentials", 
                      "Email or password incorrect. Please try again.");
        }
    } catch (SQLException e) {
        recaptchaCheckBox.reset();
        showError("Database Error", 
                  "Error accessing database: " + e.getMessage());
    }
}
```

---

## Testing Examples

### Example 1: Unit Test
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecaptchaUtilTest {
    
    @Test
    public void testRecaptchaLoadsProperties() {
        String siteKey = RecaptchaUtil.getSiteKey();
        assertNotNull(siteKey);
        assertFalse(siteKey.isEmpty());
    }
    
    @Test
    public void testInvalidTokenRetursFalse() {
        boolean result = RecaptchaUtil.verifyToken("invalid_token");
        assertFalse(result);
    }
    
    @Test
    public void testNullTokenReturnsFalse() {
        boolean result = RecaptchaUtil.verifyToken(null);
        assertFalse(result);
    }
}
```

### Example 2: Integration Test
```java
public class AuthControllerIntegrationTest {
    
    @Test
    public void testLoginWithVerifiedRecaptcha() {
        // Simulate user interaction
        recaptchaCheckBox.select(); // Simulates checkbox click
        
        // Get token
        String token = recaptchaCheckBox.getToken();
        assertNotNull(token);
        
        // Verify token
        boolean verified = RecaptchaUtil.verifyToken(token);
        assertTrue(verified);
    }
    
    @Test
    public void testLoginWithoutRecaptcha() {
        // Don't select checkbox
        boolean verified = recaptchaCheckBox.isVerified();
        assertFalse(verified);
        
        // Should fail login
        assertFalse(canProceedWithLogin());
    }
}
```

---

## Styling Examples

### Example 1: Custom CSS for reCAPTCHA
```css
/* login-style.css */

.recaptcha-container {
    -fx-border-color: #3498db;
    -fx-border-width: 2;
    -fx-padding: 15;
    -fx-border-radius: 8;
    -fx-background-color: #f8f9fa;
}

.recaptcha-checkbox:selected {
    -fx-text-fill: #27ae60;
}

.recaptcha-checkbox:hover {
    -fx-opacity: 0.8;
}
```

### Example 2: Dark Theme Support
```java
public class RecaptchaCheckBox extends VBox {
    
    public enum Theme { LIGHT, DARK }
    
    public void setTheme(Theme theme) {
        if (theme == Theme.DARK) {
            this.setStyle("-fx-border-color: #444; -fx-background-color: #222;");
            // ... adjust text colors for dark theme
        } else {
            this.setStyle("-fx-border-color: #d3d3d3; -fx-background-color: #fff;");
        }
    }
}
```

---

## Error Handling Examples

### Example 1: Detailed Error Messages
```java
public class RecaptchaErrorHandler {
    
    public static String getErrorMessage(String errorCode) {
        return switch(errorCode) {
            case "missing-input-secret" -> 
                "Server configuration error. Please try again.";
            case "invalid-input-secret" -> 
                "Server configuration error. Please try again.";
            case "missing-input-response" -> 
                "Please verify that you are not a robot.";
            case "invalid-input-response" -> 
                "Verification failed. Please try again.";
            case "bad-request" -> 
                "Invalid verification request. Please try again.";
            case "timeout-or-duplicate" -> 
                "Verification timed out. Please try again.";
            default -> "An error occurred during verification. Please try again.";
        };
    }
}
```

### Example 2: Fallback Mechanism
```java
@FXML
void onSignIn(ActionEvent event) {
    // Try to verify reCAPTCHA
    try {
        if (!recaptchaCheckBox.isVerified()) {
            showError("Verification Required", 
                      "Please verify that you are not a robot.");
            return;
        }
    } catch (Exception e) {
        // Fallback: If reCAPTCHA service is down, 
        // require additional verification
        System.err.println("reCAPTCHA service error: " + e.getMessage());
        
        // Option 1: Require 2FA as backup
        if (!requireAdditional2FA()) {
            showError("Security Check Failed", 
                      "Please enable 2FA and try again.");
            return;
        }
        
        // Option 2: Manual review required
        showInfo("Pending Review", 
                 "Your login will be reviewed by our team.");
        return;
    }
    
    // Continue with login...
}
```

---

## Advanced Examples

### Example 1: Analytics Integration
```java
public class RecaptchaAnalytics {
    
    private int totalAttempts = 0;
    private int successfulVerifications = 0;
    private int failedVerifications = 0;
    
    public void recordAttempt(boolean success) {
        totalAttempts++;
        if (success) {
            successfulVerifications++;
        } else {
            failedVerifications++;
        }
    }
    
    public double getSuccessRate() {
        if (totalAttempts == 0) return 0;
        return (double) successfulVerifications / totalAttempts * 100;
    }
    
    public void printStatistics() {
        System.out.println("reCAPTCHA Statistics:");
        System.out.println("Total Attempts: " + totalAttempts);
        System.out.println("Success Rate: " + getSuccessRate() + "%");
    }
}
```

### Example 2: Custom Challenge Response
```java
public class AdvancedRecaptchaCheckBox extends RecaptchaCheckBox {
    
    private List<String> challengeQuestions = Arrays.asList(
        "What is 2 + 2?",
        "What color is the sky?",
        "How many legs does a cat have?"
    );
    
    public boolean showCustomChallenge() {
        String question = challengeQuestions.get(
            new Random().nextInt(challengeQuestions.size())
        );
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Additional Verification");
        dialog.setContentText(question);
        
        Optional<String> result = dialog.showAndWait();
        return result.isPresent() && validateAnswer(question, result.get());
    }
    
    private boolean validateAnswer(String question, String answer) {
        // Validate answer logic
        return true;
    }
}
```

---

## Deployment Examples

### Example 1: Development Environment Setup
```bash
#!/bin/bash
# setup-dev.sh

# Install dependencies
mvn clean install

# Run tests
mvn test

# Start in development mode
mvn spring-boot:run
```

### Example 2: Production Environment Setup
```bash
#!/bin/bash
# setup-production.sh

# Build production package
mvn clean package -DskipTests

# Update recaptcha.properties with production keys
sed -i 's/recaptcha.site.key=.*/recaptcha.site.key=YOUR_PROD_SITE_KEY/' \
    recaptcha.properties
sed -i 's/recaptcha.secret.key=.*/recaptcha.secret.key=YOUR_PROD_SECRET_KEY/' \
    recaptcha.properties

# Deploy
java -jar target/rankup-1.0-SNAPSHOT.jar
```

---

## Common Patterns

### Pattern 1: Login with Auto-Retry
```java
int maxRetries = 3;
int retryCount = 0;

while (retryCount < maxRetries) {
    try {
        if (!recaptchaCheckBox.isVerified()) {
            retryCount++;
            if (retryCount >= maxRetries) {
                showError("Max Attempts", 
                          "Too many verification failures.");
                return;
            }
            showError("Try Again", 
                      "Please verify. Attempt " + retryCount + "/" + maxRetries);
            continue;
        }
        
        authenticateUser();
        break;
    } catch (Exception e) {
        retryCount++;
        System.err.println("Login attempt failed: " + e.getMessage());
    }
}
```

### Pattern 2: Conditional reCAPTCHA
```java
// Only require reCAPTCHA for suspicious logins
public boolean shouldRequireRecaptcha(String email, String ipAddress) {
    // Check if IP is suspicious
    if (isSuspiciousIP(ipAddress)) return true;
    
    // Check if login at unusual time
    if (isUnusualTime()) return true;
    
    // Check if multiple failed attempts
    if (getFailedAttempts(email) > 3) return true;
    
    return false;
}

@FXML
void onSignIn(ActionEvent event) {
    if (shouldRequireRecaptcha(email, getClientIP())) {
        if (!recaptchaCheckBox.isVerified()) {
            showError("Verification Required", 
                      "Additional verification needed.");
            return;
        }
    }
    
    authenticateUser();
}
```

---

## Reference Links

- [Google reCAPTCHA Admin Console](https://www.google.com/recaptcha/admin)
- [reCAPTCHA v2 Documentation](https://developers.google.com/recaptcha/docs/v2)
- [Server-Side Verification API](https://www.google.com/recaptcha/api/siteverify)
- [reCAPTCHA FAQ](https://www.google.com/recaptcha/about/)

---

This document provides quick reference examples for common reCAPTCHA implementation patterns and use cases.

