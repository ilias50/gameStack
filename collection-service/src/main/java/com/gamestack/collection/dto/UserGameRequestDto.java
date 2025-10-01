package com.gamestack.collection.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO utilisé pour ajouter un jeu. Contient l'ID API, les détails du catalogue (Game),
 * et l'ID numérique de la plateforme de l'utilisateur (UserGame).
 */
public class UserGameRequestDto {

    // Clé de recherche : L'ID du jeu dans l'API externe (obligatoire)
    @NotNull(message = "L'ID du jeu (apiId) ne peut pas être nul.")
    private Long apiId;

    // Détails du jeu à persister (obligatoire car non-nullable dans Game)
    @NotNull(message = "Le titre du jeu ne peut pas être nul.")
    private String title;

    private String releaseDate;
    private String imagePath;
    // ATTENTION: platform (String) EST SUPPRIMÉ ICI

    // ID de la plateforme choisie par l'utilisateur (ID numérique local)
    private Long platformId;

    // Getters and Setters

    public Long getApiId() { return apiId; }
    public void setApiId(Long apiId) { this.apiId = apiId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }
}