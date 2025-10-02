package com.gamestack.collection.dto;

public class PlatformResponseDto {

    private Integer id;
    private String name;

    // Constructeur sans argument (souvent nécessaire pour la désérialisation JSON)
    public PlatformResponseDto() {
    }

    // Constructeur avec tous les arguments (pratique pour le mapping depuis l'entité)
    public PlatformResponseDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- Getters et Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}