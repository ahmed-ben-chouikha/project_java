# Budget & Depense Management System - Documentation

## Overview
Ce système gère les budgets alloués aux équipes et les dépenses associées.

## Architecture

### Entités
1. **Budget**
   - id (int, PK)
   - montant_alloue (float) - Montant initial du budget
   - montant_utilise (float) - Montant dépensé
   - date_allocation (datetime) - Date de création
   - date_modification (datetime) - Dernière modification
   - team_id (int, FK) - Équipe associée (obligatoire)
   - notes (longtext) - Notes supplémentaires
   - statut (enum) - 'en attente', 'approuvé', 'refusé', 'épuisé'
   - justificatif (varchar) - Référence justificatif

2. **Depense**
   - id (int, PK)
   - titre (varchar, NOT NULL)
   - montant (float, NOT NULL)
   - description (longtext)
   - date_creation (datetime)
   - statut (enum) - 'en attente', 'approuvé', 'refusé', 'payée'
   - categorie (enum) - 'salaire', 'équipement', 'voyage', 'autre'
   - team_id (int, FK, nullable) - Équipe associée (optionnelle)
   - facture (varchar) - Référence facture

### Relation
- Budget → Team (ManyToOne, obligatoire)
- Depense → Team (ManyToOne, optionnelle)
- Relation indirecte : Via Team (une équipe peut avoir plusieurs budgets et dépenses)

### Services

#### BudgetService
```java
// Méthodes CRUD
- addBudget(Budget) - Ajouter un nouveau budget
- getAllBudgets() - Récupérer tous les budgets
- getBudgetById(int) - Récupérer un budget par ID
- getBudgetsByTeam(int) - Récupérer les budgets d'une équipe
- updateBudget(Budget) - Mettre à jour un budget
- deleteBudget(int) - Supprimer un budget
- getBudgetsByStatus(String) - Filtrer par statut
- updateMontantUtilise(int, float) - Mettre à jour le montant utilisé
```

#### DepenseService
```java
// Méthodes CRUD
- addDepense(Depense) - Ajouter une nouvelle dépense
- getAllDepenses() - Récupérer toutes les dépenses
- getDepenseById(int) - Récupérer une dépense par ID
- getDepensesByTeam(int) - Récupérer les dépenses d'une équipe
- updateDepense(Depense) - Mettre à jour une dépense
- deleteDepense(int) - Supprimer une dépense
- getDepensesByStatus(String) - Filtrer par statut
- getDepensesByCategorie(String) - Filtrer par catégorie
- getTotalDepenses() - Total de toutes les dépenses
- getTotalDepensesByTeam(int) - Total par équipe
```

### Validation (ValidationUtil)

#### Budget Validation
```java
- validateBudget(float montantAlloue, int teamId, String statut)
  ✓ Montant > 0
  ✓ Team ID valide
  ✓ Statut dans enum
  
- validateMontantUtilise(float montantUtilise, float montantAlloue)
  ✓ Montant utilisé >= 0
  ✓ Montant utilisé <= montant alloué
  
- checkBudgetExcess(float montantAlloue, float montantUtilise)
  Retourne le dépassement (si > 0)
```

#### Depense Validation
```java
- validateDepense(String titre, float montant, String statut, String categorie)
  ✓ Titre non vide (max 255 chars)
  ✓ Montant > 0
  ✓ Statut valide
  ✓ Catégorie valide
  
- validateDescription(String) - Max 5000 caractères
- isValidStatut(String) - Vérifie les statuts valides
- isValidCategorie(String) - Vérifie les catégories valides
- isValidDepenseStatut(String) - Statuts spécifiques aux dépenses
- validateAmount(float) - Montant entre 0.01 et 999999.99
```

### Controllers JavaFX

#### BudgetController
- Affiche la liste des budgets en TableView
- Opérations : Ajouter, Modifier, Supprimer, Voir les détails
- Filtrage par statut
- Dialog modal pour édition/création
- Validation lors de la sauvegarde

#### DepenseController
- Affiche la liste des dépenses en TableView
- Opérations : Ajouter, Modifier, Supprimer, Voir les détails
- Filtrage par statut
- Dialog modal pour édition/création
- Validation lors de la sauvegarde

