# RankUp E-Sports Platform - Login & Authentication Guide

## Overview
The application now includes a complete authentication system with role-based access control (RBAC). Users must log in with valid credentials from the database, and the UI adapts based on their role (admin or player).

## Database Setup

### Step 1: Create Users Table
Run the SQL setup script in your MySQL/MariaDB database:

```bash
# Using MySQL command line
mysql -h 127.0.0.1 -u root -p esportdevvvvvv < database/setup_users.sql

# Or import through PhpMyAdmin:
# 1. Open PhpMyAdmin
# 2. Select the "esportdevvvvvv" database
# 3. Click "Import"
# 4. Choose the file: database/setup_users.sql
# 5. Click "Go"
```

### Step 2: Verify Users Table Created
```sql
-- Run this query in your database to verify:
SELECT * FROM users;

-- Should show 3 test users:
-- 1. admin@esports.com (admin role)
-- 2. player@esports.com (player role)
-- 3. ahmed@esports.com (player role)
```

## Test Credentials

After running the setup script, you can log in with these test accounts:

### Admin Account
- **Email:** `admin@esports.com`
- **Password:** `admin123`
- **Role:** Admin
- **Access:** Full application + Admin Panel

### Player Accounts
- **Email:** `player@esports.com`
- **Password:** `player123`
- **Role:** Player

OR

- **Email:** `ahmed@esports.com`
- **Password:** `ahmed123`
- **Role:** Player

## Login Process

1. **Start the application**
   ```bash
   cd C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36
   .\launch.ps1
   ```

2. **You'll see the Login Screen:**
   - Email field (required)
   - Password field (required)
   - Sign In button
   - Forgot Password & Sign Up links (placeholders)

3. **Enter credentials:**
   - Use one of the test accounts above
   - Click "Sign In"

4. **Authentication Flow:**
   - System queries the users table
   - Password is verified using SHA-256 hashing
   - If credentials match, user is authenticated
   - Session is created with user info (username, role, email, userId)
   - Dashboard loads based on role

## Role-Based UI Changes

### For Admin Users
The top navigation bar will show:
- Account Menu with username and "(admin)" badge
- **New:** "Admin Panel" option in the dropdown menu
- Sorting: Admin Panel appears first in the menu
- Access to all administrative features

### For Player Users
The top navigation bar will show:
- Account Menu with username and "(player)" badge
- Standard user options (My Profile, Settings, Logout)
- No Admin Panel access (access denied error if attempted)

## Key Features Implemented

### Authentication ✅
- Email + Password login validation
- Database password verification
- Session management
- Active/Inactive user status checking

### Session Management ✅
- User ID stored in session
- Username stored in session
- User role stored in session
- User email stored in session
- Session cleared on logout

### Role-Based Access Control ✅
- Different menu items based on role
- Admin Panel only visible to admins
- Access control checks on sensitive operations

## File Structure

```
src/main/java/edu/connexion3a36/
├── entities/
│   └── User.java                 (User entity with getters/setters)
├── services/
│   └── UserService.java          (Database authentication)
└── rankup/
    ├── app/
    │   ├── SessionManager.java   (Updated: stores email and userId)
    │   └── RankUpApp.java        (Updated: new methods for email/userId)
    └── controllers/
        └── TopNavController.java (Updated: role-based menu display)

database/
├── setup_users.sql               (Complete setup script with test data)
└── users_table.sql               (Table creation only)

resources/views/
├── auth/
│   └── login.fxml               (Login screen)
└── common/
    └── TopNavBar.fxml           (Updated: dynamic account menu)
```

## Password Management

### Hashing Algorithm
- **Current:** SHA-256 (for development)
- **Recommended for Production:** BCrypt or Argon2

### How Passwords Are Stored
1. User enters password in login form
2. Password is hashed using SHA-256
3. Hash is compared with hash stored in database
4. If match: authentication succeeds

### Creating New Users Programmatically
```java
User newUser = new User("newemail@example.com", "password123", "username", "player");
UserService userService = new UserService();
boolean success = userService.createUser(newUser);
```

## Troubleshooting

### "Invalid email or password" Error
- Verify email exists in users table: `SELECT * FROM users WHERE email = 'admin@esports.com';`
- Verify database connection is working
- Check that users table was created successfully

### "Database Error" Message
- Ensure database connection in DataBaseConnection is configured correctly
- Check MySQL/MariaDB is running
- Verify database credentials

### Admin Panel Not Appearing
- Log in with `admin@esports.com` account
- Verify the role in database is "admin": `SELECT * FROM users WHERE email = 'admin@esports.com';`

## Future Enhancements

- [ ] Implement BCrypt password hashing
- [ ] Add "Sign Up" functionality
- [ ] Add password reset via email
- [ ] Add two-factor authentication
- [ ] Add user account lockout after failed attempts
- [ ] Add user activity logging
- [ ] Add role management interface (admin-only)
- [ ] Add permission-based access control (not just role-based)

## Next Steps

1. Run the database setup script
2. Start the application
3. Log in with test credentials
4. Test the role-based UI changes
5. Navigate through the application as different roles

