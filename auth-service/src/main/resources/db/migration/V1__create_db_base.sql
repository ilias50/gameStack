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

INSERT INTO users (username, password_hash) VALUES ('test_user', 'hashed_password123');