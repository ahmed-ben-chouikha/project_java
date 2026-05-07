# reCAPTCHA Quiz-Based Implementation

## 🎯 What's New

Your reCAPTCHA is now **quiz-based** instead of just a simple checkbox! When users click "I'm not a robot", they'll see actual challenges to solve.

---

## 🧩 Quiz Types

### 1. Multiple Choice Quiz
```
Question: "What is the capital of France?"
Options:
  ○ Paris
  ○ London
  ○ Berlin
  ○ Madrid

User selects answer → Click Verify → ✓ Verified
```

**Examples of questions:**
- What is the capital of France?
- Which planet is closest to the Sun?
- What is 7 + 5?
- Who wrote 'Romeo and Juliet'?
- What is the largest ocean on Earth?

### 2. Text Input Quiz
```
Question: "What color is an apple (commonly)?"
Input: [red________________]
        [Verify]

User enters answer → Click Verify → ✓ Verified
```

**Examples of questions:**
- What is the color of an apple (commonly)?
- How many fingers do humans have?
- What animal barks?
- What is H2O?
- How many legs does a spider have?

### 3. Image Grid Selection
```
Question: "Select all images with cars:"

┌──────┬──────┬──────┐
│ 🚗   │ 🐱   │ 🚗   │
├──────┼──────┼──────┤
│ 🌳   │ 🚗   │ 🏠   │
├──────┼──────┼──────┤
│ 🌲   │ 🚗   │ 🌼   │
└──────┴──────┴──────┘

User checks matching images → Click Verify → ✓ Verified
```

**Examples:**
- Select all images with cars
- Select all images with trees
- Select all images with animals
- Select all images with flowers

---

## 📊 How It Works

```
User clicks "I'm not a robot" checkbox
            ↓
Random quiz type selected (Multiple Choice, Text, or Grid)
            ↓
Quiz question displayed with options/input field
            ↓
User solves the challenge
            ↓
User clicks "Verify" button
            ↓
Answer validated:
  ✓ Correct → Show success, token generated, proceed to login
  ✗ Incorrect → Show error, user can retry
```

---

## 🔧 Files Updated

### Modified
- `RecaptchaCheckBox.java` - Now shows quiz challenges instead of just checkbox
- `AuthController.java` - No changes needed, works with existing code

### New
- `RecaptchaQuiz.java` - Generates random quiz questions and validates answers

---

## 🚀 Features

### Quiz Generation
✅ Randomly selects quiz type each time
✅ Random question from the pool
✅ Case-insensitive answer validation
✅ Multiple correct answer formats supported

### User Experience
✅ Simple and intuitive interface
✅ Clear instructions
✅ Real-time feedback
✅ Can retry after failure
✅ Success message when passed

### Security
✅ Prevents automated attacks (bot detection)
✅ Requires human interaction
✅ Anti-pattern: varies questions to prevent automation
✅ Works with server-side verification

---

## 💡 Customization

### Add More Questions

Edit `RecaptchaQuiz.java`:

#### For Multiple Choice:
```java
private void generateMultipleChoiceQuiz() {
    String[] questions = {
        "What is the capital of France?",
        "Which planet is closest to the Sun?",
        // ADD YOUR QUESTIONS HERE
        "What is your favorite color?"
    };

    Map<String, List<String>> qAndA = new HashMap<>();
    // ...existing code...
    qAndA.put("What is your favorite color?", 
              Arrays.asList("Blue", "Red", "Green", "Yellow"));
    
    Map<String, String> answers = new HashMap<>();
    // ...existing code...
    answers.put("What is your favorite color?", "Blue");
}
```

#### For Text Input:
```java
private void generateTextInputQuiz() {
    Map<String, String> qAndA = new LinkedHashMap<>();
    // ...existing code...
    qAndA.put("What is the capital of Spain?", "madrid");
}
```

#### For Image Grid:
```java
private void generateGridSelectQuiz() {
    Map<String, List<String>> imageQuizzes = new LinkedHashMap<>();
    // ...existing code...
    imageQuizzes.put("Select all images with dogs:",
        Arrays.asList("🐕", "🌳", "🐕", "🚗", "🏠", "🐕", "🌼", "🐱", "🐕"));
}
```

### Change Quiz Difficulty

Create difficulty levels:

```java
public enum QuizDifficulty {
    EASY,
    MEDIUM,
    HARD
}

public RecaptchaQuiz(QuizDifficulty difficulty) {
    this.difficulty = difficulty;
    // Generate questions based on difficulty
}
```

### Add Language Support

```java
private void generateMultipleChoiceQuiz(Language lang) {
    switch(lang) {
        case ENGLISH:
            // English questions
            break;
        case FRENCH:
            // Questions en français
            break;
        case SPANISH:
            // Preguntas en español
            break;
    }
}
```

---

## 🎓 Code Example

### Using the Quiz Component

```java
// The component is automatically created in AuthController
RecaptchaCheckBox recaptchaCheckBox = new RecaptchaCheckBox();

// When user clicks "I'm not a robot"
// → Quiz automatically displays

// When user solves and clicks Verify
// → Answer is validated
// → Token is generated

// Check if verified
if (recaptchaCheckBox.isVerified()) {
    // Proceed with login
}

// Get token
String token = recaptchaCheckBox.getToken();
```

