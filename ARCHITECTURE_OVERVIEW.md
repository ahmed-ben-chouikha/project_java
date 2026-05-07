# Architecture Overview

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                     RankUp E-Sports Platform                     │
└─────────────────────────────────────────────────────────────────┘

                         ┌──────────────────┐
                         │   JavaFX UI      │
                         │   (Views/FXML)   │
                         └────────┬─────────┘
                                  │
                  ┌───────────────┼───────────────┐
                  │               │               │
        ┌─────────▼────────┐  ┌──▼──────────┐  ┌─▼────────────────┐
        │ AuthController   │  │ TopNav      │  │PlayerProfile     │
        │ (Login/Register) │  │ Controller  │  │Controller (NEW)  │
        └────────┬─────────┘  └──┬──────────┘  └─┬────────────────┘
                 │               │              │
                 └───────────────┼──────────────┘
                                 │
                    ┌────────────▼──────────────┐
                    │   SessionManager (NEW)    │
                    │  - currentPlayerName      │
                    │  - currentEmail           │
                    │  - currentRole            │
                    │  - currentUserId          │
                    └────────────┬──────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
    ┌───▼────────────┐   ┌──────▼──────────┐   ┌────────▼────────┐
    │  UserService   │   │  EmailService   │   │ JwtTokenGenerator│
    │  (Updated)     │   │  (Enhanced)     │   │  (NEW)           │
    └───┬────────────┘   └──────┬──────────┘   └────────┬────────┘
        │                      │                        │
        │         ┌────────────┼────────────┐           │
        │         │            │            │           │
        │    ┌────▼─────┐ ┌───▼──────┐ ┌──▼──────────┐ │
        │    │Database  │ │Gmail     │ │JWT Secret  │ │
        │    │(User)    │ │SMTP      │ │& Token     │ │
        │    └──────────┘ └──────────┘ └────────────┘ │
        │                                              │
        └──────────────────────────────────────────────┘


                    ┌─────────────────────────┐
                    │  REST API (NEW)         │
                    │  /api/auth/login        │
                    │  /api/auth/register     │
                    │  /api/auth/validate     │
                    │  /api/auth/forgot-pwd   │
                    │  /api/auth/verify-otp   │
                    │  /api/auth/reset-pwd    │
                    └─────────────────────────┘
                            │
                    ┌───────▼────────┐
                    │ External Apps  │
                    │ (JWT Token)    │
                    └────────────────┘
```

---

## Data Flow: User Login & Profile

```
1. USER LOGIN FLOW
══════════════════════════════════════════════════════════════

User Types Email & Password
        │
        ▼
AuthController.onSignIn()
        │
        ▼
UserService.authenticate(email, password)
        ├─ Check database for user
        ├─ Verify password (bcrypt)
        └─ Return User object
        │
        ▼
SessionManager.setCurrentPlayerName(username)
SessionManager.setCurrentEmail(email)
SessionManager.setCurrentRole(role)
        │
        ▼
RankUpApp.showBase()  ◄─ User logged in!
```

```
2. PROFILE DISPLAY FLOW (NEW)
══════════════════════════════════════════════════════════════

User Clicks "My Profile"
        │
        ▼
TopNavController.onMyProfile()
        │
        ▼
RankUpApp.loadInBase("/views/players/player-profile.fxml")
        │
        ▼
PlayerProfileController.initialize()  ◄─ NEW
        │
        ├─ RankUpApp.getCurrentPlayerName()
        ├─ SessionManager.getCurrentEmail()
        ├─ RankUpApp.getCurrentRole()
        └─ SessionManager.getCurrentUserId()
        │
        ▼
Update UI Labels
        ├─ userNameLabel.setText(username)
        ├─ userDetailsLabel.setText(email + role)
        └─ Load statistics (ready for database)
        │
        ▼
PROFILE DISPLAYED WITH CORRECT USER DATA ✅
```

```
3. FORGOT PASSWORD FLOW (UPDATED)
══════════════════════════════════════════════════════════════

User Clicks "Forgot Password"
        │
        ▼
Enter Email Address
        │
        ▼
AuthController.onForgotPassword()
        │
        ▼
