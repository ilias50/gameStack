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
}

export default new GamesService();