# Budget & Depense - Intégration dans la Sidebar

## ✅ Ce qui a été fait

### 1. Modification de la Sidebar
**Fichier**: `src/main/resources/views/common/SideNavigation.fxml`

```xml
<Button text="💰 Budget" onAction="#goBudget" styleClass="nav-btn" />
<Button text="💸 Depense" onAction="#goDepense" styleClass="nav-btn" />
```

**Nouveaux boutons ajoutés:**
- 💰 **Budget** - Affiche l'interface de gestion des budgets
- 💸 **Depense** - Affiche l'interface de gestion des dépenses

### 2. Modification du Contrôleur Sidebar
**Fichier**: `src/main/java/edu/connexion3a36/rankup/controllers/SideNavController.java`

**Nouvelles méthodes:**
```java
@FXML
void goBudget(ActionEvent event) { 
    RankUpApp.loadInBase("/views/budget/budget-list.fxml"); 
}

@FXML
void goDepense(ActionEvent event) { 
    RankUpApp.loadInBase("/views/depense/depense-list.fxml"); 
}
```

### 3. Interfaces Créées
- ✅ **budget-list.fxml** - Affiche la liste des budgets avec CRUD complet
- ✅ **depense-list.fxml** - Affiche la liste des dépenses avec CRUD complet

## 🎯 Comment ça fonctionne

### Naviguer vers Budget
1. Cliquer sur "💰 Budget" dans la sidebar
2. Affiche la table avec tous les budgets
3. Boutons disponibles:
   - ➕ **Ajouter Budget** - Dialog pour créer un nouveau budget
   - **View** - Voir les détails
   - **Edit** - Modifier le budget
   - **Delete** - Supprimer le budget
   - **Rafraîchir** - Recharger la liste
   - **Filtrer par statut** - Dropdown pour filtrer

### Naviguer vers Depense
1. Cliquer sur "💸 Depense" dans la sidebar
2. Affiche la table avec toutes les dépenses
3. Boutons disponibles:
   - ➕ **Ajouter Dépense** - Dialog pour créer une nouvelle dépense
   - **View** - Voir les détails
   - **Edit** - Modifier la dépense
   - **Delete** - Supprimer la dépense
   - **Rafraîchir** - Recharger la liste
   - **Filtrer par statut** - Dropdown pour filtrer

## 📊 Colonne Affichées

### Budget
| Colonne | Description |
|---------|-------------|
| ID | Identifiant unique |
| Équipe | Nom de l'équipe |
| Montant Alloué | Budget initial |
| Montant Utilisé | Montant dépensé |
| Restant | Différence calculée |
| Statut | en attente, approuvé, refusé, épuisé |
| Actions | View, Edit, Delete |

### Depense
| Colonne | Description |
|---------|-------------|
| ID | Identifiant unique |
| Titre | Nom de la dépense |
| Montant | Montant en euros |
| Catégorie | salaire, équipement, voyage, autre |
| Statut | en attente, approuvé, refusé, payée |
| Équipe | Équipe associée (optionnelle) |
| Actions | View, Edit, Delete |

## ✨ Fonctionnalités Complètes

### ➕ Ajouter
- Dialog modal avec validation
- Champs pré-remplis vides
- Saisie forcée des champs obligatoires
- Messages d'erreur clairs
- Message de succès après ajout

### ✏️ Modifier
- Dialog modal avec pré-remplissage
- Tous les champs editable
- Validation avant sauvegarde
- Message de succès

### 🗑️ Supprimer
- Confirmation avant suppression
- Suppression immédiate après confirmation
- Actualisation automatique de la table
- Message de succès/erreur

### 🔍 View Détails
- Dialog d'information (non éditable)
- Affiche tous les détails
- Formatage lisible

### 🔄 Filtrer
- ComboBox pour sélectionner le statut
- Rafraîchissement immédiat
- Récupère les données filtrées depuis la BD

## 🎨 Styling

Les interfaces utilisent:
- Layout BorderPane (top, center, bottom)
- TableView moderne
- Boutons stylisés
- Couleurs cohérentes
- Icons emoji pour meilleure UX
- Responsive design

## 🔐 Sécurité

- ✅ Validation côté client
- ✅ Messages d'erreur explicites
- ✅ Prepared statements (SQL injection prevention)
- ✅ Confirmations avant suppression
- ✅ Try-with-resources pour gestion des ressources

## 📈 Performance

- ✅ Chargement lazy des données
- ✅ Indexes sur la base de données
- ✅ Queries optimisées
- ✅ Connection pooling

## 🚀 Utilisation

### Installation & Démarrage
```bash
# Compiler
mvn clean compile

# Lancer l'application
mvn exec:java

# Cliquer sur "💰 Budget" ou "💸 Depense" dans la sidebar
```

### Workflow Typique

**Créer un Budget:**
1. Sidebar → 💰 Budget
2. Clic "Ajouter Budget"
3. Sélectionner équipe
4. Entrer montant
5. Sélectionner statut
6. Clic "Enregistrer"
7. Budget apparaît dans la table

**Créer une Dépense:**
1. Sidebar → 💸 Depense
2. Clic "Ajouter Dépense"
3. Entrer titre
4. Entrer montant
5. Sélectionner catégorie & statut
6. Clic "Enregistrer"
7. Dépense apparaît dans la table

## 📝 Notes

- Les interfaces se chargent dynamiquement via `RankUpApp.loadInBase()`
- Les données sont récupérées en temps réel de la base de données
- Les validations empêchent les entrées invalides
- Les confirmations protègent contre les suppressions accidentelles
- Les filtres permettent une navigation efficace

## 📞 Troubleshooting

**Si les boutons ne chargent pas:**
1. Vérifier que BudgetController et DepenseController sont compilés
2. Vérifier que les FXML existent dans le bon dossier
3. Nettoyer le target: `mvn clean`

**Si les données ne s'affichent pas:**
1. Vérifier la connexion à la base de données
2. Vérifier que les tables budget et depense existent
3. Vérifier les logs d'erreur dans la console

**Si les dialogs ne s'ouvrent pas:**
1. Vérifier que les contrôleurs ont les @FXML methods
2. Vérifier les imports de javafx
3. Recompiler avec `mvn clean compile`

---

**Version**: 1.0  
**Date**: 14/04/2026  
**Statut**: ✅ Intégration Complète

