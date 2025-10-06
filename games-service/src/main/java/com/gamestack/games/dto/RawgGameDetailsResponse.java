package com.gamestack.games.dto;

/**
 * DTO (Data Transfer Object) représentant la réponse détaillée pour un jeu spécifique
 * de l'API RAWG (/games/{id}).
 * Contient toutes les informations principales du jeu et les structures imbriquées nécessaires
 * pour la désérialisation de Jackson, le tout dans une seule classe publique.
 */
public class RawgGameDetailsResponse {

    // --- Champs principaux du jeu ---
    public long id;
    public String slug;
    public String name;
    public String description_raw; // Description en texte brut
    public String released; // Date de sortie (format "YYYY-MM-DD")
    public String background_image; // URL de l'image de fond
    public double rating;
    public int ratings_count;
    public int playtime; // Durée de jeu estimée
    public String website;

    // --- Listes d'objets complexes (définis en interne) ---
    public PlatformInfo[] platforms;
    public Genre[] genres;
    public Developer[] developers;
    public Developer[] publishers;

    // --- Structures internes nécessaires pour la désérialisation ---

    /**
     * DTO représentant une structure de Plateforme renvoyée par l'API RAWG.
     */
    public static class PlatformInfo {
        public long id;
        public String name;
    }

    /**
     * DTO représentant une structure de Genre renvoyée par l'API RAWG.
     */
    public static class Genre {
        public long id;
        public String name;
    }

    /**
     * DTO représentant une structure de Développeur ou d'Éditeur (Publisher) renvoyée par l'API RAWG.
     */
    public static class Developer {
        public long id;
        public String name;
    }
}
