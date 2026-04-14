    -- =====================================================
-- Base de données: esportdevvvvvv
-- =====================================================

-- Créer la base de données si elle n'existe pas
CREATE DATABASE IF NOT EXISTS esportdevvvvvv CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE esportdevvvvvv;

-- =====================================================
-- Table: user
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `user` (full_name, email, password_hash, role) VALUES
('Admin RankUp', 'admin@rankup.local', SHA2('admin123', 256), 'ADMIN'),
('Player Demo', 'player@rankup.local', SHA2('player123', 256), 'PLAYER');

-- =====================================================
-- Table: recompense
-- =====================================================
DROP TABLE IF EXISTS recompense;
CREATE TABLE recompense (
    id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    recompense VARCHAR(30) NOT NULL,
    type VARCHAR(50) NOT NULL,
    classement INT(11) NOT NULL,
    description LONGTEXT,
    tournament_id INT(11) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- Table: demande_recompense
-- =====================================================
DROP TABLE IF EXISTS demande_recompense;
CREATE TABLE demande_recompense (
    id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT(11) NULL,
    nom_demandeur VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    motif LONGTEXT,
    date_demande DATETIME NOT NULL,
    statut VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_demande_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- =====================================================
-- Données d'exemple: recompense
-- =====================================================
INSERT INTO recompense (recompense, type, classement, description, tournament_id) VALUES
('Trophée Or', 'Or', 1, 'Trophée pour le 1er classement du tournoi', 1),
('Trophée Argent', 'Argent', 2, 'Trophée pour le 2ème classement', 1),
('Trophée Bronze', 'Bronze', 3, 'Trophée pour le 3ème classement', 1),
('Médaille d''Or League of Legends', 'Or', 1, 'Médaille d''or pour les champions en LoL', 2),
('Certificat Excellence', 'Or', 1, 'Certificat d''excellence du tournoi Valorant', 3),
('Monnaie Virtuelle 1000', 'Argent', 2, 'Récompense en monnaie virtuelle - 1000 points', 2),
('Skin Exclusif', 'Bronze', 3, 'Skin exclusif pour le jeu', 3);

-- =====================================================
-- Données d'exemple: demande_recompense
-- =====================================================
INSERT INTO demande_recompense (user_id, nom_demandeur, email, motif, date_demande, statut) VALUES
(2, 'Player Demo', 'player@rankup.local', 'Demande de récompense de démonstration envoyée après connexion joueur.', '2024-04-10 14:30:00', 'En attente'),
(2, 'Player Demo', 'player@rankup.local', 'Deuxième demande de test pour vérifier le flux login → demande.', '2024-04-08 10:15:00', 'Approuvée'),
(NULL, 'Pierre Durand', 'pierre.durand@example.com', 'Demande de récompense pour le meilleur score individuel en matches de poule. J''ai maintenu une moyenne de 1.5 K/D ratio.', '2024-04-09 16:45:00', 'En attente'),
(NULL, 'Sophie Bernard', 'sophie.bernard@example.com', 'Demande de récompense pour avoir remporté le prix du joueur le plus prometteur du tournoi League of Legends.', '2024-04-07 09:20:00', 'Rejetée'),
(NULL, 'Thomas Lefevre', 'thomas.lefevre@example.com', 'Demande de récompense spéciale pour sponsoring et support du tournoi esports régional.', '2024-04-06 11:00:00', 'Approuvée');

-- =====================================================
-- Vérification des données
-- =====================================================
SELECT 'Nombre de récompenses:' as info, COUNT(*) as count FROM recompense;
SELECT 'Nombre de demandes:' as info, COUNT(*) as count FROM demande_recompense;
SELECT 'Tables créées avec succès!' as status;

