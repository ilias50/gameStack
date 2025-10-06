// src/services/collectionService.js
import apiClient from './apiClient';

class CollectionService {

    /**
     * Récupère la liste des jeux de la bibliothèque de l'utilisateur connecté.
     * C'est l'appel réel au Gateway/Microservice.
     */
    async getUserCollection() {
        try {
            // L'appel réel au backend est effectué ici :
            const response = await apiClient.get('/collections/games');
            return response.data; // Retourne soit [] soit List<UserGameResponseDto>
        } catch (error) {
            console.error("Erreur lors de la récupération de la collection:", error);
            // Si l'appel échoue (ex: 400 Bad Request), nous propageons l'erreur
            throw error;
        }
    }

    /**
     * Ajoute un jeu à la collection de l'utilisateur.
     */
    async addGameToCollection(gameDto) {
        try {
            const response = await apiClient.post('/collections/games', gameDto);
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de l'ajout du jeu :`, error);
            throw error;
        }
    }

    /**
     * Supprime un jeu de la collection de l'utilisateur.
     */
    async removeGameFromCollection(gameId) {
        try {
            const response = await apiClient.delete(`/collections/games/${gameId}`);
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la suppression du jeu ${gameId} :`, error);
            throw error;
        }
    }

    // --- NOUVELLE MÉTHODE ---

    /**
     * Récupère la liste complète des plateformes disponibles.
     * Appelle l'endpoint GET /collections/platforms.
     */
    async getAllPlatforms() {
        try {
            const response = await apiClient.get('/collections/platforms');
            // Le backend peut retourner un statut 204 No Content si la liste est vide.
            // On s'assure de retourner un tableau vide si les données sont nulles.
            return response.data || [];
        } catch (error) {
            console.error("Erreur lors de la récupération des plateformes:", error);
            throw error;
        }
    }

    /**
     * Met à jour la plateforme d'un jeu spécifique dans la collection de l'utilisateur.
     * Appelle l'endpoint PUT /collections/games/{userGameId}/platform.
     */
    async updateGamePlatform(userGameId, newPlatformId) {
        try {
            // Le nouvel ID de plateforme est envoyé directement dans le corps de la requête (RequestBody)
            const response = await apiClient.put(
                `/collections/games/${userGameId}/platform`,
                newPlatformId
            );
            return response.data;
        } catch (error) {
            console.error(`Erreur lors de la mise à jour de la plateforme du jeu ${userGameId}:`, error);
            throw error;
        }
    }
}

export default new CollectionService();