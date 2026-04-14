-- Nettoie la table team en supprimant les colonnes obsolètes
-- et en ajoutant des valeurs par défaut aux colonnes requises

-- Supprime les colonnes obsolètes de l'ancien schéma
ALTER TABLE team DROP COLUMN IF EXISTS region;
ALTER TABLE team DROP COLUMN IF EXISTS roster;
ALTER TABLE team DROP COLUMN IF EXISTS record;

-- Ajoute des valeurs par défaut aux colonnes requises
ALTER TABLE team MODIFY COLUMN jeu VARCHAR(100) NOT NULL DEFAULT 'League of Legends';
ALTER TABLE team MODIFY COLUMN niveau VARCHAR(50) DEFAULT 'Débutant';
ALTER TABLE team MODIFY COLUMN description TEXT DEFAULT NULL;
ALTER TABLE team MODIFY COLUMN detailed_description LONGTEXT DEFAULT NULL;
ALTER TABLE team MODIFY COLUMN logo VARCHAR(255) DEFAULT NULL;
ALTER TABLE team MODIFY COLUMN statut ENUM('en attente', 'approuvé', 'refusé') DEFAULT 'en attente';

-- Affiche la structure finale de la table
DESCRIBE team;

