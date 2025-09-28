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