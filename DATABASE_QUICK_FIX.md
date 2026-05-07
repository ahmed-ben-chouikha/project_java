# 🗄️ Database Setup - Quick Reference

## Problem
❌ Table `esportdevvvvvv.user` doesn't exist
❌ Application cannot connect to database

## Solution
✅ Run the database setup script

---

## ⚡ Quick Fix (3 Steps)

### Step 1: Double-Click to Run
```
C:\Users\ghass\OneDrive\Desktop\project_java\setup_database.bat
```
**That's it!** The script will set up everything automatically.

---

## 📋 What Gets Created

| Item | Details |
|------|---------|
| Database | `esportdevvvvvv` |
| User Table | `user` (for authentication) |
| Supporting Tables | team, tournament, match, etc. |
| Sample Users | 6 test accounts |
| Sample Teams | 3 sample teams |

---

## 🔓 Test Credentials

After setup, use these to login:

```
Email:    player1@rankup.gg
Password: password123
Username: falconx
```

Or any of these:
- admin@rankup.gg / admin
- player2@rankup.gg / vortex7
- player3@rankup.gg / phoenix99

---

## 🔧 Manual Alternative (If .bat doesn't work)

Open Command Prompt and run:

```bash
mysql -h localhost -u root < "C:\Users\ghass\OneDrive\Desktop\project_java\DATABASE_COMPLETE_SETUP.sql"
```

---

## ✅ Verify It Works

In Command Prompt:
```bash
mysql -h localhost -u root esportdevvvvvv -e "SELECT * FROM user;"
```

Should show your 6 test users ✓

---

## 🎯 Next Steps

1. Run the setup script
2. Start the application
3. Login with test credentials
4. Enjoy the chatbot feature! 💬

---

**Files Provided:**
- ✅ `setup_database.bat` - One-click setup
- ✅ `DATABASE_COMPLETE_SETUP.sql` - SQL script
- ✅ `DATABASE_SETUP_FIX_GUIDE.md` - Detailed guide

Ready to fix your database! 🚀

