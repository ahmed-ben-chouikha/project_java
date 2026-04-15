-- Create tournament_registrations table with auto-close support
CREATE TABLE IF NOT EXISTS tournament_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL,
    team_name VARCHAR(255) NOT NULL,
    tournament_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'confirmed', 'rejected') DEFAULT 'pending',
    rejection_reason VARCHAR(500),
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (player_name, tournament_id),
    INDEX idx_player (player_name),
    INDEX idx_tournament (tournament_id),
    INDEX idx_status (status)
);
