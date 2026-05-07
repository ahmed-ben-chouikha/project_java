-- Fix: Remove the UNIQUE constraint on reclamation_id in the punition table
-- This allows one-to-many: a reclamation can have multiple punitions.
--
-- The unique index UNIQ_3A26754CF826B5F is used by FK FK_punition_reclamation_v2,
-- so we must drop the FK first, then drop the unique index, then re-add both
-- as a regular (non-unique) index + FK.

ALTER TABLE punition DROP FOREIGN KEY FK_punition_reclamation_v2;
ALTER TABLE punition DROP INDEX UNIQ_3A26754CF826B5F;
ALTER TABLE punition ADD INDEX IDX_punition_reclamation (reclamation_id);
ALTER TABLE punition ADD CONSTRAINT FK_punition_reclamation_v2 FOREIGN KEY (reclamation_id) REFERENCES reclamation(id);
