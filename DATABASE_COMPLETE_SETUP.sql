-- =====================================
-- RankUp E-Sports Database Setup
-- =====================================
-- This script creates the complete database schema for the RankUp platform
-- Database: esportdevvvvvv
-- Run this script in MySQL to set up all tables

-- Create Database if it doesn't exist
CREATE DATABASE IF NOT EXISTS esportdevvvvvv;
USE esportdevvvvvv;

-- =====================================
-- 1. Users Table
-- =====================================
CREATE TABLE IF NOT EXISTS `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) DEFAULT 'PLAYER',
    status VARCHAR(50) DEFAULT 'ACTIVE',
    otp_code VARCHAR(10),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 2. Teams Table
-- =====================================
CREATE TABLE IF NOT EXISTS `team` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    captain_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (captain_id) REFERENCES `user`(id) ON DELETE CASCADE,
    INDEX idx_captain (captain_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 3. Team Members Table
-- =====================================
CREATE TABLE IF NOT EXISTS `team_member` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    team_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(50) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES `team`(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    UNIQUE KEY unique_team_member (team_id, user_id),
    INDEX idx_team (team_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 4. Tournaments Table
-- =====================================
CREATE TABLE IF NOT EXISTS `tournament` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status VARCHAR(50) DEFAULT 'UPCOMING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_start_date (start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 5. Tournament Registrations Table
-- =====================================
CREATE TABLE IF NOT EXISTS `tournament_registration` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team_id INT,
    user_id INT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'REGISTERED',
    FOREIGN KEY (tournament_id) REFERENCES `tournament`(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES `team`(id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE SET NULL,
    INDEX idx_tournament (tournament_id),
    INDEX idx_team (team_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 6. Matches Table
-- =====================================
CREATE TABLE IF NOT EXISTS `match_record` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    team1_id INT,
    team2_id INT,
    player1_id INT,
    player2_id INT,
    winner_id INT,
    score_team1 INT DEFAULT 0,
    score_team2 INT DEFAULT 0,
    match_date DATETIME,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES `tournament`(id) ON DELETE CASCADE,
    FOREIGN KEY (team1_id) REFERENCES `team`(id) ON DELETE SET NULL,
    FOREIGN KEY (team2_id) REFERENCES `team`(id) ON DELETE SET NULL,
    FOREIGN KEY (player1_id) REFERENCES `user`(id) ON DELETE SET NULL,
    FOREIGN KEY (player2_id) REFERENCES `user`(id) ON DELETE SET NULL,
    FOREIGN KEY (winner_id) REFERENCES `user`(id) ON DELETE SET NULL,
    INDEX idx_tournament (tournament_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 7. Reviews/Ratings Table
-- =====================================
CREATE TABLE IF NOT EXISTS `review` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    match_id INT NOT NULL,
    reviewer_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES `match_record`(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES `user`(id) ON DELETE CASCADE,
    INDEX idx_match (match_id),
    INDEX idx_reviewer (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 8. Budget/Expenses Table
-- =====================================
CREATE TABLE IF NOT EXISTS `budget_expense` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    team_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES `team`(id) ON DELETE CASCADE,
    INDEX idx_team (team_id),
    INDEX idx_date (expense_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================
-- 9. Sample Data
-- =====================================

-- Insert test users
INSERT IGNORE INTO `user` (email, password, username, role, status) VALUES
('admin@rankup.gg', 'password123', 'admin', 'ADMIN', 'ACTIVE'),
('manager@rankup.gg', 'password123', 'teamlead', 'MANAGER', 'ACTIVE'),
('player1@rankup.gg', 'password123', 'falconx', 'PLAYER', 'ACTIVE'),
('player2@rankup.gg', 'password123', 'vortex7', 'PLAYER', 'ACTIVE'),
('player3@rankup.gg', 'password123', 'phoenix99', 'PLAYER', 'ACTIVE'),
('test@rankup.gg', 'password123', 'testuser', 'PLAYER', 'ACTIVE');

-- Insert sample teams
INSERT IGNORE INTO `team` (name, description, captain_id) VALUES
('Phoenix Warriors', 'Elite competitive team', 2),
('Dragon Slayers', 'Rising star team', 3),
('Shadow Legends', 'Professional esports team', 4);

-- =====================================
-- 10. Indexes for Performance
-- =====================================
-- Indexes are already created above as part of table definitions
-- Additional indexes can be added here as needed

-- =====================================
-- 11. View Privileges (Optional)
-- =====================================
-- GRANT ALL PRIVILEGES ON esportdevvvvvv.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

-- =====================================
-- End of Database Setup
-- =====================================

