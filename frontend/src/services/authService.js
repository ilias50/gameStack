// src/services/authService.js

/**
 * Service pour gérer les requêtes d'authentification.
 * Pour l'instant, c'est un FAUX service qui simule des appels API avec un délai.
 */
class AuthService {

    /**
     * Simule une connexion au backend.
     * @param {string} email L'e-mail de l'utilisateur.
     * @param {string} password Le mot de passe de l'utilisateur.
     * @returns {Promise<Object>} Promesse résolue avec les données utilisateur/token.
     */
    async login(email, password) {
        // Simuler le délai d'une requête réseau (2 secondes)
        await new Promise(resolve => setTimeout(resolve, 2000));

        // Logique de FAUX login :
        if (email === 'test@example.com' && password === 'password123') {
            console.log("FAUX LOGIN RÉUSSI");

            // Simuler la réponse du backend avec un token
            const fakeToken = `fake-jwt-token-for-${email}`;
            const userData = { email: email, name: 'Utilisateur Test' };

            // Stocker le token localement (pour le maintenir connecté)
            localStorage.setItem('user_token', fakeToken);

            return { token: fakeToken, user: userData };
        } else {
            console.error("FAUX LOGIN ÉCHOUÉ");
            // Simuler une erreur 401 Unauthorized
            const error = new Error('Identifiants incorrects.');
            error.status = 401;
            throw error;
        }
    }

    /**
     * Simule une déconnexion (retire le token).
     */
    logout() {
        localStorage.removeItem('user_token');
        console.log("Déconnexion effectuée.");
    }

    /**
     * Vérifie si l'utilisateur est connecté.
     * @returns {boolean}
     */
    isLoggedIn() {
        return !!localStorage.getItem('user_token');
    }
}

export default new AuthService();