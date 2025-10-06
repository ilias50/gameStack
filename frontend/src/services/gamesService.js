// src/services/gamesService.js
import apiClient from './apiClient';

class GamesService {
    /**
     * Recherche des jeux disponibles via le Games Service.
     * @param {string} query Terme de recherche.
     * @returns {Promise<Array>} Liste des jeux trouvés.
     */
    async searchGames(query = '') {
        try {
            // L'URL du Gateway est /api/games.
            // Le Gateway le route vers le service interne /api/games.
            // Le paramètre doit être 'search' pour correspondre au contrôleur Spring.
            const response = await apiClient.get('/games', {
                params: {
                    search: query // 🟢 CORRECTION: Utilise 'search'
                }
            });

            // Le backend renvoie une List<Game>, nous la renvoyons directement
            return response.data;
        } catch (error) {
            console.error("Erreur lors de la recherche des jeux:", error);
            throw error;
        }
    }

    /**
     * Récupère les détails complets d'un jeu spécifique par son ID.
     * @param {number} gameId L'ID du jeu RAWG.
     * @returns {Promise<Object>} Les détails du jeu (RawgGameDetailsResponse).
     */
    async getGameDetails(gameId) {
        if (!gameId) {
            throw new Error("L'ID du jeu est requis pour récupérer les détails.");
        }
        try {
            // Endpoint Spring Boot: /api/games/{gameId}/details
            const response = await apiClient.get(`/games/${gameId}/details`);

            // Le backend renvoie RawgGameDetailsResponse
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la récupération des détails du jeu ID ${gameId}:`, error);
            throw error;
        }
    }

    /**
     * Récupère la liste des bandes-annonces (trailers) pour un jeu spécifique.
     * @param {number} gameId L'ID du jeu RAWG.
     * @returns {Promise<Object>} La réponse des trailers (RawgTrailerResponse).
     */
    async getGameTrailers(gameId) {
        if (!gameId) {
            throw new Error("L'ID du jeu est requis pour récupérer les trailers.");
        }
        try {
            // Endpoint Spring Boot: /api/games/{gameId}/trailers
            const response = await apiClient.get(`/games/${gameId}/trailers`);

            // Le backend renvoie RawgTrailerResponse
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la récupération des trailers du jeu ID ${gameId}:`, error);
            throw error;
        }
    }

    /**
     * Récupère la liste des captures d'écran (screenshots) pour un jeu spécifique.
     * @param {number} gameId L'ID du jeu RAWG.
     * @returns {Promise<Object>} La réponse des captures d'écran (RawgScreenshotResponse).
     */
    async getGameScreenshots(gameId) {
        if (!gameId) {
            throw new Error("L'ID du jeu est requis pour récupérer les captures d'écran.");
        }
        try {
            // Endpoint Spring Boot: /api/games/{gameId}/screenshots
            const response = await apiClient.get(`/games/${gameId}/screenshots`);

            // Le backend renvoie RawgScreenshotResponse
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la récupération des captures d'écran du jeu ID ${gameId}:`, error);
            throw error;
        }
    }
}

export default new GamesService();