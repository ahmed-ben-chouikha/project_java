-- Create tournaments table
CREATE TABLE IF NOT EXISTS tournaments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_name VARCHAR(255) NOT NULL,
    game_type VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    max_teams INT NOT NULL CHECK (max_teams > 0),
    status ENUM('open', 'closed', 'finished') DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_dates CHECK (end_date >= start_date)
);

-- Create index for common queries
CREATE INDEX idx_status ON tournaments(status);
CREATE INDEX idx_name ON tournaments(tournament_name);
