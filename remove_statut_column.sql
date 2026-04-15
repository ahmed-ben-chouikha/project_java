-- Supprimer la colonne statut des tables team et budget

-- Vérifier les colonnes avant suppression
SELECT TABLE_NAME, COLUMN_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('team', 'budget') AND COLUMN_NAME = 'statut'
ORDER BY TABLE_NAME;

-- Supprimer la colonne statut de la table team si elle existe
ALTER TABLE team DROP COLUMN IF EXISTS statut;

-- Supprimer la colonne statut de la table budget
ALTER TABLE budget DROP COLUMN IF EXISTS statut;

-- Vérifier que la suppression a fonctionné
DESCRIBE team;
DESCRIBE budget;

-- Vérifier les données restantes
SELECT COUNT(*) as budget_count FROM budget;
SELECT COUNT(*) as team_count FROM team;

-- Afficher les colonnes finales
SELECT * FROM budget LIMIT 3;
SELECT * FROM team LIMIT 3;

