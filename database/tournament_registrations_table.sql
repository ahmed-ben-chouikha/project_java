-- Create tournament_registrations table
CREATE TABLE IF NOT EXISTS tournament_registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL,
    team_name VARCHAR(255) NOT NULL,
    tournament_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'confirmed') DEFAULT 'pending',
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (player_name, tournament_id)
);

-- Create indexes for common queries
CREATE INDEX idx_player ON tournament_registrations(player_name);
CREATE INDEX idx_tournament ON tournament_registrations(tournament_id);
CREATE INDEX idx_status_reg ON tournament_registrations(status);
