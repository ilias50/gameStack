package com.gamestack.games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO (Data Transfer Object) représentant la réponse pour la liste des bandes-annonces
 * d'un jeu spécifique de l'API RAWG (/games/{id}/movies).
 * Utilise les records Java pour des structures de données concises.
 */
public class RawgTrailerResponse {

    // Champs de la réponse principale
    public int count;
    public TrailerResult[] results; // Tableau des bandes-annonces trouvées

    // --- Records internes pour simplifier la structure ---

    /**
     * DTO représentant une seule entrée de bande-annonce (un élément dans la liste 'results').
     * C'est un record pour la concision.
     */
    public static record TrailerResult(
            long id,
            String name,
            String preview, // URL de l'image de prévisualisation
            TrailerData data // Structure imbriquée contenant les URLs réelles des vidéos
    ) {}

    /**
     * DTO représentant les URLs des fichiers vidéo pour différentes résolutions.
     * Utilise @JsonProperty pour gérer les clés JSON numériques (ex: "480").
     * C'est un record pour la concision.
     */
    public static record TrailerData(
            // La clé JSON est "480"
            @JsonProperty("480")
            String video480p,
            // La clé JSON est "max"
            String max  // URL de la vidéo en qualité maximale
    ) {}
}
