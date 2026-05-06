-- Enforce one budget per team
-- 1) Keep the latest budget row per team, remove older duplicates.
DELETE b1 FROM budget b1
JOIN budget b2
  ON b1.team_id = b2.team_id
 AND b1.id < b2.id;

-- 2) Add a unique constraint so one team cannot own multiple budgets.
ALTER TABLE budget
ADD CONSTRAINT uq_budget_team UNIQUE (team_id);
