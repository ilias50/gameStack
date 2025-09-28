package com.gamestack.collection.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_games")
public class UserGame implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    // NOUVEAU: Relation vers la table 'platforms'
    @ManyToOne
    @JoinColumn(name = "platform_id") // Nom de la colonne créée via la commande SQL
    private Platform platform;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    public UserGame() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // NOUVEAU: Getter pour Platform
    public Platform getPlatform() {
        return platform;
    }

    // NOUVEAU: Setter pour Platform
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
