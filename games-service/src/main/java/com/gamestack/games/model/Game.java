package com.gamestack.games.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    // Use a generic name like api_id to be independent of the API provider.
    @Column(name = "api_id", nullable = false, unique = true)
    private int apiId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "platform")
    private String platform;
}
