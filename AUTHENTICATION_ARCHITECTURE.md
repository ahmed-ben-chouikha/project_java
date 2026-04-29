# 🔐 Authentication System - Visual Architecture

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        RankUp Application                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Login Screen (FXML)                        │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ Email: [____________________]                              │ │
│  │ Password: [____________________]                           │ │
│  │ [Sign In]  [Forgot Password]  [Sign Up]                   │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
                    AuthController.onSignIn()
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Input Validation                               │
│  - Check email not empty                                        │
│  - Check password not empty                                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                  UserService.authenticate()                      │
│  - Hash input password with SHA-256                             │
│  - Query database for user with email                           │
│  - Compare password hashes                                      │
│  - Check user status = 'active'                                 │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
                    ▼                   ▼
              [Match Found]        [No Match]
                    │                   │
                    ▼                   ▼
        ┌──────────────────┐  ┌──────────────────┐
        │ Create User Obj  │  │ Show Error Alert │
        │ - username       │  │ "Invalid email   │
        │ - email          │  │  or password"    │
        │ - role           │  └──────────────────┘
        │ - id             │          │
        └──────────────────┘          │
                │                     │
                ▼                     ▼
        ┌──────────────────┐  [Return to Login]
        │ Store in Session │
        │ (SessionManager) │
        │ - playerName     │
        │ - email          │
        │ - role           │
        │ - userId         │
        └──────────────────┘
                │
                ▼
        ┌──────────────────┐
        │ RankUpApp        │
        │ .showBase()      │
        └──────────────────┘
                │
                ▼
        ┌──────────────────────────┐
        │  Base View with TopNav    │
        │  & Dashboard loaded       │
        └──────────────────────────┘
                │
                ▼
        TopNavController.initialize()
        - Update button text with username (role)
        - Add Admin Panel if role == "admin"
```

---

## Authentication Flow (Detailed)

```
START: User Launches App
│
├─ No session data
│  └─ Show Login Screen
│
LOGIN ATTEMPT
│
├─ User enters credentials
│  ├─ Email: admin@esports.com
│  └─ Password: admin123
│
├─ AuthController.onSignIn() called
│  │
│  ├─ Validate input (not empty) ✓
│  │
│  └─ UserService.authenticate(email, password)
│     │
│     ├─ Query database:
│     │  SELECT id, email, username, password, role, status
│     │  FROM users
│     │  WHERE email = 'admin@esports.com' AND status = 'active'
│     │
│     ├─ Database returns user record
│     │
│     ├─ Verify password:
│     │  input_hash = SHA256("admin123")
│     │  stored_hash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"
│     │  input_hash == stored_hash? ✓ YES
│     │
│     └─ Return User object with all details
│
├─ Session Manager stores user data
│  ├─ currentPlayerName = "admin"
│  ├─ currentEmail = "admin@esports.com"
│  ├─ currentRole = "admin"
│  └─ currentUserId = 1
│
├─ RankUpApp.showBase() loads dashboard
│
├─ TopNavController.initialize() updates UI
│  │
│  ├─ Check current role
│  │  └─ role == "admin"? YES
│  │
│  ├─ Update button text
│  │  └─ accountMenuButton.setText("admin (admin)")
│  │
│  └─ Add Admin Panel menu item
│     ├─ Create MenuItem("Admin Panel")
│     ├─ Set action to onAdminPanel()
│     └─ Add to menu at position 0
│
└─ Dashboard displayed with role-aware UI
```

---

## User Interface State Diagrams

### Admin User - Top Navigation

```
Before Login:
┌─────────────────────────────────────────────────┐
│ [RankUp ESPORTS HUB] [Search...] ● Live [Account ▼]
└─────────────────────────────────────────────────┘
                                          Default menu

After Admin Login:
┌─────────────────────────────────────────────────┐
│ [RankUp ESPORTS HUB] [Search...] ● Live [admin (admin) ▼]
└─────────────────────────────────────────────────┘
                                    ▼
                            ┌─────────────────┐
                            │ Admin Panel     │
                            │ ─────────────── │
                            │ My Profile      │
                            │ Settings        │
                            │ Logout          │
                            └─────────────────┘
```

### Player User - Top Navigation

```
After Player Login:
┌─────────────────────────────────────────────────┐
│ [RankUp ESPORTS HUB] [Search...] ● Live [player1 (player) ▼]
└─────────────────────────────────────────────────┘
                                    ▼
                            ┌─────────────────┐
                            │ My Profile      │
                            │ Settings        │
                            │ Logout          │
                            └─────────────────┘
                     (No Admin Panel visible!)
