# Module de Gestion des Récompenses - RankUp E-Sports

## 📋 Vue d'ensemble

Ce module permet une gestion complète des récompenses et des demandes de récompenses dans l'application RankUp E-Sports. Vous pouvez créer, lire, modifier et supprimer des récompenses et des demandes.

## 🗄️ Configuration de la Base de Données

### Prérequis
- XAMPP v3.3.0 ou supérieur
- MySQL/MariaDB en cours d'exécution
- phpMyAdmin (optional but recommended)

### Création de la base de données

#### Option 1 : Via phpMyAdmin (Recommandé)
1. Ouvrez phpMyAdmin : `http://localhost/phpmyadmin`
2. Créez une nouvelle base de données nommée `esportdevvv`
3. Sélectionnez la base de données
4. Allez à l'onglet "SQL"
5. Copiez et collez le contenu du fichier `database_setup.sql`
6. Cliquez sur "Exécuter"

#### Option 2 : Via MySQL Command Line
```bash
# Ouvrez un terminal/CMD et naviguez vers le répertoire du projet
cd C:\xampp\mysql\bin

# Exécutez le script SQL
mysql -u root < "C:\path\to\project\database_setup.sql"
```

#### Option 3 : Via MySQL Workbench
1. Ouvrez MySQL Workbench
2. Connectez-vous à votre serveur MySQL local
3. Fichier → Ouvrir Script SQL
4. Sélectionnez `database_setup.sql`
5. Exécutez le script (Ctrl + Shift + Enter)

## 📁 Structure des Fichiers

```
module-reward/
├── database_setup.sql                    # Script SQL pour créer les tables
│
├── Controllers/
│   ├── rewards/
│   │   ├── RecompenseController.java            # Gestion de la liste des récompenses
│   │   ├── RecompenseFormController.java        # Formulaire CRUD pour récompense
│   │   ├── DemandeRecompenseController.java     # Gestion de la liste des demandes
│   │   ├── DemandeFormController.java           # Formulaire CRUD pour demande
│   │   └── DemandeDetailController.java         # Vue détail et approbation
│
├── Entities/
│   ├── Recompense.java                  # Modèle d'entité récompense
│   └── DemandeRecompense.java           # Modèle d'entité demande
│
├── Services/
│   ├── RecompenseService.java           # Service CRUD pour récompenses
│   └── DemandeRecompenseService.java    # Service CRUD pour demandes
│
└── Views/
    └── rewards/
        ├── recompense-list.fxml             # Liste des récompenses
        ├── recompense-form.fxml             # Formulaire récompense (Create/Edit)
        ├── demande-list.fxml                # Liste des demandes
        ├── demande-form.fxml                # Formulaire demande (Create/Edit)
        └── demande-detail.fxml              # Vue détail avec approbation
```

## 🎯 Fonctionnalités

### Récompenses (Recompense)
- ✅ **CREATE** : Ajouter une nouvelle récompense
- ✅ **READ** : Afficher la liste et les détails
- ✅ **UPDATE** : Modifier une récompense existante
- ✅ **DELETE** : Supprimer une récompense
- 🔍 **SEARCH** : Filtrer par nom ou description
- 📊 **FILTER** : Filtrer par type (Or, Argent, Bronze)

### Demandes de Récompense (DemandeRecompense)
- ✅ **CREATE** : Créer une nouvelle demande
- ✅ **READ** : Consulter la liste et les détails
- ✅ **UPDATE** : Modifier une demande
- ✅ **DELETE** : Supprimer une demande
- ✅ **APPROVE** : Approuver une demande
- ✅ **REJECT** : Rejeter une demande
- 🔍 **SEARCH** : Rechercher par nom ou email
- 📊 **FILTER** : Filtrer par statut (En attente, Approuvée, Rejetée)

## ✅ Validations et Contrôles de Saisie

