package com.gamestack.games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO représentant la réponse complète de l'endpoint RAWG /games/{id}/screenshots.
 * Cette structure est utilisée pour décoder la liste des captures d'écran.
 *
 * Exemple de JSON :
 * {
 * "count": 4,
 * "next": null,
 * "previous": null,
 * "results": [
 * { "id": 12345, "image": "URL_IMAGE_1" },
 * { "id": 67890, "image": "URL_IMAGE_2" }
 * ]
 * }
 */
public record RawgScreenshotResponse(
        // Nombre total de résultats (captures d'écran)
        int count,

        // Lien vers la page suivante de résultats (souvent null pour les screenshots)
        String next,

        // Lien vers la page précédente de résultats (souvent null pour les screenshots)
        String previous,

        // La liste réelle des objets de capture d'écran
        List<ScreenshotResult> results
) {
    /**
     * Représente une capture d'écran individuelle dans la liste 'results'.
     */
    public record ScreenshotResult(
            // L'identifiant unique de la capture d'écran
            long id,

            // L'URL de la capture d'écran en haute résolution
            String image
    ) {}
}
