# ✅ RECLAMATION TITRE VALIDATION - QUICK REFERENCE

## 🎯 New Validation Rules

**Reclamation Title (Titre) must:**
- ✅ Be **at least 5 characters** long
- ✅ Contain **at least one alphabetic character** (not only numbers)
- ✅ Not exceed **255 characters**
- ✅ Not be **empty or blank**

---

## 📋 Validation Messages

### Error Messages You'll See

| Condition | Error Message |
|-----------|---------------|
| Empty title | "Titre is required." |
| Less than 5 chars | "Titre must be at least 5 characters long." |
| Only numbers | "Titre must contain at least one alphabetic character (not only numbers)." |
| More than 255 chars | "Titre must not exceed 255 characters." |

---

## ✅ VALID Examples

```
✅ "Test Case"          (2 words, has letters)
✅ "Joueur agressif"    (French title, has letters)
✅ "Problème1"          (has letters and number)
✅ "Case123Test"        (5+ chars, has letters)
✅ "Comportement équipe" (multiple words with letters)
```

---

## ❌ INVALID Examples

```
❌ "" (empty)
❌ "Test" (only 4 chars)
❌ "12345" (only numbers)
❌ "123 456" (numbers with space, no letters)
❌ "!@#$%" (symbols, no letters)
```

---

## 🧪 How to Test

### Test 1: Leave empty
```
1. Leave Titre empty
2. Click Save
3. See: "Titre is required."
```

### Test 2: Enter too short
```
1. Type: "Test"
2. Click Save
3. See: "Titre must be at least 5 characters long."
```

### Test 3: Enter only numbers
```
1. Type: "12345"
2. Click Save
3. See: "Titre must contain at least one alphabetic character (not only numbers)."
```

### Test 4: Valid entry
```
1. Type: "Test Case"
2. Click Save
3. Success! Record created ✅
```

---

## 💡 User Tips

**Remember when filling Reclamation Title:**
1. At least 5 characters
2. Must include at least one letter
3. Can use numbers, but not alone
4. Can be up to 255 characters
5. Examples: "Player behavior", "Team dispute", "Match issue123"

---

**Quick Reference - Last Updated**: April 14, 2026