UserService.forgotPassword(email)
        ├─ Generate OTP
        ├─ Update database with OTP_CODE
        │
        └─ EmailService.sendResetEmail(email, otp)  ◄─ NEW
           ├─ Connect to Gmail SMTP
           ├─ Format HTML email with OTP
           └─ Send Email ✉️
        │
        ▼
User Receives Email
        │
        ▼
User Enters OTP
        │
        ▼
UserService.verifyOTP(email, otp)  ✓ Verified
        │
        ▼
User Sets New Password
        │
        ▼
UserService.resetPassword(email, newPassword)
        ├─ Hash new password
        ├─ Update database
        └─ Clear OTP_CODE
        │
        ▼
LOGIN WITH NEW PASSWORD ✅
```

---

## Component Interaction Diagram

```
┌────────────────────────────────────────────────────────────┐
│                    USER INTERFACE LAYER                    │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │Login Screen  │  │Forgot Pwd    │  │Player Profile  │  │
│  │              │  │Dialog        │  │Page (UPDATED)  │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬───────┘  │
│         │                 │                   │           │
└─────────┼─────────────────┼───────────────────┼───────────┘
          │                 │                   │
          ▼                 ▼                   ▼
┌────────────────────────────────────────────────────────────┐
│              CONTROLLER LAYER                              │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ AuthController       TopNavController              │ │
│  │ ├─ onSignIn()        ├─ onMyProfile()             │ │
│  │ ├─ onSignUp()        ├─ onLogout()                │ │
│  │ └─ onForgotPassword()└─ onAdminPanel()            │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ PlayerProfileController (NEW)                      │ │
│  │ ├─ initialize()                                    │ │
│  │ ├─ loadUserProfile()                              │ │
│  │ ├─ loadProfileStats()                             │ │
│  │ └─ onEdit()                                       │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────┐
│              SERVICE LAYER                                 │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ UserService                                         │ │
│  │ ├─ authenticate()  ┌──────────────────┐           │ │
│  │ ├─ createUser()    │  EmailService    │           │ │
│  │ ├─ forgotPassword()│  (ENHANCED)      │           │ │
│  │ ├─ verifyOTP()     ├─ sendResetEmail()│           │ │
│  │ ├─ resetPassword() ├─ sendVerifEmail()│           │ │
│  │ └─ emailExists()   └──────────────────┘           │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ JwtTokenGenerator (NEW)                            │ │
│  │ ├─ generateToken()                                 │ │
│  │ ├─ validateToken()                                 │ │
│  │ ├─ extractUserId()                                 │ │
│  │ ├─ extractEmail()                                  │ │
│  │ └─ extractRole()                                   │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────┐
│           DATA ACCESS LAYER                                │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ MyConnection                                        │ │
│  │ ├─ getInstance()                                    │ │
│  │ └─ getCnx() - MySQL Connection                     │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────┐
│              DATABASE LAYER                                │
│  ┌──────────────────────────────────────────────────────┐ │
│  │ MySQL Database (rankup_esports)                     │ │
│  │ ├─ user table                                       │ │
│  │ │  ├─ id, email, password, username                │ │
│  │ │  ├─ role, status, otp_code, created_at           │ │
│  │ │  └─ (+ other columns)                            │ │
│  │ └─ (other tables)                                  │ │
│  └──────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────┘
```

---

## API Flow Diagram

```
                    REST API ENDPOINTS (NEW)
                    ========================

