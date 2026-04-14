# 🚀 Quick Setup - Teams CRUD

## Setup Express en 3 étapes

### 1️⃣ Créer la table MySQL

```bash
mysql -u root esportdevvvvvv < setup_team_table.sql
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
('Eclipse', 'EU West', 'Rex • Nova • Byte • Kaze • Lynx', '14W / 3L', 'Dominant EU West team'),
('Apex Drift', 'NA Central', 'Milo • Vex • Raze • Sol • Flux', '11W / 5L', 'NA Rising stars'),
('Shadow Unit', 'APAC', 'Kai • Ember • Drift • Zen • Orion', '17W / 2L', 'APAC Champions');
```

### 2️⃣ Compiler

```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### 3️⃣ Intégrer dans votre navigation

Vous avez déjà un contrôleur `TeamsController.java` et une vue `teams.fxml` prêts à utiliser !

## Fonctionnalités

✅ **Affichage** - Liste toutes les équipes avec tableau  
✅ **Création** - Ajouter une nouvelle équipe  
✅ **Modification** - Éditer une équipe existante  
✅ **Suppression** - Supprimer une équipe (avec confirmation)  
✅ **Recherche** - Chercher par nom d'équipe  
✅ **Refresh** - Actualiser la liste

## Fichiers ajoutés

```
src/main/java/
├── entities/
│   └── Team.java (NOUVEAU)
├── services/
│   └── TeamService.java (NOUVEAU)
└── rankup/controllers/
    └── TeamsController.java (NOUVEAU)

src/main/resources/
└── views/teams/
    └── teams.fxml (MODIFIÉ)

Racine/
├── setup_team_table.sql (NOUVEAU - Script DB)
└── TEAMS_CRUD_GUIDE.md (NOUVEAU - Documentation)
```

## Architecture

```
┌─────────────────────────────────────┐
│     TeamsController (JavaFX UI)     │
│  - Affichage tableau                │
│  - Dialogs création/modification    │
│  - Actions (Edit/Delete)            │
└──────────────┬──────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│      TeamService (Business Logic)    │
│  - CRUD operations                   │
│  - Database queries                  │
│  - Search functionality              │
└──────────────┬───────────────────────┘
               │
               ▼
┌──────────────────────────────────────┐
│    MySQL Database (esportdevvvvvv)   │
│  - Table: team                       │
│  - Columns: id, name, region, ...    │
└──────────────────────────────────────┘
```

## Exemples d'utilisation

### Récupérer toutes les équipes
```java
TeamService service = new TeamService();
List<Team> teams = service.getAllTeams();
teams.forEach(t -> System.out.println(t.getName()));
```

### Ajouter une équipe
```java
Team newTeam = new Team("Nova Crew", "EU West", "Player1 • Player2", "5W / 1L", "Cool team");
teamService.addTeam(newTeam);
```

### Rechercher une équipe
```java
List<Team> results = teamService.searchTeamsByName("Eclipse");
```

### Modifier une équipe
```java
Team team = teamService.getTeamById(1);
team.setRecord("20W / 5L");
teamService.updateTeam(team);
```

### Supprimer une équipe
```java
teamService.deleteTeam(1);
```

## Prochaines étapes

1. ✅ Créer la table MySQL
2. ✅ Compiler le projet
3. ⏭️ Connecter le bouton "Teams" de votre navigation vers TeamsController
4. ⏭️ Lancer l'application et tester le CRUD

## Notes

- La classe `Team` a automatiquement des getters/setters
- `TeamService` utilise `MyConnection.getInstance()` pour la DB
- Les dialogues sont créés dynamiquement en JavaFX (pas besoin de FXML séparé)
- La recherche utilise des requêtes paramétrées (sécurité SQL)

---

**Status** ✅ Compilé et prêt à l'emploi !

