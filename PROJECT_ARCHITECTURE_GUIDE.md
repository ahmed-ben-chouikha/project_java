# 🏗️ Project Architecture: New Subsystems Overview

## Current Architecture (project_java)

```
╔════════════════════════════════════════════════════════════════════╗
║                    RankUp E-Sports Platform                        ║
╠════════════════════════════════════════════════════════════════════╣
║                                                                    ║
║  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            ║
║  │  Tournament  │  │    Teams     │  │   Reviews    │            ║
║  │   System     │  │   Management │  │   System     │            ║
║  └──────────────┘  └──────────────┘  └──────────────┘            ║
║                                                                    ║
║  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐            ║
║  │   Matches    │  │   Players    │  │   Persons    │            ║
║  │ Management   │  │  Management  │  │  Management  │            ║
║  └──────────────┘  └──────────────┘  └──────────────┘            ║
║                                                                    ║
║             Basic Authentication (Login Only)                     ║
║                                                                    ║
╚════════════════════════════════════════════════════════════════════╝
```

---

## Enhanced Architecture (project_java-master)

```
╔════════════════════════════════════════════════════════════════════════════╗
║                    RankUp E-Sports Platform (Enhanced)                     ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  ┌─────────────────────────────────── CORE SYSTEMS ─────────────────────────┐ ║
║  │                                                                           │ ║
║  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                   │ ║
║  │  │  Tournament  │  │    Teams     │  │   Reviews    │                   │ ║
║  │  │   System     │  │   Management │  │   System     │                   │ ║
║  │  └──────────────┘  └──────────────┘  └──────────────┘                   │ ║
║  │                                                                           │ ║
║  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                   │ ║
║  │  │   Matches    │  │   Players    │  │   Persons    │                   │ ║
║  │  │ Management   │  │  Management  │  │  Management  │                   │ ║
║  │  └──────────────┘  └──────────────┘  └──────────────┘                   │ ║
║  │                                                                           │ ║
║  └───────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌─────────────────────── NEW: AUTHENTICATION LAYER ──────────────────────┐ ║
║  │                                                                        │ ║
║  │  User Management                                                      │ ║
║  │  ├─ User Entity (id, email, username, password, role, status)        │ ║
║  │  ├─ UserService (CRUD, authentication, role checking)               │ ║
║  │  ├─ SessionManager (user state, login/logout tracking)              │ ║
║  │  ├─ PasswordHashGenerator (SHA-256 hashing)                         │ ║
║  │  └─ Registration & Login UI                                         │ ║
║  │     ├─ AuthController (login)                                       │ ║
║  │     ├─ RegisterController (registration) [NEW]                      │ ║
║  │     └─ Views: login.fxml, register.fxml [register.fxml NEW]        │ ║
║  │                                                                        │ ║
║  │  RBAC (Role-Based Access Control)                                    │ ║
║  │  ├─ Admin Role: Full system access                                   │ ║
║  │  └─ Player Role: Limited feature access                              │ ║
║  │                                                                        │ ║
║  └────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌─────────────────── NEW: FINANCIAL MANAGEMENT SYSTEM ──────────────────┐ ║
║  │                                                                        │ ║
║  │  Budget & Expense Tracking                                            │ ║
║  │  ├─ Budget Module                                                     │ ║
║  │  │  ├─ Budget Entity (montantAlloué, montantUtilisé, teamId)         │ ║
║  │  │  ├─ BudgetService (CRUD, search, filtering)                      │ ║
║  │  │  └─ BudgetController → budget-list.fxml [NEW]                    │ ║
║  │  │                                                                     │ ║
║  │  └─ Expense Module                                                    │ ║
║  │     ├─ Depense Entity (titre, montant, catégorie, teamId)           │ ║
║  │     ├─ DepenseService (CRUD, categorization, calculations)          │ ║
║  │     └─ DepenseController → depense-list.fxml [NEW]                  │ ║
║  │                                                                        │ ║
║  │  Features: View, Create, Update, Delete, Filter by Status           │ ║
║  │                                                                        │ ║
║  └────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌──────────────────── NEW: SUPPORT & COMPLAINT SYSTEMS ──────────────────┐ ║
║  │                                                                        │ ║
║  │  ┌─────────────────┐  ┌──────────────┐  ┌──────────────┐             │ ║
║  │  │ Support Tickets │  │ Complaints   │  │  Disciplinary│             │ ║
║  │  │                 │  │(Reclamations)│  │ (Punitions)  │             │ ║
║  │  ├─ Ticket Entity  │  ├─ Reclamation │  ├─ Punition    │             │ ║
║  │  │   (title,       │  │   Entity     │  │   Entity     │             │ ║
║  │  │    category,    │  │   (title,    │  │   (type,     │             │ ║
║  │  │    priority)    │  │    reason,   │  │    reason,   │             │ ║
║  │  │                 │  │    status)   │  │    date)     │             │ ║
║  │  ├─ TicketService  │  ├─ Reclamation│  ├─ Punition    │             │ ║
║  │  │                 │  │   Service    │  │   Service    │             │ ║
║  │  ├─ TicketsCtrlr   │  ├─ Reclamation│  ├─ Punitions   │             │ ║
║  │  │                 │  │   Controller │  │   Controller │             │ ║
║  │  └─ tickets.fxml   │  └─ reclamations│  └─ punitions  │             │ ║
║  │    ticket-form.fxml│    .fxml       │    .fxml       │             │ ║
║  │                    │                │                │             │ ║
║  └─────────────────┘  └──────────────┘  └──────────────┘             │ ║
║  │                                                                        │ ║
║  └────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌──────────────────── ENHANCEMENTS TO EXISTING SYSTEMS ─────────────────┐ ║
║  │                                                                        │ ║
║  │  Team Management [ENHANCED]                                           │ ║
║  │  └─ Team Entity: 14 fields (added country, jeu, niveau, etc.)       │ ║
║  │     └─ TeamFormController [NEW] + TeamFormState [NEW]               │ ║
║  │                                                                        │ ║
║  │  Match Management [ENHANCED]                                          │ ║
║  │  └─ Match Entity, MatchService, MatchFormController [NEW]            │ ║
║  │     └─ MatchFormState [NEW]                                          │ ║
║  │                                                                        │ ║
║  │  Admin Response System [NEW]                                          │ ║
║  │  └─ AdminResponse Entity, AdminResponseService [NEW]                 │ ║
║  │     └─ AdminResponsesController [NEW]                               │ ║
║  │     └─ admin-responses.fxml [NEW]                                   │ ║
║  │                                                                        │ ║
║  └────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌──────────────── SHARED INFRASTRUCTURE & UTILITIES ──────────────────────┐ ║
║  │                                                                        │ ║
║  │  ValidationUtil.java [NEW]                                            │ ║
║  │  └─ Centralized validation for Budget, Depense, Tickets, etc.        │ ║
║  │                                                                        │ ║
║  │  Database Connection (MyConnection.java)                              │ ║
║  │  └─ Connection pooling & management                                   │ ║
║  │                                                                        │ ║
║  │  UI Components (FXML common)                                          │ ║
║  │  └─ Enhanced with ControlsFX & Ikonli icons                          │ ║
║  │                                                                        │ ║
║  └────────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  ┌──────────────────── DATABASE LAYER (MySQL) ─────────────────────────┐  ║
║  │                                                                      │  ║
║  │  EXISTING TABLES:                    NEW TABLES:                   │  ║
║  │  ├─ tournaments                      ├─ users [P1]               │  ║
║  │  ├─ tournament_registrations         ├─ budgets [P2]            │  ║
║  │  ├─ reviews                          ├─ depenses [P2]           │  ║
║  │  └─ (existing game, person tables)   ├─ tickets [P3]            │  ║
║  │                                      ├─ reclamations [P3]       │  ║
║  │                                      ├─ punitions [P3]          │  ║
║  │                                      └─ admin_responses [P3]    │  ║
║  │                                                                      │  ║
║  └──────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 🔄 Data Flow Examples

### Authentication Flow (NEW)
```
User Registration
    ↓
