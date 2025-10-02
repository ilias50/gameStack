import axios from 'axios';

// --- CONFIGURATION ---
const AUTH_BASE_URL = `${import.meta.env.VITE_APP_API_URL}/auth`;
const TOKEN_KEY = 'user_token';
const USER_ID_KEY = 'user_id';

// Instance d'Axios SANS l'intercepteur de token/ID pour les requêtes d'Auth
const authApi = axios.create({
    baseURL: AUTH_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * Stocke le token et l'ID utilisateur dans le stockage local.
 */
const saveUserData = (token, userId) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_ID_KEY, String(userId));
};


/**
 * Déconnecte l'utilisateur en nettoyant les données stockées.
 */
export const logout = () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_ID_KEY);
    console.log("Déconnexion réussie. Token et ID utilisateur supprimés.");
};


/**
 * Enregistre un nouvel utilisateur.
 */
export const register = async (username, password) => {
    try {
        const response = await authApi.post('/register', { username, password });
        return response.data;
    } catch (error) {
        console.error("Erreur lors de l'inscription:", error);
        throw error.response?.data || new Error("Erreur inconnue lors de l'inscription.");
    }
};


/**
 * Connecte un utilisateur et stocke le token d'accès ainsi que l'ID.
 */
export const login = async (username, password) => {
    try {
        const response = await authApi.post('/login', { username, password });

        // Assomption : Le service Auth renvoie 'token' et 'userId'
        const { token, userId } = response.data;

        if (!token || userId === undefined || userId === null) {
            // userId peut être 0 ou un nombre, donc on vérifie s'il est absent/null/undefined
            throw new Error("Réponse de connexion incomplète: Token ou ID manquant.");
        }

        saveUserData(token, userId);

        console.log("Connexion réussie. Token et ID utilisateur stockés.");
        return token;

    } catch (error) {
        console.error("Erreur de connexion:", error);
        throw error.response?.data || new Error("Identifiants invalides ou erreur serveur.");
    }
};

/**
 * Vérifie si l'utilisateur est actuellement authentifié. (Nom principal)
 */
export const isAuthenticated = () => {
    return !!localStorage.getItem(TOKEN_KEY) && !!localStorage.getItem(USER_ID_KEY);
};

// 🟢 AJOUT CRITIQUE POUR LE DÉBOGAGE (Résout le TypeError Fl.isLoggedIn)
// Ceci permet aux composants qui utilisent le nom 'isLoggedIn' de fonctionner.
export const isLoggedIn = isAuthenticated;

// Exportez toutes les fonctions nécessaires
export default {
    register,
    login,
    logout,
    isAuthenticated,
    isLoggedIn // Ajout de l'alias pour compatibilité
};