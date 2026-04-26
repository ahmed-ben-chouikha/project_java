# 📋 TITRE VALIDATION - QUICK REFERENCE CARD

## ✅ THE RULES

```
┌──────────────────────────────────────┐
│   RECLAMATION TITRE VALIDATION       │
├──────────────────────────────────────┤
│                                      │
│ 1. MIN LENGTH: 5 characters          │
│ 2. MAX LENGTH: 255 characters        │
│ 3. REQUIRED: At least 1 letter       │
│ 4. FORBIDDEN: Numbers-only           │
│                                      │
└──────────────────────────────────────┘
```

---

## 🎯 ERROR MESSAGES

| If You See | Problem | Fix |
|-----------|---------|-----|
| "Titre is required." | Left field empty | Type something |
| "Titre must be at least 5 characters long." | Too short | Add characters |
| "Titre must contain at least one alphabetic character (not only numbers)." | Only numbers | Add a letter |
| "Titre must not exceed 255 characters." | Too long | Remove some text |

---

## ✅ VALID EXAMPLES

```
✅ "Test Case"
✅ "Player Behavior"
✅ "Problème équipe"
✅ "Issue 123"
✅ "Match Cancelled - Very Important!"
```

---

## ❌ INVALID EXAMPLES

```
❌ "" (empty)
❌ "Test" (4 chars)
❌ "12345" (numbers only)
❌ "!@#$%" (no letters)
```

---

## 🧪 TEST IT

| Test | Input | Result |
|------|-------|--------|
| Empty | "" | ❌ Error: "required" |
| Short | "Test" | ❌ Error: "min 5 chars" |
| Numbers | "12345" | ❌ Error: "need alphabet" |
| Valid | "Test1" | ✅ Success |

---

**Date**: April 14, 2026 | **Status**: ✅ Ready