```

---

## Database Query Flow

```
┌─────────────────────────────────────────────┐
│  UserService.authenticate() Method          │
└─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│  Input Parameters:                           │
│  - email: "admin@esports.com"               │
│  - password: "admin123"                     │
└─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│  Step 1: Hash Password                      │
│  SHA256("admin123") =                       │
│  "240be518fabd2724ddb6f04eeb1da5967448d7..." │
└─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│  Step 2: Database Query                     │
│  SELECT id, email, username, password,      │
│         role, status                        │
│  FROM users                                 │
│  WHERE email = 'admin@esports.com'          │
│    AND status = 'active'                    │
└─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│  Step 3: Compare Hashes                     │
│  Input Hash:  240be518fabd2724ddb6f04eb...  │
│  Stored Hash: 240be518fabd2724ddb6f04eb...  │
│  Match? ✓ YES                               │
└─────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────┐
│  Step 4: Create User Object                 │
│  User {                                      │
│    id: 1,                                   │
│    email: "admin@esports.com",              │
│    username: "admin",                       │
│    role: "admin",                           │
│    status: "active"                         │
│  }                                          │
└─────────────────────────────────────────────┘
              │
              ▼
     Return User Object
```

---

## Session State Lifecycle

```
┌──────────────────────────────────────────┐
│  Initial State (No Login)                 │
│  SessionManager:                          │
│  - currentPlayerName: "DefaultPlayer"    │
│  - currentUserId: -1                     │
│  - currentRole: "player"                 │
│  - currentEmail: ""                      │
└──────────────────────────────────────────┘
              │
         [Login as admin]
              │
              ▼
┌──────────────────────────────────────────┐
│  After Successful Admin Login             │
│  SessionManager:                          │
│  - currentPlayerName: "admin"            │
│  - currentUserId: 1                      │
│  - currentRole: "admin"                  │
│  - currentEmail: "admin@esports.com"     │
└──────────────────────────────────────────┘
              │
        [User Navigates App]
              │
              ▼
┌──────────────────────────────────────────┐
│  During Navigation (Session Active)       │
│  - TopNav shows: "admin (admin)"         │
│  - Admin features accessible             │
│  - User ID available for queries         │
│  - Session persists in memory            │
└──────────────────────────────────────────┘
              │
           [Logout]
              │
              ▼
┌──────────────────────────────────────────┐
│  After Logout                             │
│  SessionManager.clear() called:           │
│  - currentPlayerName: "DefaultPlayer"    │
│  - currentUserId: -1                     │
│  - currentRole: "player"                 │
│  - currentEmail: ""                      │
└──────────────────────────────────────────┘
              │
         Return to Login
```

---

## Role-Based Access Control (RBAC)

```
┌─────────────────────────────────────────────────────────┐
│  RBAC Decision Tree                                     │
└─────────────────────────────────────────────────────────┘

User Logged In?
├─ NO → [Show Login Screen]
│
└─ YES → Check Role
   │
   ├─ role == "admin"?
   │  │
   │  ├─ YES → [Show Admin Menu Items]
   │  │       - Admin Panel ✓
   │  │       - My Profile ✓
   │  │       - Settings ✓
   │  │       - Logout ✓
   │  │
   │  └─ NO → Continue to Player Check
   │
   └─ role == "player"?
      │
      ├─ YES → [Show Player Menu Items]
      │       - Admin Panel ✗ (Not shown)
      │       - My Profile ✓
      │       - Settings ✓
      │       - Logout ✓
      │
      └─ NO → [Default to Player]
```

---

## Data Flow Diagram

```
┌─────────────┐
│  User Input │ (email, password)
└──────┬──────┘
       │
       ▼
┌────────────────────┐
│ AuthController     │ Validation & Entry Point
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ UserService        │ Business Logic
│ .authenticate()    │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ MyConnection       │ Database Connection
│ .getInstance()     │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ Database (MySQL)   │ Persistent Storage
│ users table        │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ User Record        │ Retrieved Data
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ SessionManager     │ Session Storage
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ TopNavController   │ UI Updates
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│ UI Components      │ Displayed to User
│ (TopNav, Menu)     │
└────────────────────┘
```

---

## Component Interaction Diagram

```
     ┌──────────────────────────────────────────────────┐
     │              RankUpApp (Singleton)                │
     │  - Manages scene transitions                     │
     │  - Coordinates session storage                   │
     └──────────────────────────────────────────────────┘
                 △                      △
                 │                      │
                 │                      │
     ┌───────────┴────┐        ┌────────┴────────┐
     │                │        │                 │
     ▼                ▼        ▼                 ▼