## Flux d'Utilisation

### 1. Créer un Budget
```
1. Cliquer sur "Ajouter Budget"
2. Sélectionner l'équipe
3. Entrer le montant alloué
4. Entrer le montant utilisé (optionnel)
5. Sélectionner le statut
6. Ajouter des notes (optionnel)
7. Cliquer sur "Enregistrer"
```

### 2. Créer une Dépense
```
1. Cliquer sur "Ajouter Dépense"
2. Entrer le titre (obligatoire)
3. Entrer le montant (obligatoire)
4. Sélectionner la catégorie
5. Sélectionner le statut
6. Sélectionner l'équipe (optionnel)
7. Ajouter la description (optionnel)
8. Ajouter la référence facture (optionnel)
9. Cliquer sur "Enregistrer"
```

### 3. Suivre le Budget
```
1. Voir le tableau des budgets
2. Colonne "Restant" = Montant Alloué - Montant Utilisé
3. Filtrer par statut
4. Vérifier si dépassement (Restant < 0)
```

## Contrôles de Saisie

### Budget
| Champ | Règles |
|-------|--------|
| Montant Alloué | Obligatoire, > 0, <= 999999.99 |
| Montant Utilisé | >= 0, <= Montant Alloué |
| Équipe | Obligatoire, ID valide |
| Statut | Obligatoire, dans enum |
| Notes | Max 5000 caractères |

### Depense
| Champ | Règles |
|-------|--------|
| Titre | Obligatoire, 1-255 caractères |
| Montant | Obligatoire, > 0, <= 999999.99 |
| Catégorie | Obligatoire, dans enum |
| Statut | Obligatoire, dans enum |
| Description | Max 5000 caractères |
| Équipe | Optionnel |
| Facture | Optionnel |

## Messages d'Erreur

### Erreurs Courantes
- "Le montant alloué doit être positif"
- "L'ID de l'équipe est invalide"
- "Le statut est invalide"
- "Le montant utilisé ne peut pas dépasser le montant alloué"
- "Le titre est obligatoire"
- "La catégorie est invalide"
- "La description ne doit pas dépasser 5000 caractères"

## Base de Données

### Table: budget
```sql
CREATE TABLE budget (
    id INT AUTO_INCREMENT PRIMARY KEY,
    montant_alloue FLOAT NOT NULL,
    montant_utilise FLOAT DEFAULT 0,
    date_allocation DATETIME NOT NULL,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP,
    team_id INT NOT NULL,
    notes LONGTEXT,
    statut ENUM('en attente', 'approuvé', 'refusé', 'épuisé') DEFAULT 'en attente',
    justificatif VARCHAR(255),
    FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE CASCADE
);
```

### Table: depense
```sql
CREATE TABLE depense (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    montant FLOAT NOT NULL,
    description LONGTEXT,
    date_creation DATETIME NOT NULL,
    statut ENUM('en attente', 'approuvé', 'refusé', 'payée') DEFAULT 'en attente',
    categorie ENUM('salaire', 'équipement', 'voyage', 'autre') DEFAULT 'autre',
    team_id INT,
    facture VARCHAR(255),
    FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE SET NULL
);
```

## Fonctionnalités Futures
- [ ] Rapports d'analyses budgétaires
- [ ] Alertes de dépassement
- [ ] Export Excel/PDF
- [ ] Graphiques de dépenses
- [ ] Approbation en workflows
- [ ] Pièces jointes (factures, justificatifs)
- [ ] Historique des modifications
- [ ] Notifications email

## Notes Importantes
1. **Intégrité Référentielle** : Les budgets sont supprimés avec l'équipe, les dépenses restent (team_id devient NULL)
2. **Validation Côté Client** : Toutes les validations sont effectuées dans ValidationUtil
3. **Thread Safety** : À améliorer pour multi-utilisateurs
4. **Performance** : Ajouter du cache pour les grandes quantités de données
5. **Sécurité** : À implémenter (permissions utilisateur, audit trail)

---
Dernière mise à jour : 14/04/2026
Version : 1.0

