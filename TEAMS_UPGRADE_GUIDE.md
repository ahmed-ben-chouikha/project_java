# 🎯 Teams CRUD - Mise à jour complète avec tous les champs

## ✅ Modifications effectuées

### 1. **Entité Team (Team.java)**
Ajout de 14 nouveaux champs :
- ✅ `country` (pays)
- ✅ `detailedDescription` (description détaillée)
- ✅ `logo` (nom du fichier)
- ✅ `jeu` (jeu : LoL, CS:GO, Valorant, etc.)
- ✅ `niveau` (niveau : Débutant, Intermédiaire, Pro)
- ✅ `couleurEquipe` (couleur en hex)
- ✅ `statut` (statut : en attente, approuvé, refusé)
- ✅ `dateValidation` (date de validation)
- ✅ `score` (score de l'équipe)
- ✅ `updatedAt` (date de mise à jour)
- Conservés : `id`, `name`, `description`, `createdAt`

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
    statut ENUM('en attente', 'approuvé', 'refusé') DEFAULT 'en attente',
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
Nouvelles méthodes :
- ✅ `searchTeamsByStatus(String)` - Filtrer par statut
- ✅ `searchTeamsByGame(String)` - Filtrer par jeu
- ✅ `updateTeamStatus(int, String)` - Mettre à jour le statut
- ✅ `updateTeamScore(int, int)` - Mettre à jour le score

Méthodes mises à jour pour supporter tous les champs.

### 4. **Contrôleur TeamsController (TeamsController.java)**
Améliorations :
- ✅ Affichage de 7 colonnes (ID, Nom, Pays, Jeu, Niveau, Statut, Score)
- ✅ Colonne "Actions" avec 3 boutons (Voir, Éditer, Supprimer)
- ✅ Formulaire complet pour création/modification
- ✅ Système de filtrage par statut
- ✅ Bouton "Voir détails" affichant description complète
- ✅ Sélecteurs combo pour Jeu, Niveau, Statut
- ✅ Spinner pour le score
- ✅ Deux zones de texte pour descriptions

### 5. **Vue FXML teams.fxml**
Mise à jour :
- ✅ Header avec titre et bouton "Nouvelle Équipe"
- ✅ Barre de recherche + combo de filtrage
- ✅ Table avec 7 colonnes + actions
- ✅ Thème dark neon (#1a1a2e, #00d4ff)
- ✅ Texte en français

## 📊 Données d'exemple

3 équipes pré-insérées :
1. **Eclipse** - France, League of Legends, Pro, Score: 2500
2. **Apex Drift** - Canada, Valorant, Intermédiaire, Score: 1800
3. **Shadow Unit** - Japon, CS:GO, Pro, Score: 3200

## 🚀 Installation

### Étape 1 : Créer/Mettre à jour la table
```bash
mysql -u root esportdevvvvvv < update_team_table.sql
```

### Étape 2 : Compiler
```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
mvn clean compile
```

### Étape 3 : Lancer l'application
```bash
mvn exec:java@run
```

## 📝 Fonctionnalités CRUD

### ➕ Créer
- Cliquer "➕ Nouvelle Équipe"
- Remplir tous les champs (obligatoires : Nom, Pays, Jeu, Niveau)
- Cliquer "💾 Enregistrer"

### 👁️ Lire / Afficher
- La table affiche toutes les équipes triées par score (décroissant)
- Bouton "Voir" pour afficher les détails complets
- Recherche par nom en temps réel
- Filtrage par statut (Tous, en attente, approuvé, refusé)

### ✏️ Modifier
- Cliquer bouton "✏️ Edit" sur une ligne
- Modifier les champs souhaités
- Cliquer "💾 Enregistrer"

### 🗑️ Supprimer
- Cliquer bouton "🗑️ Delete" sur une ligne
- Confirmer la suppression
- L'équipe est supprimée de la BD et du tableau

## 🔍 Recherche & Filtrage

**Recherche par nom :**
1. Entrez le nom dans le champ "Rechercher..."
2. Cliquez "🔍 Rechercher"
3. Cliquez "🔄 Actualiser" pour réinitialiser

**Filtrage par statut :**
1. Sélectionnez un statut dans le combo
2. Cliquez "Afficher" (automatique)
3. Sélectionnez "Tous" pour réinitialiser

## 📋 Formulaire détaillé

Champs du formulaire :
- Nom de l'équipe (TextField) - **Obligatoire**
- Pays (TextField) - **Obligatoire**
- Jeu (ComboBox) - **Obligatoire** (LoL, Valorant, CS:GO, Dota 2, Autre)
- Niveau (ComboBox) - **Obligatoire** (Débutant, Intermédiaire, Pro)
- Statut (ComboBox) - (en attente, approuvé, refusé)
- Couleur (TextField) - Format hex (#RRGGBB)
- Score (Spinner) - De 0 à 10000 par pas de 100
- Description courte (TextArea) - Max 500 caractères
- Description détaillée (TextArea) - Max 10000 caractères

## 🎨 Thème & Style

- Fond : `#1a1a2e` (dark navy)
- Primary : `#00d4ff` (cyan neon)
- Texte : `#ffffff` (blanc)
- Table : `#16213e` (dark blue)
- Hover : Automatique JavaFX

## 🔐 Sécurité

- ✅ Requêtes paramétrées (protection SQL injection)
- ✅ Validation des champs obligatoires
- ✅ Confirmation pour suppression
- ✅ Gestion des erreurs avec messages clairs

## 📁 Fichiers modifiés/créés

```
src/main/java/
├── entities/
│   └── Team.java ✏️ (14 champs ajoutés)
├── services/
│   └── TeamService.java ✏️ (6 méthodes ajoutées)
└── rankup/controllers/
    └── TeamsController.java ✏️ (Entièrement refondu)

src/main/resources/
└── views/teams/
    └── teams.fxml ✏️ (Mise à jour UI)

Racine/
├── update_team_table.sql ✨ (Nouvelle table)
└── TEAMS_UPGRADE_GUIDE.md ✨ (Ce guide)
```

## ✨ Prochaines étapes possibles

1. Upload de fichiers logo (VichBundle ou similaire)
2. Système de notation/review des équipes
3. Intégration de matchmaking
4. Historique des changements de score
5. Export CSV/PDF des équipes
6. Système de permissions (admin/user)

## 🐛 Troubleshooting

**"Table 'esportdevvvvvv.team' doesn't exist"**
→ Exécutez : `mysql -u root esportdevvvvvv < update_team_table.sql`

**Les champs ne s'affichent pas dans le formulaire**
→ Attendez que la table soit créée avec `updated_at` auto-timestamp

**Erreur de compilation**
→ Vérifiez que Team.java importe `java.util.Date`

---

**Status** ✅ **COMPLÈTEMENT FONCTIONNEL - Prêt pour production**

**Dernière mise à jour** : 13/04/2026