RegisterController
    ↓
User Input Validation (ValidationUtil)
    ↓
PasswordHashGenerator (SHA-256)
    ↓
UserService.createUser()
    ↓
INSERT → users table
    ↓
✅ Success / Redirect to Login

User Login
    ↓
AuthController
    ↓
UserService.authenticate(email, password)
    ↓
SELECT user + verify hash
    ↓
SessionManager.login(user)
    ↓
✅ Session Established / Grant Access
```

### Budget Management Flow (NEW)
```
Admin Views Budget Dashboard
    ↓
BudgetController.initialize()
    ↓
BudgetService.getAllBudgets()
    ↓
SELECT * FROM budgets
    ↓
TableView Display (7 columns)
    ↓
User Actions:
├─ View Details → Modal display
├─ Edit → Dialog → ValidationUtil → BudgetService.updateBudget()
├─ Delete → Confirm → BudgetService.deleteBudget()
└─ Add → Dialog → BudgetService.addBudget()
    ↓
✅ Database Updated
```

### Support Ticket Flow (NEW)
```
User Creates Ticket
    ↓
TicketFormController
    ↓
Input Validation (ValidationUtil)
    ↓
TicketService.addTicket()
    ↓
INSERT → tickets table
    ↓
Admin Views Tickets
    ↓
