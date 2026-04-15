# 🚀 Budget & Depense - QUICK START Guide

## ⚡ Démarrage en 5 minutes

### Étape 1: Ouvrir Terminal
```bash
cd C:\Users\melki\OneDrive\Bureau\pidev\project_java
```

### Étape 2: Compiler & Lancer
```bash
mvn clean compile
mvn exec:java
```

### Étape 3: Attendre que l'app démarre
- Voir la fenêtre JavaFX s'ouvrir
- Voir la sidebar à gauche

### Étape 4: Cliquer sur "💰 Budget"
- Table de budgets s'affiche
- Voir les colonnes: ID, Équipe, Montant Alloué, etc.

### Étape 5: Cliquer sur "💸 Depense"
- Table de dépenses s'affiche
- Voir les colonnes: ID, Titre, Montant, Catégorie, etc.

---

## 🎯 Cas d'Usage Rapides

### ✅ Ajouter un Budget
```
1. Cliquer "➕ Ajouter Budget"
2. Équipe: Sélectionner "Eclipse" (ou autre)
3. Montant alloué: 5000
4. Montant utilisé: 1200
5. Statut: "approuvé"
6. Notes: (optionnel)
7. Cliquer "💾 Enregistrer"
✓ Budget créé!
```

### ✅ Ajouter une Dépense
```
1. Cliquer "➕ Ajouter Dépense"
2. Titre: "Achat clavier"
3. Montant: 150
4. Catégorie: "équipement"
5. Statut: "payée"
6. Équipe: (optionnel)
7. Cliquer "💾 Enregistrer"
✓ Dépense créée!
```

### ✅ Modifier un Budget
```
1. Table → Cliquer "✏️ Edit"
2. Modifier les champs
3. Cliquer "💾 Enregistrer"
✓ Budget modifié!
```

### ✅ Supprimer une Dépense
```
1. Table → Cliquer "🗑️ Delete"
2. Confirmer la suppression
3. Cliquer "OK"
✓ Dépense supprimée!
```

### ✅ Voir Détails
```
1. Table → Cliquer "👁️ View"
2. Voir popup avec tous les détails
3. Fermer la popup
```

### ✅ Filtrer par Statut
```
1. Dropdown "Tous" → Sélectionner "approuvé"
2. Table réfiltrée automatiquement
3. Cliquer "🔄 Rafraîchir" pour réinitialiser
```

---

## 📊 Statuts Disponibles

### Budget
- ✅ en attente
- ✅ approuvé
- ✅ refusé
- ✅ épuisé

### Depense
- ✅ en attente
- ✅ approuvé
- ✅ refusé
- ✅ payée

---

## 🏷️ Catégories Depense

- 💼 salaire
- 🖥️ équipement
- ✈️ voyage
- 📦 autre

---

## ❌ Erreurs Courantes

### "Montant alloué doit être positif"
→ Entrer un montant > 0

### "Le montant utilisé ne peut pas dépasser le montant alloué"
→ Montant utilisé doit être <= montant alloué

### "Équipe obligatoire"
→ Sélectionner une équipe dans le dropdown

### "Titre est obligatoire"
→ Entrer un titre pour la dépense

### "Statut est invalide"
→ Sélectionner un statut valide du dropdown

---

## 🎨 Interface Expliquée

```
┌──────────────────────────────────────────┐
│ Application ESportDev                    │
├──────────────────────────────────────────┤
│ SIDEBAR           │ CONTENU               │
│                   │                       │
│ Dashboard         │ Budget/Depense        │
│ Matches           │ Management            │
│ Teams             │ Interface             │
│ Players           │                       │
│ Tournaments       │ [Table avec data]     │
│ 💰 Budget    ◄───│                       │
│ 💸 Depense   ◄───│                       │
│ Notifications     │                       │
│ Admin             │                       │
└──────────────────────────────────────────┘
```

---

## 💾 Données Exemple

### Budgets Pré-chargés
```
1. Eclipse         - 5000€ alloué, 1200€ utilisé, approuvé
2. Apex Drift      - 3000€ alloué, 800€ utilisé, approuvé
3. Shadow Unit     - 7000€ alloué, 0€ utilisé, en attente
```

