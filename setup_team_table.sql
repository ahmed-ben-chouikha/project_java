-- Create team table if it doesn't exist
CREATE TABLE IF NOT EXISTS team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100) NOT NULL,
    description TEXT,
    detailed_description LONGTEXT,
    logo VARCHAR(255),
    jeu VARCHAR(100) NOT NULL,
    niveau VARCHAR(50),
    couleur_equipe VARCHAR(7) DEFAULT '#00d4ff',
    statut ENUM('en attente', 'approuvé', 'refusé') DEFAULT 'en attente',
    date_validation DATETIME,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_name (name),
    INDEX idx_statut (statut),
    INDEX idx_jeu (jeu),
    INDEX idx_pays (country)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data (optional)
INSERT IGNORE INTO team (name, country, description, detailed_description, jeu, niveau, couleur_equipe, statut, score, date_validation) VALUES
('Eclipse', 'France', 'Équipe dominante EU', 'Équipe avec beaucoup d\'expérience en compétition internationale', 'League of Legends', 'Pro', '#00d4ff', 'approuvé', 2500, NOW()),
('Apex Drift', 'Canada', 'Équipe montante NA', 'Jeune équipe avec du potentiel énorme et un style agressif', 'Valorant', 'Intermédiaire', '#ff00ff', 'approuvé', 1800, NOW()),
('Shadow Unit', 'Japon', 'Champions APAC', 'Meilleure équipe de la région APAC avec coordination impeccable', 'CS:GO', 'Pro', '#ffff00', 'approuvé', 3200, NOW());

