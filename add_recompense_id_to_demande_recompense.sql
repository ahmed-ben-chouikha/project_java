-- Migration: aligner la table demande_recompense avec le CRUD actuel
USE esportdevvvvvv;

ALTER TABLE demande_recompense
    ADD COLUMN IF NOT EXISTS recompense_id INT(11) NULL AFTER statut;

ALTER TABLE demande_recompense
    ADD CONSTRAINT fk_demande_recompense
    FOREIGN KEY (recompense_id) REFERENCES recompense(id) ON DELETE SET NULL;

-- Normalisation des statuts historiques
UPDATE demande_recompense
SET statut = 'en_attente'
WHERE LOWER(TRIM(statut)) IN ('en attente', 'en_attente');

UPDATE demande_recompense
SET statut = 'approuvee'
WHERE LOWER(TRIM(statut)) IN ('approuvee', 'approuvée');

UPDATE demande_recompense
SET statut = 'rejetee'
WHERE LOWER(TRIM(statut)) IN ('rejetee', 'rejetée');

