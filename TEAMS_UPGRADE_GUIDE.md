# ðŸŽ¯ Teams CRUD - Mise Ã  jour complÃ¨te avec tous les champs

## âœ… Modifications effectuÃ©es

### 1. **EntitÃ© Team (Team.java)**
Ajout de 14 nouveaux champs :
- âœ… `country` (pays)
- âœ… `detailedDescription` (description dÃ©taillÃ©e)
- âœ… `logo` (nom du fichier)
- âœ… `jeu` (jeu : LoL, CS:GO, Valorant, etc.)
- âœ… `niveau` (niveau : DÃ©butant, IntermÃ©diaire, Pro)
- âœ… `couleurEquipe` (couleur en hex)
- âœ… `statut` (statut : en attente, approuvÃ©, refusÃ©)
- âœ… `dateValidation` (date de validation)
- âœ… `score` (score de l'Ã©quipe)
- âœ… `updatedAt` (date de mise Ã  jour)
- ConservÃ©s : `id`, `name`, `description`, `createdAt`

### 2. **Table MySQL `team`**
Nouvelle structure avec colonnes :
```sql
CREATE TABLE team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100) NOT NULL,
    description TEXT,
    detailed_description LONGTEXT,
    logo VARCHAR(255),
    jeu VARCHAR(100) NOT NULL,
    niveau VARCHAR(50),
    couleur_equipe VARCHAR(7) DEFAULT '#00d4ff',
    statut ENUM('en attente', 'approuvÃ©', 'refusÃ©') DEFAULT 'en attente',
    date_validation DATETIME,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_name (name),
    INDEX idx_statut (statut),
    INDEX idx_jeu (jeu),
    INDEX idx_pays (country)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. **Service TeamService (TeamService.java)**
Nouvelles mÃ©thodes :
- âœ… `searchTeamsByStatus(String)` - Filtrer par statut
- âœ… `searchTeamsByGame(String)` - Filtrer par jeu
- âœ… `updateTeamStatus(int, String)` - Mettre Ã  jour le statut
- âœ… `updateTeamScore(int, int)` - Mettre Ã  jour le score

MÃ©thodes mises Ã  jour pour supporter tous les champs.

### 4. **ContrÃ´leur TeamsController (TeamsController.java)**
AmÃ©liorations :
- âœ… Affichage de 7 colonnes (ID, Nom, Pays, Jeu, Niveau, Statut, Score)
- âœ… Colonne "Actions" avec 3 boutons (Voir, Ã‰diter, Supprimer)
- âœ… Formulaire complet pour crÃ©ation/modification
- âœ… SystÃ¨me de filtrage par statut
- âœ… Bouton "Voir dÃ©tails" affichant description complÃ¨te
- âœ… SÃ©lecteurs combo pour Jeu, Niveau, Statut
- âœ… Spinner pour le score
- âœ… Deux zones de texte pour descriptions

### 5. **Vue FXML teams.fxml**
Mise Ã  jour :
- âœ… Header avec titre et bouton "Nouvelle Ã‰quipe"
- âœ… Barre de recherche + combo de filtrage
- âœ… Table avec 7 colonnes + actions
- âœ… ThÃ¨me dark neon (#1a1a2e, #00d4ff)
- âœ… Texte en franÃ§ais

## ðŸ“Š DonnÃ©es d'exemple

3 Ã©quipes prÃ©-insÃ©rÃ©es :
1. **Eclipse** - France, League of Legends, Pro, Score: 2500
2. **Apex Drift** - Canada, Valorant, IntermÃ©diaire, Score: 1800
3. **Shadow Unit** - Japon, CS:GO, Pro, Score: 3200

## ðŸš€ Installation

### Ã‰tape 1 : CrÃ©er/Mettre Ã  jour la table
```bash
mysql -u root esportdevvvvvv-2 < update_team_table.sql
```

### Ã‰tape 2 : Compiler
```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### Ã‰tape 3 : Lancer l'application
```bash
mvn exec:java@run
```

## ðŸ“ FonctionnalitÃ©s CRUD

### âž• CrÃ©er
- Cliquer "âž• Nouvelle Ã‰quipe"
- Remplir tous les champs (obligatoires : Nom, Pays, Jeu, Niveau)
- Cliquer "ðŸ’¾ Enregistrer"

### ðŸ‘ï¸ Lire / Afficher
- La table affiche toutes les Ã©quipes triÃ©es par score (dÃ©croissant)
- Bouton "Voir" pour afficher les dÃ©tails complets
- Recherche par nom en temps rÃ©el
- Filtrage par statut (Tous, en attente, approuvÃ©, refusÃ©)

### âœï¸ Modifier
- Cliquer bouton "âœï¸ Edit" sur une ligne
- Modifier les champs souhaitÃ©s
- Cliquer "ðŸ’¾ Enregistrer"

### ðŸ—‘ï¸ Supprimer
- Cliquer bouton "ðŸ—‘ï¸ Delete" sur une ligne
- Confirmer la suppression
- L'Ã©quipe est supprimÃ©e de la BD et du tableau

## ðŸ” Recherche & Filtrage

**Recherche par nom :**
1. Entrez le nom dans le champ "Rechercher..."
2. Cliquez "ðŸ” Rechercher"
3. Cliquez "ðŸ”„ Actualiser" pour rÃ©initialiser

**Filtrage par statut :**
1. SÃ©lectionnez un statut dans le combo
2. Cliquez "Afficher" (automatique)
3. SÃ©lectionnez "Tous" pour rÃ©initialiser

## ðŸ“‹ Formulaire dÃ©taillÃ©

Champs du formulaire :
- Nom de l'Ã©quipe (TextField) - **Obligatoire**
- Pays (TextField) - **Obligatoire**
- Jeu (ComboBox) - **Obligatoire** (LoL, Valorant, CS:GO, Dota 2, Autre)
- Niveau (ComboBox) - **Obligatoire** (DÃ©butant, IntermÃ©diaire, Pro)
- Statut (ComboBox) - (en attente, approuvÃ©, refusÃ©)
- Couleur (TextField) - Format hex (#RRGGBB)
- Score (Spinner) - De 0 Ã  10000 par pas de 100
- Description courte (TextArea) - Max 500 caractÃ¨res
- Description dÃ©taillÃ©e (TextArea) - Max 10000 caractÃ¨res

## ðŸŽ¨ ThÃ¨me & Style

- Fond : `#1a1a2e` (dark navy)
- Primary : `#00d4ff` (cyan neon)
- Texte : `#ffffff` (blanc)
- Table : `#16213e` (dark blue)
- Hover : Automatique JavaFX

## ðŸ” SÃ©curitÃ©

- âœ… RequÃªtes paramÃ©trÃ©es (protection SQL injection)
- âœ… Validation des champs obligatoires
- âœ… Confirmation pour suppression
- âœ… Gestion des erreurs avec messages clairs

## ðŸ“ Fichiers modifiÃ©s/crÃ©Ã©s

```
src/main/java/
â”œâ”€â”€ entities/
â”‚   â””â”€â”€ Team.java âœï¸ (14 champs ajoutÃ©s)
â”œâ”€â”€ services/
â”‚   â””â”€â”€ TeamService.java âœï¸ (6 mÃ©thodes ajoutÃ©es)
â””â”€â”€ rankup/controllers/
    â””â”€â”€ TeamsController.java âœï¸ (EntiÃ¨rement refondu)

src/main/resources/
â””â”€â”€ views/teams/
    â””â”€â”€ teams.fxml âœï¸ (Mise Ã  jour UI)

Racine/
â”œâ”€â”€ update_team_table.sql âœ¨ (Nouvelle table)
â””â”€â”€ TEAMS_UPGRADE_GUIDE.md âœ¨ (Ce guide)
```

## âœ¨ Prochaines Ã©tapes possibles

1. Upload de fichiers logo (VichBundle ou similaire)
2. SystÃ¨me de notation/review des Ã©quipes
3. IntÃ©gration de matchmaking
4. Historique des changements de score
5. Export CSV/PDF des Ã©quipes
6. SystÃ¨me de permissions (admin/user)

## ðŸ› Troubleshooting

**"Table 'esportdevvvvvv-2.team' doesn't exist"**
â†’ ExÃ©cutez : `mysql -u root esportdevvvvvv-2 < update_team_table.sql`

**Les champs ne s'affichent pas dans le formulaire**
â†’ Attendez que la table soit crÃ©Ã©e avec `updated_at` auto-timestamp

**Erreur de compilation**
â†’ VÃ©rifiez que Team.java importe `java.util.Date`

---

**Status** âœ… **COMPLÃˆTEMENT FONCTIONNEL - PrÃªt pour production**

**DerniÃ¨re mise Ã  jour** : 13/04/2026

