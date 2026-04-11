# Quick Start Guide

## 1. Prerequisites Setup

### Install Maven (if not already installed)
- Download: https://maven.apache.org/download.cgi
- Extract to `C:\Apache\maven` (or your preferred path)
- Add to PATH: `C:\Apache\maven\bin`
- Verify: `mvn --version`

### Ensure MySQL is Running
```bash
# Start XAMPP MySQL (or MySQL Server)
# Verify connection
mysql -u root -h localhost -e "SELECT 1;"
```

### Create the Database
```sql
-- Connect to MySQL
mysql -u root

-- Create database and table
CREATE DATABASE IF NOT EXISTS esportdevvvvvv;
USE esportdevvvvvv;

CREATE TABLE IF NOT EXISTS personne (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(255) NOT NULL,
  prenom VARCHAR(255) NOT NULL
);

-- Optional: Insert sample data
INSERT INTO personne (nom, prenom) VALUES 
  ('Nova', 'Crew'),
  ('Eclipse', 'Squad'),
  ('Apex', 'Drift');
```

## 2. Build the App

```bash
# Navigate to project
cd C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36

# Clean and compile
mvn clean compile

# Run via Maven plugin
mvn javafx:run

# OR: Package and run standalone
mvn clean package
java -jar target/Connexion3A36-1.0-SNAPSHOT.jar
```

## 3. Verify Installation

1. **App starts successfully**: Main dashboard appears with dark theme
2. **Navigation works**: Click sidebar buttons to switch pages
3. **Database connects**: Check console for "Connection établie!" message
4. **Tournament join works**: Fill form and click "Join tournament"

## 4. Common Issues

### "Connection refused" / "Unknown database 'esportdevvvvvv'"
- **Fix**: Start XAMPP MySQL and create the database (see step 1)

### "Cannot find symbol: class StackPane"
- **Fix**: Ensure JavaFX SDK is in IDE classpath (Project → Libraries)

### App doesn't launch
- **Fix**: Check Java version: `java -version` (should be 17+)
- Run: `mvn help:describe -Dplugin=org.openjfx:javafx-maven-plugin`

### CSS not applying (white background instead of dark)
- **Fix**: Verify CSS file is in `src/main/resources/styles/esports.css`
- Clean and rebuild: `mvn clean compile`

## 5. IDE Setup (IntelliJ IDEA recommended)

1. **Open Project**: File → Open → Select `pom.xml`
2. **Configure JDK**: File → Project Structure → Project SDK → jdk-22
3. **Configure JavaFX SDK**: 
   - File → Project Structure → Libraries
   - Add new library → Java
   - Select JavaFX SDK directory
4. **Edit Run Configuration**:
   - Run → Edit Configurations
   - Main class: `edu.connexion3a36.tests.MainFx`
   - VM Options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`
5. **Run**: Click Run or press Shift+F10

## 6. Check Build Output

```bash
# Verify classes were compiled
ls target/classes/edu/connexion3a36/tests/MainFx.class

# Run with debug output
mvn -X javafx:run 2>&1 | head -50
```

## 7. Next: Connect Real Data

Edit `src/main/java/edu/connexion3a36/services/EsportsCatalogService.java` to query your MySQL instead of returning mock data:

```java
public List<TournamentCard> getUpcomingTournaments() {
    List<TournamentCard> tournaments = new ArrayList<>();
    try {
        String query = "SELECT name, game, date, prizePool, slots FROM tournaments WHERE date > NOW()";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            tournaments.add(new TournamentCard(
                rs.getString("name"),
                rs.getString("game"),
                rs.getString("date"),
                rs.getString("prizePool"),
                rs.getString("slots"),
                true
            ));
        }
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    }
    return tournaments;
}
```

---

**Questions?** Check console output with `mvn -X` flag or increase logging in `logback.xml`.

