# 🔑 WHERE TO PASTE YOUR API KEY - EXACT LOCATION

## The File You Need to Edit

```
C:\Users\DIDA\Desktop\esportsnew\project_java\src\main\java\edu\connexion3a36\rankup\config\AIConfig.java
```

---

## Open This File and Find This Line

**Look for:**
```java
public static final String API_KEY = "dUDzbw5v8KyT1ODzelryCH9FZlGq8yjM";
```

---

## Replace It With Your Key

### BEFORE:
```java
public static final String API_KEY = "YOUR_OPENAI_API_KEY_HERE";
```

### AFTER (with your real key):
```java
public static final String API_KEY = "sk-proj-abc123def456ghi789jkl";
```

---

## 📸 Visual Guide

```
┌─ AIConfig.java ─────────────────────────────────────────┐
│                                                          │
│  1  | package edu.connexion3a36.rankup.config;          │
│  2  |                                                    │
│  3  | public class AIConfig {                           │
│  4  |                                                    │
│  5  |     // ⚠️ PASTE YOUR OPENAI API KEY HERE ⚠️      │
│  6  |     public static final String API_KEY =          │
│  7  |         "YOUR_OPENAI_API_KEY_HERE";  ← REPLACE   │
│  8  |     ↑                                              │
│  9  |     └── THIS LINE! Paste key between quotes       │
│ 10  |                                                    │
│ 11  |     public static final String API_URL =          │
│ 12  |         "https://api.openai.com/v1/chat/...";    │
│    ...                                                  │
└─────────────────────────────────────────────────────────┘
```

---

## Step-by-Step

### Step 1: Open File
- File path: `AIConfig.java`
- In folder: `src/main/java/edu/connexion3a36/rankup/config/`

### Step 2: Find Line 7
```java
public static final String API_KEY = "YOUR_OPENAI_API_KEY_HERE";
```

### Step 3: Select the Text
Select: `YOUR_OPENAI_API_KEY_HERE`

### Step 4: Replace with Your Key
Delete the placeholder and paste your actual OpenAI API key

**Your key looks like:**
```
sk-proj-abc123def456ghi789jkl...
```

### Step 5: Save File

---

## Complete Example

### If Your Key is: `sk-proj-123456789`

**Your line should look like:**
```java
public static final String API_KEY = "sk-proj-123456789";
```

### Full context:
```java
public class AIConfig {
    
    // ⚠️ PASTE YOUR OPENAI API KEY HERE ⚠️
    // Example: public static final String API_KEY = "sk-proj-xxxxxxxxxxxx...";
    public static final String API_KEY = "sk-proj-123456789";  ← YOUR KEY HERE
    
    // OpenAI API endpoint
    public static final String API_URL = "https://api.openai.com/v1/chat/completions";
    
    // Model to use
    public static final String MODEL = "gpt-3.5-turbo";
```

---

## ✅ You'll Know It's Correct When:

1. Line 7 in AIConfig.java has your actual key
2. It starts with `sk-proj-`
3. No quotes inside the quotes
4. File is saved
5. Application compiles without errors

---

## 🚀 Then What?

After pasting the key:

1. Save the file
2. Run: `mvn clean compile`
3. Start the application
4. Ask the chatbot a question
5. Get AI responses!

---

## ⚠️ Security Reminder

- ✅ DO: Keep key private
- ✅ DO: Use .gitignore to never commit it
- ❌ DON'T: Share key with others
- ❌ DON'T: Commit key to git
- ❌ DON'T: Post in public forums

---

## Questions?

**File to edit:** `AIConfig.java`  
**Line number:** 7  
**Text to replace:** `YOUR_OPENAI_API_KEY_HERE`  
**Paste:** Your actual OpenAI API key (starts with sk-proj-)  
**Save:** Press Ctrl+S  
**Done!** ✅