┌─────────────┐  ┌──────────────────┐  ┌────────────────┐
│AuthControl​ler│  │TopNavController  │  │SessionManager  │
│             │  │                  │  │                │
│ - onSignIn()│  │ - initialize()   │  │ - setRole()    │
│             │  │ - updateNav()    │  │ - setEmail()   │
└──────┬──────┘  └────────┬─────────┘  └────────┬───────┘
       │                  │                      │
       │                  │                      │
       └──────────────────┼──────────────────────┘
                          │
                          ▼
                  ┌───────────────┐
                  │ UserService   │
                  │               │
                  │authenticate() │
                  │createUser()   │
                  │updateUser()   │
                  └────────┬──────┘
                           │
                           ▼
                  ┌───────────────────┐
                  │ MyConnection      │
                  │ (Database Access) │
                  └────────┬──────────┘
                           │
                           ▼
                  ┌───────────────────┐
                  │ MySQL Database    │
                  │ (users table)     │
                  └───────────────────┘
```

---

## Password Verification Process

```
┌────────────────────────────────────────┐
│  User enters password: "admin123"       │
└────────────────────────────────────────┘
              │
              ▼
┌────────────────────────────────────────┐
│  SHA256 Hash Algorithm                  │
│  Input: "admin123"                      │
└────────────────────────────────────────┘
              │
              ▼
┌────────────────────────────────────────────────────────┐
│  Generated Hash:                                        │
│  240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa... │
└────────────────────────────────────────────────────────┘
              │
              ▼
┌────────────────────────────────────────────────────────┐
│  Compare with Database Hash:                           │
│  240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa... │
└────────────────────────────────────────────────────────┘
              │
              ▼
┌────────────────────────────────────────┐
│  Match?                                 │
│  ✓ YES → Authentication Success         │
│  ✗ NO  → Authentication Failed          │
└────────────────────────────────────────┘
```

---

## Class Relationship Diagram

```
┌──────────────┐
│    User      │ (Entity)
├──────────────┤
│ - id: int    │
│ - email      │
│ - password   │
│ - username   │
│ - role       │
│ - status     │
└──────┬───────┘
       │ used by
       │
┌──────▼──────────────┐
│  UserService        │ (Service/DAO)
├─────────────────────┤
│ authenticate()      │
│ getUserById()       │
│ createUser()        │
│ updateUser()        │
│ changePassword()    │
│ deleteUser()        │
│ emailExists()       │
└──────┬──────────────┘
       │ uses
       │
┌──────▼──────────────┐
│ MyConnection        │ (DB Connection)
├─────────────────────┤
│ getInstance()       │
│ getCnx()            │
└─────────────────────┘
       │ connects to
       │
┌──────▼──────────────┐
│ MySQL Database      │ (Persistence)
├─────────────────────┤
│ users table         │
└─────────────────────┘
```

---

## Security Architecture

```
┌─────────────────────────────────────────────────────┐
│                Security Layers                       │
└─────────────────────────────────────────────────────┘

Layer 1: Input Validation
├─ Email not empty ✓
├─ Password not empty ✓
└─ Format validation (future enhancement)

Layer 2: Database Access
├─ PreparedStatements (SQL injection prevention) ✓
├─ User status check (active/inactive) ✓
└─ Indexed queries for performance ✓

Layer 3: Password Security
├─ Never stored in plaintext ✓
├─ SHA-256 hashing ✓
├─ Salting (future: BCrypt adds this) 🔄
└─ Hash comparison (not string comparison) ✓

Layer 4: Session Security
├─ Session stored in application memory ✓
├─ No session tokens exposed ✓
├─ Clear on logout ✓
└─ Ready for: HttpSession, JWT, etc. 🔄

Layer 5: Access Control
├─ Role-based authorization ✓
├─ Admin-only features enforced ✓
├─ UI-level restrictions ✓
└─ Backend validation (future enhancement) 🔄

Legend: ✓ Implemented  🔄 Planned
```

---

Generated: 2026-04-29
Purpose: Visual Reference for Authentication Architecture

