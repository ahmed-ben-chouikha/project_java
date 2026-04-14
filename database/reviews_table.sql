-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL,
    tournament_id INT NOT NULL,
    tournament_name VARCHAR(255) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    review_date DATE NOT NULL,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    CONSTRAINT check_comment_length CHECK (CHAR_LENGTH(comment) >= 10 AND CHAR_LENGTH(comment) <= 300),
    CONSTRAINT unique_player_tournament UNIQUE (player_name, tournament_id),
    INDEX idx_player ON reviews(player_name),
    INDEX idx_tournament ON reviews(tournament_id),
    INDEX idx_status ON reviews(status)
);
