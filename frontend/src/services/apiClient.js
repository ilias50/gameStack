// src/services/apiClient.js (MODIFICATION CRITIQUE)
import axios from 'axios';

const baseURL = import.meta.env.VITE_APP_API_URL;

const apiClient = axios.create({
    baseURL: baseURL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Intercepteur de Requête : Ajoute le token JWT ET le X-User-Id
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('user_token');

    // 1. Gère le Token JWT (pour l'autorisation)
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    // 2. 🟢 INJECTION MANUELLE DE L'ID UTILISATEUR (Pour débloquer le collection-service)
    // Utilisez un ID valide dans votre DB, si vous en avez un.
    config.headers['X-User-Id'] = '1';

    return config;
}, error => {
    return Promise.reject(error);
});

export default apiClient;