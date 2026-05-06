# ðŸš€ Quick Setup - Teams CRUD

## Setup Express en 3 Ã©tapes

### 1ï¸âƒ£ CrÃ©er la table MySQL

```bash
mysql -u root esportdevvvvvv-2 < setup_team_table.sql
```

**OU manuellement** :
```sql
CREATE TABLE IF NOT EXISTS team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    region VARCHAR(100) NOT NULL,
    roster TEXT,
    record VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO team (name, region, roster, record, description) VALUES
('Eclipse', 'EU West', 'Rex â€¢ Nova â€¢ Byte â€¢ Kaze â€¢ Lynx', '14W / 3L', 'Dominant EU West team'),
('Apex Drift', 'NA Central', 'Milo â€¢ Vex â€¢ Raze â€¢ Sol â€¢ Flux', '11W / 5L', 'NA Rising stars'),
('Shadow Unit', 'APAC', 'Kai â€¢ Ember â€¢ Drift â€¢ Zen â€¢ Orion', '17W / 2L', 'APAC Champions');
```

### 2ï¸âƒ£ Compiler

```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### 3ï¸âƒ£ IntÃ©grer dans votre navigation

Vous avez dÃ©jÃ  un contrÃ´leur `TeamsController.java` et une vue `teams.fxml` prÃªts Ã  utiliser !

## FonctionnalitÃ©s

âœ… **Affichage** - Liste toutes les Ã©quipes avec tableau  
âœ… **CrÃ©ation** - Ajouter une nouvelle Ã©quipe  
âœ… **Modification** - Ã‰diter une Ã©quipe existante  
âœ… **Suppression** - Supprimer une Ã©quipe (avec confirmation)  
âœ… **Recherche** - Chercher par nom d'Ã©quipe  
âœ… **Refresh** - Actualiser la liste

## Fichiers ajoutÃ©s

```
src/main/java/
â”œâ”€â”€ entities/
â”‚   â””â”€â”€ Team.java (NOUVEAU)
â”œâ”€â”€ services/
â”‚   â””â”€â”€ TeamService.java (NOUVEAU)
â””â”€â”€ rankup/controllers/
    â””â”€â”€ TeamsController.java (NOUVEAU)

src/main/resources/
â””â”€â”€ views/teams/
    â””â”€â”€ teams.fxml (MODIFIÃ‰)

Racine/
â”œâ”€â”€ setup_team_table.sql (NOUVEAU - Script DB)
â””â”€â”€ TEAMS_CRUD_GUIDE.md (NOUVEAU - Documentation)
```

## Architecture

```
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚     TeamsController (JavaFX UI)     â”‚
â”‚  - Affichage tableau                â”‚
â”‚  - Dialogs crÃ©ation/modification    â”‚
â”‚  - Actions (Edit/Delete)            â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
               â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚      TeamService (Business Logic)    â”‚
â”‚  - CRUD operations                   â”‚
â”‚  - Database queries                  â”‚
â”‚  - Search functionality              â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”¬â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
               â”‚
               â–¼
â”Œâ”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”
â”‚    MySQL Database (esportdevvvvvv-2)   â”‚
â”‚  - Table: team                       â”‚
â”‚  - Columns: id, name, region, ...    â”‚
â””â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”˜
```

## Exemples d'utilisation

### RÃ©cupÃ©rer toutes les Ã©quipes
```java
TeamService service = new TeamService();
List<Team> teams = service.getAllTeams();
teams.forEach(t -> System.out.println(t.getName()));
```

### Ajouter une Ã©quipe
```java
Team newTeam = new Team("Nova Crew", "EU West", "Player1 â€¢ Player2", "5W / 1L", "Cool team");
teamService.addTeam(newTeam);
```

### Rechercher une Ã©quipe
```java
List<Team> results = teamService.searchTeamsByName("Eclipse");
```

### Modifier une Ã©quipe
```java
Team team = teamService.getTeamById(1);
team.setRecord("20W / 5L");
teamService.updateTeam(team);
```

### Supprimer une Ã©quipe
```java
teamService.deleteTeam(1);
```

## Prochaines Ã©tapes

1. âœ… CrÃ©er la table MySQL
2. âœ… Compiler le projet
3. â­ï¸ Connecter le bouton "Teams" de votre navigation vers TeamsController
4. â­ï¸ Lancer l'application et tester le CRUD

## Notes

- La classe `Team` a automatiquement des getters/setters
- `TeamService` utilise `MyConnection.getInstance()` pour la DB
- Les dialogues sont crÃ©Ã©s dynamiquement en JavaFX (pas besoin de FXML sÃ©parÃ©)
- La recherche utilise des requÃªtes paramÃ©trÃ©es (sÃ©curitÃ© SQL)

---

**Status** âœ… CompilÃ© et prÃªt Ã  l'emploi !

