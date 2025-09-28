package com.gamestack.collection.dto;

import com.gamestack.collection.model.UserGame;

public class UserGameResponseDto {

    private Long id;
    private Long apiId;
    private String title;
    // NOTE: Ce champ 'platform' sera maintenant le nom de la Platform statique liée au jeu.
    private String platform;
    private String releaseDate;
    private String imagePath;
    private boolean isActive;

    public UserGameResponseDto() {
    }

    public UserGameResponseDto(UserGame userGame) {
        // Mappage de l'entité UserGame au DTO
        this.id = userGame.getId();
        this.isActive = userGame.isActive();

        // Mappage des propriétés du jeu
        if (userGame.getGame() != null) {
            this.apiId = userGame.getGame().getApiId();
            this.title = userGame.getGame().getTitle();
            this.releaseDate = userGame.getGame().getReleaseDate();
            this.imagePath = userGame.getGame().getImagePath();

            // ANCIENNE LOGIQUE: this.platform = userGame.getGame().getPlatform();
        }

        // NOUVELLE LOGIQUE CRITIQUE: Récupérer le nom de la plateforme à partir de la FK dans UserGame
        if (userGame.getPlatform() != null) {
            this.platform = userGame.getPlatform().getName(); // <-- Utilise la nouvelle relation Platform
        } else {
            this.platform = "Inconnue";
        }
    }

    // Getters et Setters (inchangés)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
