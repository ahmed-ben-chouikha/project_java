-- No migration needed - simplified schema with only player_name, team_name, tournament_id, status
-- The registration table will use the standard schema without extra fields

-- If you have extra columns (team_members, contact_info), you can optionally remove them:
-- ALTER TABLE tournament_registrations DROP COLUMN IF EXISTS team_members;
-- ALTER TABLE tournament_registrations DROP COLUMN IF EXISTS contact_info;