---

## 🧪 Testing

### Test Multiple Choice
1. Click "I'm not a robot" checkbox
2. See question with 4 options
3. Select correct answer
4. Click Verify
5. Should see ✓ Verified message

### Test Text Input
1. Click "I'm not a robot" checkbox
2. See text input field
3. Enter the correct answer
4. Click Verify
5. Should see ✓ Verified message

### Test Image Selection
1. Click "I'm not a robot" checkbox
2. See 9 image grid
3. Select all matching images
4. Click Verify
5. Should see ✓ Verified message

### Test Error Handling
1. Click "I'm not a robot" checkbox
2. Select WRONG answer
3. Click Verify
4. Should see error message
5. Can try again

---

## 🔐 Security Considerations

### Strengths
✅ Requires human interaction and cognitive ability
✅ Prevents automated account takeover
✅ Each attempt has random questions
✅ Difficult to automate without AI
✅ Works offline (no Google API required for quiz)

### Current Limitations
⚠️ Not as advanced as Google's reCAPTCHA v3
⚠️ Can be solved by users but also by AI in future
⚠️ Limited question pool (can be expanded)

### Future Enhancements
- [ ] Integrate actual Google reCAPTCHA API alongside quiz
- [ ] Add difficulty levels based on previous attempts
- [ ] Add image CAPTCHA with distorted text
- [ ] Add audio CAPTCHA for accessibility
- [ ] Track failed attempts for security alerts
- [ ] Add machine learning to detect patterns

---

## 🎨 UI Customization

### Change Colors

In `RecaptchaCheckBox.java`:

```java
// Success message styling
successBox.setStyle("-fx-background-color: #YOUR_COLOR; " +
                    "-fx-border-color: #YOUR_BORDER_COLOR;");

// Success text color
successLabel.setStyle("-fx-text-fill: #YOUR_TEXT_COLOR;");
```

### Change Quiz Layout

```java
// Current: 3x3 grid for images
int col = 0;
int row = 0;
// ...
if (col >= 3) {  // 3 columns
    col = 0;
    row++;
}

// Change to 2x4 grid:
if (col >= 2) {  // 2 columns
    col = 0;
    row++;
}

// Change to 4x2 grid:
if (col >= 4) {  // 4 columns
    col = 0;
    row++;
}
```

---

## 📈 Statistics & Monitoring

### Track Attempts

```java
public class QuizStatistics {
    private int totalAttempts = 0;
    private int successfulAttempts = 0;
    private int failedAttempts = 0;
    
    public void recordAttempt(boolean success) {
        totalAttempts++;
        if (success) {
            successfulAttempts++;
        } else {
            failedAttempts++;
        }
    }
    
    public double getSuccessRate() {
        return (double) successfulAttempts / totalAttempts * 100;
    }
}
```

### Log Quiz Results

```java
private void logQuizResult(String email, boolean passed) {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    String status = passed ? "PASSED" : "FAILED";
    String log = String.format("[%s] Email: %s | Quiz: %s\n", 
                               timestamp, email, status);
    // Write to log file
}
```

---

## 🌍 Multi-Language Support

### Add Spanish Version

```java
// In RecaptchaQuiz.java
private Locale currentLocale = Locale.ENGLISH;

public void setLocale(Locale locale) {
    this.currentLocale = locale;
}

private void generateMultipleChoiceQuiz() {
    String[] questionsES = {
        "¿Cuál es la capital de Francia?",
        "¿Cuál es el planeta más cercano al Sol?",
        // ...
    };
    
    if (currentLocale.getLanguage().equals("es")) {
        question = questionsES[random.nextInt(questionsES.length)];
    }
}
```

---

## 🆘 Troubleshooting

| Issue | Solution |
|-------|----------|
| Quiz not appearing | Ensure RecaptchaQuiz.java is in classpath |
| Answer validation fails | Check answer spelling matches expected value |
| Grid images not showing | Verify emoji support in your system |
| Wrong answer doesn't show error | Check if error dialog is being blocked |

---

## 📚 Related Files

- `RecaptchaCheckBox.java` - Main UI component
- `RecaptchaQuiz.java` - Quiz generation and validation
- `AuthController.java` - Integration with login
- `login.fxml` - FXML layout (unchanged)

---

## 🚀 Deployment Notes

When deploying to production:

1. **Expand Question Pool** - Add more questions to prevent recognition
2. **Monitor Success Rates** - If too high/low, adjust difficulty
3. **Log Failed Attempts** - Track suspicious patterns
4. **Consider Hybrid** - Use quiz + Google reCAPTCHA together
5. **Accessibility** - Ensure alt-text for images

---

## 🎉 Summary

Your reCAPTCHA is now much more secure and engaging:
- ✅ Shows actual challenges instead of just a checkbox
- ✅ Three different quiz types for variety
- ✅ Easy to customize with more questions
- ✅ Protects against automated attacks
- ✅ User-friendly interface
- ✅ Fully integrated with login

Ready to use! 🚀

