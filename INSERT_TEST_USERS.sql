-- ==========================================
-- Insert Test Users for Authentication
-- ==========================================

-- Admin User
INSERT INTO user (email, username, typeuser, approval_status, password, discr)
VALUES ('admin@esports.com', 'admin', 'admin', 'approved', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'user')
ON DUPLICATE KEY UPDATE
  typeuser = 'admin',
  approval_status = 'approved',
  password = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9';

-- Player 1
INSERT INTO user (email, username, typeuser, approval_status, password, discr)
VALUES ('player@esports.com', 'player1', 'player', 'approved', 'e41abfd6daf8ad3289f41e5ed0cfe8f5c705dbb40531efdf63e110118b7594f2', 'user')
ON DUPLICATE KEY UPDATE
  typeuser = 'player',
  approval_status = 'approved',
  password = 'e41abfd6daf8ad3289f41e5ed0cfe8f5c705dbb40531efdf63e110118b7594f2';

-- Player 2 (Ahmed)
INSERT INTO user (email, username, typeuser, approval_status, password, discr)
VALUES ('ahmed@esports.com', 'ahmed', 'player', 'approved', 'c5716b88f7e6d3aa4c0f0d1d170604776f9d36481ba3d99c784ef539e6166110', 'user')
ON DUPLICATE KEY UPDATE
  typeuser = 'player',
  approval_status = 'approved',
  password = 'c5716b88f7e6d3aa4c0f0d1d170604776f9d36481ba3d99c784ef539e6166110';

-- Verify users were inserted
SELECT email, username, typeuser, approval_status FROM user WHERE email IN ('admin@esports.com', 'player@esports.com', 'ahmed@esports.com');

