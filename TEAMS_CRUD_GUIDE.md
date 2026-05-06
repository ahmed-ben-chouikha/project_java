# Teams CRUD Implementation Guide

## Overview
Une implÃ©mentation complÃ¨te de CRUD (Create, Read, Update, Delete) pour les Ã©quipes esports dans l'application RankUp.

## Fichiers crÃ©Ã©s

### 1. EntitÃ© Team (`entities/Team.java`)
- Classe de modÃ¨le pour reprÃ©senter une Ã©quipe
- PropriÃ©tÃ©s : id, name, region, roster, record, description
- Getters et setters complets

### 2. Service TeamService (`services/TeamService.java`)
ImplÃ©mente les opÃ©rations de base de donnÃ©es :

#### CRUD Operations:
- **`addTeam(Team)`** - Ajouter une nouvelle Ã©quipe
- **`getAllTeams()`** - RÃ©cupÃ©rer toutes les Ã©quipes
- **`getTeamById(int)`** - RÃ©cupÃ©rer une Ã©quipe spÃ©cifique
- **`updateTeam(Team)`** - Modifier une Ã©quipe existante
- **`deleteTeam(int)`** - Supprimer une Ã©quipe
- **`searchTeamsByName(String)`** - Rechercher des Ã©quipes par nom

### 3. ContrÃ´leur TeamsController (`rankup/controllers/TeamsController.java`)
GÃ¨re l'interface utilisateur avec :
- **Affichage** : TableView avec colonnes (ID, Nom, RÃ©gion, Roster, Record)
- **Ajout** : Bouton "Add Team" ouvre un dialogue de crÃ©ation
- **Modification** : Bouton "Edit" pour chaque ligne
- **Suppression** : Bouton "Delete" avec confirmation
- **Recherche** : Champ de recherche par nom
- **Actualisation** : Bouton Refresh

### 4. Vue FXML (`views/teams/teams.fxml`)
Interface JavaFX avec :
- Header avec titre et bouton d'ajout
- Barre de recherche avec boutons Rechercher et Actualiser
- TableView avec colonnes d'actions (Edit/Delete)
- ThÃ¨me dark neon (cohÃ©rent avec l'application)

## Installation

### Ã‰tape 1 : CrÃ©er la table de base de donnÃ©es

ExÃ©cutez le script SQL dans MySQL :

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

-- DonnÃ©es d'exemple
INSERT IGNORE INTO team (name, region, roster, record, description) VALUES
('Eclipse', 'EU West', 'Rex â€¢ Nova â€¢ Byte â€¢ Kaze â€¢ Lynx', '14W / 3L', 'A dominant EU West team'),
('Apex Drift', 'NA Central', 'Milo â€¢ Vex â€¢ Raze â€¢ Sol â€¢ Flux', '11W / 5L', 'Rising stars from North America'),
('Shadow Unit', 'APAC', 'Kai â€¢ Ember â€¢ Drift â€¢ Zen â€¢ Orion', '17W / 2L', 'Champions of APAC');
```

Ou exÃ©cutez directement le fichier fourni :
```bash
mysql -u root esportdevvvvvv-2 < setup_team_table.sql
```

### Ã‰tape 2 : Compiler le projet

```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### Ã‰tape 3 : IntÃ©grer Ã  la navigation

Dans votre contrÃ´leur de navigation principal, ajoutez le lien vers TeamsController :

```java
@FXML
void showTeams(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/teams/teams.fxml"));
        loader.setController(new TeamsController());
        contentPane.setCenter(loader.load());
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Utilisation

### Afficher les Ã©quipes
- L'application charge automatiquement toutes les Ã©quipes au dÃ©marrage
- La table affiche : ID, Nom, RÃ©gion, Roster, Record

### Ajouter une Ã©quipe
1. Cliquez sur le bouton "âž• Add Team"
2. Remplissez les champs du formulaire
3. Cliquez sur "Save"

### Modifier une Ã©quipe
1. Cliquez sur le bouton "Edit" de la ligne
2. Modifiez les champs
3. Cliquez sur "Save"

### Supprimer une Ã©quipe
1. Cliquez sur le bouton "Delete" de la ligne
2. Confirmez la suppression

### Rechercher une Ã©quipe
1. Entrez le nom dans le champ de recherche
2. Cliquez sur "ðŸ” Search"
3. Cliquez sur "ðŸ”„ Refresh" pour rÃ©initialiser

## Structure des donnÃ©es

### Table `team`
```
id           INT AUTO_INCREMENT PRIMARY KEY
name         VARCHAR(255) NOT NULL UNIQUE
region       VARCHAR(100) NOT NULL
roster       TEXT (joueurs sÃ©parÃ©s par â€¢)
record       VARCHAR(50) (ex: 14W / 3L)
description  TEXT
created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

## Exemple d'utilisation en code

```java
// Ajouter une Ã©quipe
TeamService teamService = new TeamService();
Team team = new Team("Nova Crew", "EU East", "Player1 â€¢ Player2 â€¢ Player3", "10W / 2L", "Description");
teamService.addTeam(team);

// RÃ©cupÃ©rer toutes les Ã©quipes
List<Team> allTeams = teamService.getAllTeams();

// RÃ©cupÃ©rer une Ã©quipe spÃ©cifique
Team team = teamService.getTeamById(1);

// Modifier une Ã©quipe
team.setRecord("15W / 3L");
teamService.updateTeam(team);

// Supprimer une Ã©quipe
teamService.deleteTeam(1);

// Rechercher des Ã©quipes
List<Team> results = teamService.searchTeamsByName("Eclipse");
```

## ThÃ¨me et Style

L'interface utilise un thÃ¨me dark neon cohÃ©rent avec l'application :
- Couleur primaire : `#00d4ff` (cyan neon)
- Fond : `#1a1a2e` (dark blue/navy)
- Accent : `#16213e` (dark blue)
- Texte : `#ffffff` (blanc)

## Notes importantes

1. **Connexion DB** : Assurez-vous que MySQL est dÃ©marrÃ© et que la base `esportdevvvvvv-2` existe
2. **Validation** : Le nom et la rÃ©gion sont obligatoires
3. **UnicitÃ©** : Le nom de l'Ã©quipe doit Ãªtre unique
4. **Confirmation** : La suppression nÃ©cessite une confirmation
5. **Actualisation** : Les modifications s'appliquent immÃ©diatement dans l'interface

## Fichiers de support

- `setup_team_table.sql` - Script SQL pour crÃ©er la table et les donnÃ©es d'exemple

---

**ComplÃ©ment du projet RankUp Arena - Ã‰quipes Management System**