CLIENT                          SERVER                    DATABASE
  │                              │                          │
  ├──POST /auth/login──────────►│ AuthApiController        │
  │  {email, password}           │ └─ validate user         │
  │                              │ └─ check status      ┌──►│ SELECT user
  │◄──────JWT Token───────────┤ │ └─ generate token    │   │
  │  {token, userId...}          │                      └───┤
  │                              │
  │                              │
  ├──POST /auth/register────────►│ AuthApiController        │
  │  {email, password, username} │ └─ validate input        │
  │                              │ └─ check email exists ┌─►│ SELECT email
  │◄────Success Response────────┤ │ └─ create user      │   │
  │  {success, message}           │                      │   │ INSERT user
  │                              │                      └───┤
  │                              │
  │                              │
  ├──POST /auth/forgot-password─►│ AuthApiController        │
  │  {email}                      │ └─ find user         ┌──►│ SELECT user
  │                              │ └─ generate OTP      │   │
  │                              │ └─ send email        │   │ UPDATE otp_code
  │◄────Success Response────────┤ │ └──────────┐        └───┤
  │                              │            │
  │                              │        ┌───▼──────┐
  │                              │        │ Email    │
  │                              │        │ Service  │
  │                              │        └──────────┘
  │                              │             │
  │                      USER ◄──┤─ Email: "Your OTP is: 123456"
  │                              │
  │                              │
  ├──POST /auth/verify-otp──────►│ AuthApiController        │
  │  {email, otp}                │ └─ verify OTP        ┌──►│ SELECT otp_code
  │                              │                      │   │
  │◄────Valid/Invalid───────────┤                      └───┤
  │                              │
  │                              │
  ├──POST /auth/reset-password──►│ AuthApiController        │
  │  {email, newPassword}        │ └─ hash password         │
  │                              │ └─ update password   ┌──►│ UPDATE password
  │◄────Success Response────────┤ │ └─ clear OTP       │   │ UPDATE otp_code=NULL
  │                              │                      └───┤
```

---

## Files Modified vs Created

```
✅ NEW FILES (3)
├── JwtTokenGenerator.java (Tool for JWT tokens)
├── AuthApiController.java (REST API endpoints)
└── PlayerProfileController.java (Dynamic profile display)

📝 MODIFIED FILES (3)
├── EmailService.java (Email sending now works!)
├── UserService.java (Calls email service)
└── player-profile.fxml (Dynamic labels instead of hardcoded)

📚 DOCUMENTATION (4)
├── API_DOCUMENTATION.md (API reference)
├── CONFIGURATION_GUIDE.md (Setup guide)
├── IMPLEMENTATION_REPORT.md (Technical summary)
└── QUICK_START_TESTING.md (Testing guide)
```

---

## Technology Stack

```
┌─────────────────────────────────────────────────────┐
│            TECHNOLOGY STACK                         │
├─────────────────────────────────────────────────────┤
│ Frontend:  JavaFX 21 + FXML                        │
│ Backend:   Java 17+                                │
│ Database:  MySQL 8.0+                              │
│ Email:     Gmail SMTP / SMTP Server                │
│ Auth:      JWT Tokens + Bcrypt Hashing            │
│ Tools:     Maven, IntelliJ IDEA                    │
└─────────────────────────────────────────────────────┘
```

---

## Security Layers

```
┌────────────────────────────────────────────────────┐
│        SECURITY IMPLEMENTATION                     │
├────────────────────────────────────────────────────┤
│ Layer 1: Input Validation                         │
│  └─ All API endpoints validate input              │
│                                                   │
│ Layer 2: Authentication                           │
│  ├─ Email/Password validation                     │
│  └─ Account status checking                       │
│                                                   │
│ Layer 3: Authorization                            │
│  ├─ JWT token validation                          │
│  └─ Role-based access control                     │
│                                                   │
│ Layer 4: Data Protection                          │
│  ├─ Bcrypt password hashing                       │
│  ├─ OTP expiration (15 min)                       │
│  └─ HTTPS ready (when deployed)                   │
│                                                   │
│ Layer 5: Logging & Monitoring                     │
│  ├─ Failed login attempts logged                  │
│  ├─ Email send status logged                      │
│  └─ API request logging ready                     │
└────────────────────────────────────────────────────┘
```

---

## Deployment Ready

```
┌────────────────────────────────────────────────────┐
│        DEPLOYMENT CHECKLIST                        │
├────────────────────────────────────────────────────┤
│ ✅ Code compiled and tested                       │
│ ✅ JavaFX UI functional                           │
│ ✅ Database integration complete                  │
│ ⏳ Email service (manual setup required)          │
│ ⏳ REST API server (manual deployment)            │
│ ⏳ Production security hardening                  │
│ ⏳ Load testing & optimization                    │
└────────────────────────────────────────────────────┘
```

---

This architecture ensures:
✅ Scalability - Modular design allows easy expansion
✅ Security - Multiple authentication layers
✅ Maintainability - Clear separation of concerns
✅ Integration - Ready for external APIs
✅ Performance - Efficient session management

