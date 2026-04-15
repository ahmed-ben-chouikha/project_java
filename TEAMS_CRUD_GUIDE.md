# Teams CRUD Implementation Guide

## Overview
Une implémentation complète de CRUD (Create, Read, Update, Delete) pour les équipes esports dans l'application RankUp.

## Fichiers créés

### 1. Entité Team (`entities/Team.java`)
- Classe de modèle pour représenter une équipe
- Propriétés : id, name, region, roster, record, description
- Getters et setters complets

### 2. Service TeamService (`services/TeamService.java`)
Implémente les opérations de base de données :

#### CRUD Operations:
- **`addTeam(Team)`** - Ajouter une nouvelle équipe
- **`getAllTeams()`** - Récupérer toutes les équipes
- **`getTeamById(int)`** - Récupérer une équipe spécifique
- **`updateTeam(Team)`** - Modifier une équipe existante
- **`deleteTeam(int)`** - Supprimer une équipe
- **`searchTeamsByName(String)`** - Rechercher des équipes par nom

### 3. Contrôleur TeamsController (`rankup/controllers/TeamsController.java`)
Gère l'interface utilisateur avec :
- **Affichage** : TableView avec colonnes (ID, Nom, Région, Roster, Record)
- **Ajout** : Bouton "Add Team" ouvre un dialogue de création
- **Modification** : Bouton "Edit" pour chaque ligne
- **Suppression** : Bouton "Delete" avec confirmation
- **Recherche** : Champ de recherche par nom
- **Actualisation** : Bouton Refresh

### 4. Vue FXML (`views/teams/teams.fxml`)
Interface JavaFX avec :
- Header avec titre et bouton d'ajout
- Barre de recherche avec boutons Rechercher et Actualiser
- TableView avec colonnes d'actions (Edit/Delete)
- Thème dark neon (cohérent avec l'application)

## Installation

### Étape 1 : Créer la table de base de données

Exécutez le script SQL dans MySQL :

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

-- Données d'exemple
INSERT IGNORE INTO team (name, region, roster, record, description) VALUES
('Eclipse', 'EU West', 'Rex • Nova • Byte • Kaze • Lynx', '14W / 3L', 'A dominant EU West team'),
('Apex Drift', 'NA Central', 'Milo • Vex • Raze • Sol • Flux', '11W / 5L', 'Rising stars from North America'),
('Shadow Unit', 'APAC', 'Kai • Ember • Drift • Zen • Orion', '17W / 2L', 'Champions of APAC');
```

Ou exécutez directement le fichier fourni :
```bash
mysql -u root esportdevvvvvv < setup_team_table.sql
```

### Étape 2 : Compiler le projet

```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### Étape 3 : Intégrer à la navigation

Dans votre contrôleur de navigation principal, ajoutez le lien vers TeamsController :

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

### Afficher les équipes
- L'application charge automatiquement toutes les équipes au démarrage
- La table affiche : ID, Nom, Région, Roster, Record

### Ajouter une équipe
1. Cliquez sur le bouton "➕ Add Team"
2. Remplissez les champs du formulaire
3. Cliquez sur "Save"

### Modifier une équipe
1. Cliquez sur le bouton "Edit" de la ligne
2. Modifiez les champs
3. Cliquez sur "Save"

### Supprimer une équipe
1. Cliquez sur le bouton "Delete" de la ligne
2. Confirmez la suppression

### Rechercher une équipe
1. Entrez le nom dans le champ de recherche
2. Cliquez sur "🔍 Search"
3. Cliquez sur "🔄 Refresh" pour réinitialiser

## Structure des données

### Table `team`
```
id           INT AUTO_INCREMENT PRIMARY KEY
name         VARCHAR(255) NOT NULL UNIQUE
region       VARCHAR(100) NOT NULL
roster       TEXT (joueurs séparés par •)
record       VARCHAR(50) (ex: 14W / 3L)
description  TEXT
created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

## Exemple d'utilisation en code

```java
// Ajouter une équipe
TeamService teamService = new TeamService();
Team team = new Team("Nova Crew", "EU East", "Player1 • Player2 • Player3", "10W / 2L", "Description");
teamService.addTeam(team);

// Récupérer toutes les équipes
List<Team> allTeams = teamService.getAllTeams();

// Récupérer une équipe spécifique
Team team = teamService.getTeamById(1);

// Modifier une équipe
team.setRecord("15W / 3L");
teamService.updateTeam(team);

// Supprimer une équipe
teamService.deleteTeam(1);

// Rechercher des équipes
List<Team> results = teamService.searchTeamsByName("Eclipse");
```

## Thème et Style

L'interface utilise un thème dark neon cohérent avec l'application :
- Couleur primaire : `#00d4ff` (cyan neon)
- Fond : `#1a1a2e` (dark blue/navy)
- Accent : `#16213e` (dark blue)
- Texte : `#ffffff` (blanc)

## Notes importantes

1. **Connexion DB** : Assurez-vous que MySQL est démarré et que la base `esportdevvvvvv` existe
2. **Validation** : Le nom et la région sont obligatoires
3. **Unicité** : Le nom de l'équipe doit être unique
4. **Confirmation** : La suppression nécessite une confirmation
5. **Actualisation** : Les modifications s'appliquent immédiatement dans l'interface

## Fichiers de support

- `setup_team_table.sql` - Script SQL pour créer la table et les données d'exemple

---

**Complément du projet RankUp Arena - Équipes Management System**

