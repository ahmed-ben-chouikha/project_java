-- Créer la table budget
CREATE TABLE IF NOT EXISTS budget (
    id INT AUTO_INCREMENT PRIMARY KEY,
    montant_alloue FLOAT NOT NULL,
    montant_utilise FLOAT DEFAULT 0,
    date_allocation DATETIME NOT NULL,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    team_id INT NOT NULL,
    notes LONGTEXT,
    statut ENUM('en attente', 'approuvé', 'refusé', 'épuisé') DEFAULT 'en attente',
    justificatif VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE CASCADE,
    INDEX idx_statut (statut),
    INDEX idx_team_id (team_id),
    INDEX idx_date_allocation (date_allocation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Créer la table depense
CREATE TABLE IF NOT EXISTS depense (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    montant FLOAT NOT NULL,
    description LONGTEXT,
    date_creation DATETIME NOT NULL,
    statut ENUM('en attente', 'approuvé', 'refusé', 'payée') DEFAULT 'en attente',
    categorie ENUM('salaire', 'équipement', 'voyage', 'autre') DEFAULT 'autre',
    team_id INT,
    facture VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id) ON DELETE SET NULL,
    INDEX idx_statut (statut),
    INDEX idx_categorie (categorie),
    INDEX idx_team_id (team_id),
    INDEX idx_date_creation (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insérer des données d'exemple
INSERT IGNORE INTO budget (montant_alloue, montant_utilise, date_allocation, team_id, notes, statut) VALUES
(5000, 1200, NOW(), 1, 'Budget mensuel pour l\'équipe Eclipse', 'approuvé'),
(3000, 800, NOW(), 2, 'Budget pour l\'équipe Apex Drift', 'approuvé'),
(7000, 0, NOW(), 3, 'Budget pour l\'équipe Shadow Unit', 'en attente');

INSERT IGNORE INTO depense (titre, montant, description, date_creation, statut, categorie, team_id) VALUES
('Achat équipement gaming', 800, 'Souris et clavier pour l\'équipe', NOW(), 'payée', 'équipement', 1),
('Frais de déplacement', 400, 'Transport pour le tournoi', NOW(), 'approuvé', 'voyage', 1),
('Salaire joueur principal', 500, 'Compensation mensuelle', NOW(), 'payée', 'salaire', 2),
('Maintenance serveur', 150, 'Frais d\'hébergement', NOW(), 'en attente', 'autre', 3);

-- Affiche les structures
DESCRIBE budget;
DESCRIBE depense;

