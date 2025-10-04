package com.gamestack.collection.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// This class represents the 'games' table in the database.
// It is an Entity managed by JPA.
@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ATTENTION: Changement de 'long' (primitif) à 'Long' (wrapper)
    @Column(name = "api_id", nullable = false, unique = true)
    private Long apiId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "platform")
    private String platform;



    // N'oubliez pas de mettre à jour le constructeur aussi pour qu'il utilise Long !
    public Game(Long apiId, String title, String releaseDate, String imagePath, String platform) {
        this.apiId = apiId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.imagePath = imagePath;
        this.platform = platform;

    }
}