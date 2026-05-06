-- =====================================
-- Users Table for Authentication
-- =====================================

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('admin', 'player') DEFAULT 'player',
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- Sample Users (Passwords hashed with SHA-256)
-- =====================================
-- Admin user: admin@esports.com / admin123
-- Player user: player@esports.com / player123
-- Ahmed user: ahmed@esports.com / ahmed123

INSERT IGNORE INTO users (email, username, password, role, status) VALUES
('admin@esports.com', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin', 'active'),
('player@esports.com', 'player1', 'e41abfd6daf8ad3289f41e5ed0cfe8f5c705dbb40531efdf63e110118b7594f2', 'player', 'active'),
('ahmed@esports.com', 'ahmed', 'c5716b88f7e6d3aa4c0f0d1d170604776f9d36481ba3d99c784ef539e6166110', 'player', 'active');