### Dépenses Pré-chargées
```
1. Achat équipement     - 800€, équipement, payée, Eclipse
2. Frais déplacement    - 400€, voyage, approuvée, Eclipse
3. Salaire principal    - 500€, salaire, payée, Apex
4. Maintenance serveur  - 150€, autre, en attente, Shadow
```

---

## 🔧 Dépannage Rapide

| Problème | Solution |
|----------|----------|
| App ne démarre pas | `mvn clean` puis `mvn exec:java` |
| Sidebar ne montre pas les boutons | Redémarrer l'app |
| Données ne s'affichent pas | Vérifier base de données |
| Dialog ne s'ouvre pas | Cliquer à nouveau |
| Validation ne fonctionne pas | Recompiler avec `mvn compile` |
| Tables vides | Exécuter `setup_budget_depense_tables.sql` |

---

## 📱 Clavier Shortcuts (Bonus)

```
Enter      → Confirmer saisie
Escape     → Fermer dialog
Tab        → Aller au champ suivant
Shift+Tab  → Retour au champ précédent
```

---

## 🎓 Concepts Clés

### Budget
- **Montant Alloué**: Budget initial pour l'équipe
- **Montant Utilisé**: Montant réellement dépensé
- **Restant**: Automatiquement calculé = Alloué - Utilisé

### Depense
- **Catégorie**: Type de dépense
- **Statut**: État d'approbation
- **Équipe**: Optionnel, associe à une équipe

### Relation
```
Équipe (1) ──────── (Many) Budget
Équipe (1) ──────── (Many) Depense
```

---

## 🚨 Important à Savoir

```
⚠️  Ne pas oublier de sélectionner une équipe pour les budgets
⚠️  Montants doivent être entre 0.01 et 999999.99€
⚠️  Titre dépense max 255 caractères
⚠️  Description max 5000 caractères
⚠️  Confirmations avant suppression = Protection
⚠️  Validation empêche les données invalides
```

---

## 📞 Besoin d'Aide?

1. Lire `BUDGET_DEPENSE_DOCUMENTATION.md`
2. Consulter `BUDGET_DEPENSE_README.md`
3. Vérifier `FINAL_SUMMARY.md`
4. Regarder les logs console pour erreurs

---

## 🎯 Next Steps

Une fois confortable:
1. Tester tous les statuts
2. Tester toutes les catégories
3. Tester filtrage
4. Tester modification
5. Tester suppression
6. Tester validation

---

## 💡 Pro Tips

```
💡 Utiliser "Rafraîchir" pour actualiser les données
💡 Utiliser "View" pour examiner les détails sans éditer
💡 Utiliser les filtres pour naviguer facilement
💡 Toujours confirmer avant de supprimer
💡 Les dialogues guides pour l'ordre de saisie
```

---

## ✅ Checklist de Premier Lancement

- [ ] Application démarre
- [ ] Sidebar visible
- [ ] Bouton Budget visible
- [ ] Bouton Depense visible
- [ ] Cliquer Budget → Table s'affiche
- [ ] Cliquer Depense → Table s'affiche
- [ ] Voir données exemple
- [ ] Cliquer Ajouter → Dialog s'ouvre
- [ ] Remplir champs
- [ ] Cliquer Enregistrer → Données ajoutées
- [ ] Nouvelle ligne dans table
- [ ] SUCCÈS! ✓

---

## 🎉 Félicitations!

Vous êtes maintenant prêt à utiliser le système Budget & Depense!

```
╔═══════════════════════════════════════╗
║                                       ║
║  🎉 Budget & Depense System Ready! 🎉 ║
║                                       ║
║  ✅ 2 Entités                         ║
║  ✅ 2 Services CRUD                   ║
║  ✅ 2 Contrôleurs JavaFX              ║
║  ✅ 2 FXML Interfaces                 ║
║  ✅ Validation Complète                ║
║  ✅ Sidebar Intégrée                  ║
║  ✅ Base de Données                   ║
║  ✅ Documentation Complète            ║
║                                       ║
║  👉 Prêt pour la Production! 👈       ║
║                                       ║
╚═══════════════════════════════════════╝
```

---

**Version**: 1.0  
**Date**: 14/04/2026  
**Statut**: 🚀 LIVE  
**Support**: ✅ Documentation Complète

