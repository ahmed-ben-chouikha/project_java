# Quick Start Guide (Windows + PowerShell)

This project is configured with Maven and JavaFX (`javafx-maven-plugin` in `pom.xml`) and targets Java 17.

## 1) Prerequisites

- Windows PowerShell
- JDK 17 or newer (21/22 is fine)
- Maven 3.8+ (or use the local script `mvn.ps1`)
- XAMPP MySQL running on `localhost:3306`

## 2) Open the Project Folder

```powershell
Set-Location "C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36"
```

## 3) Check Java and Maven

```powershell
java -version
.\mvn.ps1 -version
```

If `java -version` shows Java 8 or lower, switch to JDK 17+ before running.

## 4) Start and Prepare the Database

Start MySQL from XAMPP, then run:

```powershell
mysql -u root -h localhost -e "CREATE DATABASE IF NOT EXISTS esportdevvvvvv;"
```

If your `mysql` command is not in PATH, use the XAMPP binary directly:

```powershell
& "C:\xampp\mysql\bin\mysql.exe" -u root -h localhost -e "CREATE DATABASE IF NOT EXISTS esportdevvvvvv;"
```

Create the minimum base table used by older parts of the app:

```powershell
mysql -u root -h localhost -D esportdevvvvvv -e "CREATE TABLE IF NOT EXISTS personne (id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL);"
```

Optional sample rows:

```powershell
mysql -u root -h localhost -D esportdevvvvvv -e "INSERT INTO personne (nom, prenom) VALUES ('Nova','Crew'),('Eclipse','Squad'),('Apex','Drift');"
```

## 5) Run the App

Use one of these methods.

### Method A (recommended): launcher script

```powershell
.\launch.ps1
```

### Method B: direct Maven command via local script

```powershell
.\mvn.ps1 clean javafx:run
```

### Method C: global Maven (if installed in PATH)

```powershell
mvn clean javafx:run
```

## 6) Quick Verification After Launch

- The main JavaFX window opens.
- Sidebar navigation switches pages.
- DB-driven pages load without SQL errors.

## 7) Common Errors and Fixes

### `mvn` is not recognized

Use the project script instead of global Maven:

```powershell
.\mvn.ps1 clean javafx:run
```

Or install Maven and add `<maven>\bin` to PATH.

### `Java 17+ requis` / Java 8 detected

Set Java 17+ for the current terminal session:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-22"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```

### `No plugin found for prefix 'javafx'`

This usually means Maven was run from the wrong folder. Ensure you are in the folder that contains `pom.xml`:

```powershell
Set-Location "C:\Users\ahmed\Downloads\JAVAFX\Connexion3A36"
.\mvn.ps1 clean javafx:run
```

### `Maven introuvable` from `mvn.ps1`

Install Maven or set `M2_HOME` to your Maven installation root:

```powershell
$env:M2_HOME = "C:\Apache\maven"
$env:Path = "$env:M2_HOME\bin;" + $env:Path
.\mvn.ps1 -version
```

### Database/table errors (unknown table, missing column)

Run the SQL scripts in the `database/` folder and top-level `*.sql` files against `esportdevvvvvv`.

Example:

```powershell
mysql -u root -h localhost esportdevvvvvv < .\database\reviews_table.sql
```

## 8) Build Only (No Launch)

```powershell
.\mvn.ps1 clean compile
```

## 9) IDE Run (IntelliJ)

- Open `pom.xml` as a Maven project.
- Set Project SDK to JDK 17+.
- Run Maven goal `javafx:run`, or run `edu.connexion3a36.rankup.app.Main`.

---

If you want, I can also generate a one-command `dev-run.ps1` that starts MySQL checks, validates Java/Maven, and launches the app in one shot.