TicketsController.initialize()
    ↓
TicketService.getAllTickets()
    ↓
TableView Display
    ↓
Admin Actions:
├─ Assign to admin
├─ Change priority
└─ Resolve ticket
    ↓
✅ Ticket Lifecycle Managed
```

---

## 📦 Maven Dependency Tree

### Current Dependencies
```
org.junit.jupiter:junit-jupiter:5.10.2
mysql:mysql-connector-java:8.0.30
org.openjfx:javafx-fxml:21.0.2
org.openjfx:javafx-controls:21.0.2
com.google.code.gson:gson:2.10.1
```

### NEW Dependencies to Add
```
org.controlsfx:controlsfx:11.2.1
    ├─ Enhanced dialogs
    ├─ Advanced UI controls
    └─ Professional look & feel

org.kordamp.ikonli:ikonli-javafx:12.3.1
    ├─ Icon support in JavaFX
    └─ Used with:

org.kordamp.ikonli:ikonli-fontawesome5-pack:12.3.1
    └─ FontAwesome 5 icons (500+ icons)
```

---

## 🗂️ File Organization

### Java Source Structure
```
src/main/java/edu/connexion3a36/
├── entities/
│   ├── User.java [NEW]
│   ├── Budget.java [NEW]
│   ├── Depense.java [NEW]
│   ├── Ticket.java [NEW]
│   ├── Reclamation.java [NEW]
│   ├── Punition.java [NEW]
│   ├── AdminResponse.java [NEW]
│   ├── Team.java [ENHANCED]
│   ├── Match.java [ENHANCED]
│   └── (existing entities)
├── services/
│   ├── UserService.java [NEW]
│   ├── BudgetService.java [NEW]
│   ├── DepenseService.java [NEW]
│   ├── TicketService.java [NEW]
│   ├── ReclamationService.java [NEW]
│   ├── PunitionService.java [NEW]
│   ├── AdminResponseService.java [NEW]
│   ├── MatchService.java [NEW]
│   └── (existing services)
├── rankup/
│   ├── app/
│   │   ├── SessionManager.java [NEW]
│   │   └── (other app files)
│   └── controllers/
│       ├── RegisterController.java [NEW]
│       ├── BudgetController.java [NEW]
│       ├── DepenseController.java [NEW]
│       ├── TicketsController.java [NEW]
│       ├── TicketFormController.java [NEW]
│       ├── ReclamationsController.java [NEW]
│       ├── PunitionsController.java [NEW]
│       ├── TeamFormController.java [NEW]
│       ├── MatchFormController.java [NEW]
│       ├── admin/
│       │   └── AdminResponsesController.java [NEW]
│       └── (existing controllers)
├── tools/
│   ├── PasswordHashGenerator.java [NEW]
│   ├── ValidationUtil.java [NEW]
│   └── (existing utilities)
└── (existing structure)
```

### Resources Structure
```
src/main/resources/views/
├── auth/
│   ├── login.fxml
│   └── register.fxml [NEW]
├── budget/
│   ├── budget-dashboard.fxml
│   └── budget-list.fxml [NEW]
├── depense/
│   └── depense-list.fxml [NEW]
├── tickets/
│   ├── tickets.fxml [NEW]
│   └── ticket-form.fxml [NEW]
├── reclamations/
│   └── reclamations.fxml [NEW]
├── punitions/
│   └── punitions.fxml [NEW]
├── adminresponses/
│   └── admin-responses.fxml [NEW]
└── (existing views)
```

---

## 📊 Feature Comparison Matrix

| Feature | Current | Enhanced | Status |
|---------|---------|----------|--------|
| Tournament Management | ✅ | ✅ | Maintained |
| Team Management | ✅ Basic | ✅ 14 fields | Enhanced |
| Match Management | ✅ Basic | ✅ Full CRUD | Enhanced |
| Player Management | ✅ | ✅ | Maintained |
| Reviews System | ✅ | ✅ | Maintained |
| **Authentication** | ⚠️ Basic | ✅ Full RBAC | **NEW** |
| **Budget Tracking** | ❌ | ✅ | **NEW** |
| **Expense Management** | ❌ | ✅ | **NEW** |
| **Support Tickets** | ❌ | ✅ | **NEW** |
| **Complaints/Reclamations** | ❌ | ✅ | **NEW** |
| **Disciplinary System** | ❌ | ✅ | **NEW** |
| **Admin Response System** | ❌ | ✅ | **NEW** |
| **Session Management** | ❌ | ✅ | **NEW** |
| **Password Security** | ❌ | ✅ SHA-256 | **NEW** |
| **Form State Management** | ⚠️ Basic | ✅ Advanced | Enhanced |
| **Input Validation** | ⚠️ Scattered | ✅ Centralized | Enhanced |

---

## 🚀 Integration Sequence Diagram

```
PHASE 1: Setup & Dependencies (Day 1)
┌─────────────────────┐
│ 1. Backup project   │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 2. Update pom.xml   │
│    (deps + plugins) │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 3. Run migrations   │
│    (SQL files)      │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 4. Rebuild project  │
└─────────────────────┘

