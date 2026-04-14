-- Script de migration : Ajoute la colonne country à la table team
-- Cette colonne est nécessaire pour la classe Team.java

-- Vérifier si la colonne n'existe pas avant de l'ajouter
-- Ajoute la colonne country à la table team
ALTER TABLE team ADD COLUMN country VARCHAR(100) NOT NULL DEFAULT 'France' AFTER name;

-- Ajoute un index sur la colonne country pour les recherches
ALTER TABLE team ADD INDEX idx_pays (country);

-- Mets à jour les colonnes existantes avec une valeur par défaut si besoin
-- Les colonnes qui manquent seront ajoutées aussi
ALTER TABLE team ADD COLUMN IF NOT EXISTS detailed_description LONGTEXT;
ALTER TABLE team ADD COLUMN IF NOT EXISTS logo VARCHAR(255);
ALTER TABLE team ADD COLUMN IF NOT EXISTS jeu VARCHAR(100) NOT NULL DEFAULT 'League of Legends';
ALTER TABLE team ADD COLUMN IF NOT EXISTS niveau VARCHAR(50) DEFAULT 'Débutant';
ALTER TABLE team ADD COLUMN IF NOT EXISTS couleur_equipe VARCHAR(7) DEFAULT '#00d4ff';
ALTER TABLE team ADD COLUMN IF NOT EXISTS statut ENUM('en attente', 'approuvé', 'refusé') DEFAULT 'en attente';
ALTER TABLE team ADD COLUMN IF NOT EXISTS date_validation DATETIME;
ALTER TABLE team ADD COLUMN IF NOT EXISTS score INT DEFAULT 0;
ALTER TABLE team ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Affiche la structure finale de la table
DESCRIBE team;

