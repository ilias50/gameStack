--v1

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS gameStackDB;
USE gameStackDB;

-- Création de la table users
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Création de la table games
CREATE TABLE IF NOT EXISTS games (
    id INT PRIMARY KEY AUTO_INCREMENT,
    api_id int NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    release_date DATE,
    image_path VARCHAR(255),
    platform VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
    );

-- Création de la table statuses
CREATE TABLE IF NOT EXISTS status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Création de la table user_games
CREATE TABLE IF NOT EXISTS user_games (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    game_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (game_id) REFERENCES games(id)
    );

CREATE TABLE IF NOT EXISTS user_games_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_games_id INT NOT NULL,
    status_id INT NOT NULL,
    FOREIGN KEY (user_games_id) REFERENCES user_games(id),
    FOREIGN KEY (status_id) REFERENCES status(id),
    UNIQUE (user_games_id, status_id)
    );

-- Insertion des statuts de base
INSERT INTO status (status_name) VALUES
    ('Playing'),
    ('Completed'),
    ('On Hold'),
    ('Dropped'),
    ('Plan to Play');



--v2------------------------------------------------------

-- Création de la table 'platforms'
CREATE TABLE IF NOT EXISTS platforms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
 );


ALTER TABLE user_games
    ADD COLUMN platform_id INT NULL;


ALTER TABLE user_games
    ADD CONSTRAINT fk_user_games_platform
        FOREIGN KEY (platform_id)
            REFERENCES platforms(id)
            ON DELETE SET NULL
            ON UPDATE CASCADE;


INSERT INTO platforms (name) VALUES ('Non spécifiée');



--v3------------------------------------------------------------------------

INSERT INTO platforms (name) VALUES
-- Ordinateurs
('PC (Ordinateur Personnel)'),
('Mac'),
('Linux'),

-- Consoles Sony PlayStation
('PlayStation 5'),
('PlayStation 4'),
('PlayStation 3'),
('PlayStation 2'),
('PlayStation'),

-- Consoles Microsoft Xbox
('Xbox Series X/S'),
('Xbox One'),
('Xbox 360'),
('Xbox'),

-- Consoles Nintendo (Salon)
('Nintendo Switch'),
('Nintendo Wii U'),
('Nintendo Wii'),
('Nintendo GameCube'),
('Nintendo 64'),
('Super Nintendo Entertainment System (SNES)'),
('Nintendo Entertainment System (NES)'),

-- Consoles Nintendo (Portable)
('Nintendo 3DS'),
('Nintendo DS'),
('Nintendo Game Boy Advance'),
('Nintendo Game Boy Color'),
('Nintendo Game Boy'),

-- Consoles Sony (Portable)
('PlayStation Vita'),
('PlayStation Portable (PSP)'),

-- Consoles SEGA
('Sega Dreamcast'),
('Sega Saturn'),
('Sega Mega Drive/Genesis'),
('Sega Master System'),
('Sega Game Gear'),

-- Consoles Atari et autres
('Atari 2600'),
('Neo Geo'),
('TurboGrafx-16'),

-- Appareils Mobiles
('Android'),
('iOS (iPhone/iPad)'),

-- Consoles Portables Modernes
('Steam Deck'),
('ASUS ROG Ally');