PHASE 2: Core Infrastructure (Day 2)
┌─────────────────────┐
│ 1. Add User entity  │
│    + UserService    │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 2. Add utilities    │
│  (Session, Password)│
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 3. Update Auth ctrl │
│    + Register ctrl  │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 4. Add auth views   │
│   (register.fxml)   │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 5. Test login/reg   │
└─────────────────────┘

PHASE 3: Financial System (Day 3)
┌─────────────────────┐
│ 1. Add Budget/      │
│    Depense entities │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 2. Add services     │
│    + controllers    │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 3. Add views        │
│   (budget/depense)  │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 4. Integrate in nav │
│    (sidebar)        │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 5. Test budget UI   │
└─────────────────────┘

PHASE 4: Support Systems (Day 4)
┌─────────────────────┐
│ 1. Add Ticket/      │
│    Complaint/       │
│    Punition files   │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 2. Integration test │
│    all features     │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ 3. Performance test │
└─────────────────────┘
```

---

## ✅ Validation Checklist After Merge

- [ ] Project builds without errors: `mvn clean compile`
- [ ] All tests pass: `mvn test`
- [ ] Application launches: `mvn exec:java@run`
- [ ] Login works with test credentials
- [ ] User registration works
- [ ] Budget module accessible
- [ ] Expense tracking works
- [ ] Support tickets can be created
- [ ] Admin can manage all systems
- [ ] Session management works (login/logout)
- [ ] Navigation sidebar shows all new items
- [ ] Database queries execute correctly
- [ ] No SQL errors in console
- [ ] UI looks professional (with icons & styling)

---

Generated: April 30, 2026