### Récompense
| Champ | Validation | Message d'erreur |
|-------|-----------|------------------|
| `recompense` | Max 30 caractères | ❌ Dépasse 30 caractères |
| `type` | Obligatoire (Or, Argent, Bronze) | ❌ Sélectionner un type |
| `classement` | Nombre positif | ❌ Doit être un nombre positif |
| `description` | Max 500 caractères | ❌ Dépasse 500 caractères |
| `tournament_id` | Nombre positif | ❌ L'ID doit être positif |

### Demande de Récompense
| Champ | Validation | Message d'erreur |
|-------|-----------|------------------|
| `nom_demandeur` | 3-255 caractères | ❌ Le nom doit avoir min 3 caractères |
| `email` | Format email valide | ❌ Format email invalide |
| `motif` | Min 10 caractères | ⚠️ Au minimum 10 caractères |
| `statut` | Obligatoire | ❌ Veuillez sélectionner un statut |

## 🎨 Thème et Styles

Le module utilise le thème CSS RankUp avec :
- **Couleurs primaires** : Bleu ciel (#38bdf8) et Violet (#8b5cf6)
- **Fond** : Dégradé bleu foncé
- **Messages d'erreur** : Texte rouge (#ef4444) sous chaque champ
- **Bordures d'erreur** : Bordure rouge en cas de validation échouée

## 📊 Schéma de Base de Données

### Table: recompense
```sql
- id (INT) : Clé primaire auto-incrémentée
- recompense (VARCHAR 30) : Nom de la récompense
- type (VARCHAR 50) : Type (Or, Argent, Bronze)
- classement (INT) : Position du classement
- description (LONGTEXT) : Description détaillée
- tournament_id (INT) : Référence au tournoi
```

### Table: demande_recompense
```sql
- id (INT) : Clé primaire auto-incrémentée
- nom_demandeur (VARCHAR 255) : Nom du demandeur
- email (VARCHAR 255) : Email du demandeur
- motif (LONGTEXT) : Raison de la demande
- date_demande (DATETIME) : Date de création
- statut (VARCHAR 50) : En attente / Approuvée / Rejetée
```

## 🚀 Utilisation

### Accéder au module
1. Lancez l'application RankUp
2. Navigquez vers le menu "Récompenses"
3. Vous avez accès à deux sections :
   - **Gestion des Récompenses** : Gérer les récompenses du tournoi
   - **Gestion des Demandes** : Consulter et approuver les demandes

### Ajouter une récompense
1. Cliquez sur "+ Ajouter"
2. Remplissez les champs (validation en temps réel)
3. Cliquez sur "💾 Enregistrer"

### Approuver une demande
1. Allez à "Gestion des Demandes"
2. Sélectionnez une demande "En attente"
3. Cliquez sur "👁️ Voir"
4. Cliquez sur "✅ Approuver" ou "❌ Rejeter"

## 🔧 Configuration et Déploiement

### Vérifier la connexion à la base de données
Modifiez le fichier `MyConnection.java` si nécessaire :
```java
private String url="jdbc:mysql://localhost:3306/esportdevvv?useSSL=false&serverTimezone=UTC";
private String login="root";
private String pwd="";  // Mettez à jour si vous avez un mot de passe
```

### Compiler et exécuter
```bash
mvn clean install
mvn javafx:run
```

## 📝 Données d'Exemple

La base de données est pré-remplie avec :
- 7 récompenses d'exemple (Or, Argent, Bronze)
- 5 demandes de récompense en différents statuts

Pour ajouter vos propres données, utilisez les formulaires CRUD.

## 🐛 Dépannage

### Erreur de connexion à la base de données
- ✅ Vérifiez que XAMPP est en cours d'exécution
- ✅ Vérifiez que MySQL est démarré
- ✅ Vérifiez les identifiants dans `MyConnection.java`
- ✅ Vérifiez que la base de données `esportdevvv` existe

### Erreur de validation
- Les messages d'erreur s'affichent en rouge sous les champs
- Corrigez les valeurs selon les indications
- Les boutons de sauvegarde restent inactifs jusqu'à la validation

## 📞 Support
Pour toute question ou problème, veuillez contacter l'administrateur système.

---
**Version** : 1.0  
**Dernière mise à jour** : Avril 2024

