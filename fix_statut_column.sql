-- Script de correction pour les colonnes statut

-- Vérifier la structure existante
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, CHARACTER_SET_NAME, COLLATION_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME IN ('budget', 'depense') AND COLUMN_NAME = 'statut';

-- Modifier les colonnes statut pour utiliser VARCHAR au lieu d'ENUM
-- Cela évite les problèmes d'encoding avec les accents

ALTER TABLE budget
MODIFY COLUMN statut VARCHAR(50) DEFAULT 'en attente';

ALTER TABLE depense
MODIFY COLUMN statut VARCHAR(50) DEFAULT 'en attente';

-- Vérifier que la modification a fonctionné
DESCRIBE budget;
DESCRIBE depense;

-- Tester une insertion
INSERT INTO budget (montant_alloue, montant_utilise, date_allocation, team_id, notes, statut)
VALUES (5000, 1200, NOW(), 1, 'Test budget', 'approuvé');

INSERT INTO depense (titre, montant, date_creation, statut, categorie)
VALUES ('Test depense', 150, NOW(), 'payée', 'équipement');

-- Afficher les données
SELECT * FROM budget LIMIT 5;
SELECT * FROM depense LIMIT 5;

