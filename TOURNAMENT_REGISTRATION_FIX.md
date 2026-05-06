# Tournament Registration Error Fix

## Problem
The error "Unknown column 'team_members' in 'field list'" was appearing in the Tournament Registration form when trying to submit a registration.

## Root Cause
The database schema for the `tournament_registrations` table was missing two required columns:
- `team_members` - to store the list of team members
- `contact_info` - to store contact information

However, the Java entity, service layer, and controller were all expecting these columns to exist.

## Solution
The following changes have been made:

### 1. Database Schema Updates
Updated the table creation scripts to include the missing columns:
- **File**: `database/tournament_registration_table.sql`
- **File**: `database/tournament_registrations_table.sql`

Both files now include:
```sql
team_members VARCHAR(1000),
contact_info VARCHAR(255),
```

### 2. Migration Script
Created a migration script to add the missing columns to existing databases:
- **File**: `database/add_team_members_contact_to_registrations.sql`

### 3. Service Layer Fixes
Updated `TournamentRegistrationService.java`:
- **INSERT Query** (line 48-49): Added `team_members` and `contact_info` to the insert statement
- **UPDATE Query** (line 98): Added `team_members` to the update statement  
- **Result Mapping** (line 436-459): Updated to safely handle both columns that may or may not exist

### 4. Backward Compatibility
The service is designed to handle both old and new schema versions:
- The `mapResultSetToEntity` method uses try-catch blocks to gracefully handle missing columns
- Only the core required columns are checked during table resolution
- Existing databases without these columns will still work until the migration is applied

## How to Apply the Fix

### For New Installations
The updated schema files (`tournament_registration_table.sql` and `tournament_registrations_table.sql`) will automatically create the table with the missing columns.

### For Existing Installations
1. Open your MySQL client or GUI tool
2. Run the migration script: `database/add_team_members_contact_to_registrations.sql`
3. Or execute these commands directly:

```sql
ALTER TABLE tournament_registrations 
ADD COLUMN IF NOT EXISTS team_members VARCHAR(1000),
ADD COLUMN IF NOT EXISTS contact_info VARCHAR(255);
```

## Verification
After applying the fix, the Tournament Registration form should:
- Accept "Team Members" input
- Accept "Contact Info" input
- Successfully save the registration with all information stored in the database
- Display all fields when viewing past registrations

## Files Modified
1. `database/add_team_members_contact_to_registrations.sql` (NEW)
2. `database/tournament_registration_table.sql` (UPDATED)
3. `database/tournament_registrations_table.sql` (UPDATED)
4. `src/main/java/edu/connexion3a36/services/TournamentRegistrationService.java` (UPDATED)
5. `src/main/java/edu/connexion3a36/rankup/controllers/TournamentRegistrationUserController.java` (NO CHANGES NEEDED)
6. `src/main/java/edu/connexion3a36/entities/TournamentRegistration.java` (NO CHANGES NEEDED)

