# EsportDev Arena - Modern JavaFX Esports Dashboard

A sleek, dark-themed JavaFX application for managing esports tournaments, matches, teams, and player registrations. Connects to your MySQL database.

## 🎯 Features

- **Dashboard**: Live match overview, tournament highlights, featured teams, and admin/user role snapshots
- **Match Center**: Browse live and upcoming esports matches with broadcast-ready actions
- **Tournament Hub**: Browse open tournaments, register your squad with team name and gamer tag
- **Teams Showcase**: Explore competitive rosters, regions, and match records
- **User Area**: Player profile, tournament eligibility, and quick registration shortcuts
- **Admin Console**: Operations monitoring, pending approvals, moderation queue, and admin tools
- **Role-based Navigation**: Toggle between user and admin modes with view-specific content
- **Modern UI Theme**: Dark neon design with cyan and purple accents, glass-morphism cards, smooth animations

## 📋 Project Structure

```
Connexion3A36/
├── src/main/
│   ├── java/edu/connexion3a36/
│   │   ├── Controller/
│   │   │   ├── AjouterPersonneController.java          # Main shell / dashboard controller
│   │   │   └── AfficherPersonne2Controller.java        # Tournament hub controller
│   │   ├── services/
│   │   │   ├── EsportsCatalogService.java             # Mock esports data provider
│   │   │   └── PersonneService.java                    # (Legacy, can extend for DB)
│   │   ├── entities/
│   │   │   └── Personne.java                          # (Legacy, kept for compatibility)
│   │   ├── tools/
│   │   │   └── MyConnection.java                       # MySQL connection (esportdevvvvvv DB)
│   │   └── tests/
│   │       └── MainFx.java                            # Application entry point
│   └── resources/
│       ├── AjouterPersonne.fxml                       # Main shell layout
│       ├── AfficherPersonne2.fxml                     # Tournament hub layout
│       └── styles/
│           └── esports.css                            # Dark neon theme
├── pom.xml                                            # Maven dependencies (JavaFX, MySQL, JUnit)
└── target/                                            # Build output
```

## 🗄️ Database Setup

The app connects to your XAMPP MySQL database:
- **Database Name**: `esportdevvvvvv`
- **Host**: `localhost:3306`
- **User**: `root`
- **Password**: (empty by default)

### Required Table

Create this table in your `esportdevvvvvv` database:

```sql
CREATE TABLE IF NOT EXISTS personne (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(255) NOT NULL,
  prenom VARCHAR(255) NOT NULL
);
```

## 🚀 How to Run

### Prerequisites
- **Java 17+** (ideally 21+)
- **Maven 3.6+**
- **JavaFX SDK 21** (specified in pom.xml)
- **MySQL Server** running with `esportdevvvvvv` database

### Build and Run

```bash
# Navigate to the project directory
cd C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36

# Build the project
mvn clean javafx:run

# Or package and run
mvn clean package
java -p target/classes;$JAVAFX_MODULE_PATH -m javafx.graphics/edu.connexion3a36.tests.MainFx
```

> Replace `$JAVAFX_MODULE_PATH` with your JavaFX SDK lib path, e.g.:
> `C:\path\to\javafx-sdk-21\lib`

### Alternative: Run from IDE
1. Open the project in IntelliJ IDEA, Eclipse, or NetBeans
2. Configure JavaFX SDK in IDE settings
3. Run `MainFx.java` as the main class

## 🎨 UI Tour

### Dashboard
- **Hero Section**: Quick intro with CTA buttons
- **Stats Row**: KPIs (live matches, open slots, featured teams, admin actions)
- **Live Matches Preview**: Featured matchups with status badges
- **Tournaments Preview**: Open slots and prize pools
- **Teams Preview**: Top regional rosters
- **Role Snapshot**: Context-aware user/admin shortcuts

### Matches
- Full match center with live and upcoming matches
- Status badges (LIVE, UP NEXT, OPEN)
- Broadcast queue actions

### Tournament Hub
- Browse all upcoming tournaments
- Filter by game title
- Fill registration form (gamer tag, team name)
- View player or admin focus based on mode

### Admin Features
- Operations overview (pending approvals, live brackets, moderation)
- Admin tools (roster approval, bracket sync, announcements)
- Moderation queue cards

## 🛠️ Architecture

- **Controllers**: Handle UI events, page navigation, and state management
- **Services**: Provide mock and database-backed data (EsportsCatalogService for demo data)
- **FXML**: Declarative layouts for main shell and tournament hub
- **CSS Theme**: Global esports-inspired dark mode with neon accents
- **MySQL Connection**: Singleton pattern for DB access

## 📝 Key Files Modified

- `MainFx.java`: Updated to load new shell and apply theme
- `AjouterPersonneController.java`: Replaced with full esports dashboard
- `AfficherPersonne2Controller.java`: Replaced with tournament hub
- `AjouterPersonne.fxml`: New shell layout (sidebar nav, header, content host)
- `AfficherPersonne2.fxml`: Tournament hub layout with registration form
- `esports.css`: Complete dark neon theme
- `EsportsCatalogService.java`: New service providing mock esports data
- `MyConnection.java`: Updated to use `esportdevvvvvv` database

## 🔄 Extending the App

### Add Real Tournament Data
Replace `EsportsCatalogService` mock methods with actual database queries:

```java
// Example: Fetch tournaments from database
public List<TournamentCard> getUpcomingTournaments() {
    List<TournamentCard> tournaments = new ArrayList<>();
    String query = "SELECT * FROM tournaments WHERE date > NOW()";
    // Execute query via MyConnection.getInstance().getCnx()
    return tournaments;
}
```

### Add User Authentication
1. Create a `User` entity and `UserService`
2. Add login page before dashboard
3. Store role (user/admin) in session after login

### Connect Real Bracket Data
1. Create `Match`, `Team`, `Bracket` entities
2. Build `MatchService`, `TeamService`, `BracketService`
3. Update controller methods to fetch live data

## 📦 Dependencies

- **JavaFX 21.0.2**: UI framework
- **MySQL Connector 8.0.30**: Database driver
- **JUnit 5.10.2**: Testing

## 🎯 Next Steps

1. **Database Schema**: Create `tournaments`, `matches`, `teams`, `users` tables
2. **Real Data Binding**: Update services to query the database
3. **User Authentication**: Add login/signup flow
4. **WebSocket Integration**: Real-time match updates
5. **Styling Refinements**: Adjust colors and fonts per brand guidelines

## 📄 License

Built with JavaFX and MySQL for educational and esports management purposes.

---

**EsportDev Arena** — Compete, watch, and manage in one sleek hub.

