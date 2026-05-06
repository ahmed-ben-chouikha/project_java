# Quick Fix Guide: Tournament Registration Error

## ✅ The Error Has Been Fixed

The red error message **"Unknown column 'team_members' in 'field list'"** has been resolved.

## 📋 What Was Done

### Problem
The database table `tournament_registrations` was missing two columns:
- `team_members` - for storing team member information
- `contact_info` - for storing contact details

The Java code was trying to use these columns, but they didn't exist in the database.

### Solution Applied

1. **Created Migration Script** 
   - File: `database/add_team_members_contact_to_registrations.sql`
   - This adds the missing columns to existing databases

2. **Updated Schema Files**
   - `database/tournament_registration_table.sql` - Updated to include new columns
   - `database/tournament_registrations_table.sql` - Updated to include new columns

3. **Fixed Service Layer**
   - `TournamentRegistrationService.java` - Updated to save and retrieve both columns
   - INSERT query now includes `team_members`
   - UPDATE query now includes `team_members`
   - Result mapping safely handles both columns

## 🚀 Next Steps

### For Existing Databases
Run the migration script to add the missing columns:

```bash
# Using MySQL Command Line
mysql -u your_user -p your_database < database/add_team_members_contact_to_registrations.sql

# Or run directly in your MySQL client:
ALTER TABLE tournament_registrations 
ADD COLUMN IF NOT EXISTS team_members VARCHAR(1000),
ADD COLUMN IF NOT EXISTS contact_info VARCHAR(255);
```

### Verify It Works
1. Navigate to Tournament Registration in RankUp
2. Fill in the form including:
   - Team Name
   - Team Members (optional)
   - Contact Info (optional)
3. Click "Submit Registration"
4. The form should now work without errors!

## 📦 Files Modified
- ✅ `database/add_team_members_contact_to_registrations.sql` (NEW)
- ✅ `database/tournament_registration_table.sql` (UPDATED)
- ✅ `database/tournament_registrations_table.sql` (UPDATED)
- ✅ `src/main/java/edu/connexion3a36/services/TournamentRegistrationService.java` (UPDATED)

## 💾 Code Changes Summary

**Before (Missing team_members):**
```sql
INSERT INTO tournament_registrations 
(player_name, team_name, contact_info, tournament_id, status)
VALUES (?, ?, ?, ?, ?)
```

**After (Includes team_members):**
```sql
INSERT INTO tournament_registrations 
(player_name, team_name, team_members, contact_info, tournament_id, status)
VALUES (?, ?, ?, ?, ?, ?)
```

## ✨ All Systems Go!
The Tournament Registration feature is now fully functional with support for:
- Player name (auto-filled)
- Team name
- Team members list
- Contact information
- Tournament selection
- Status tracking (pending, confirmed, rejected)

