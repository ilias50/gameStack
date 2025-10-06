// src/services/apiClient.js (MODIFICATION CRITIQUE)
import axios from 'axios';

const baseURL = import.meta.env.VITE_APP_API_URL + '/api';

const apiClient = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Intercepteur de Requête : Ajoute le token JWT ET le X-User-Id
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('user_token');
    const userId = localStorage.getItem('user_id'); // ⚠️ Assurez-vous d'avoir stocké 'user_id' lors du login

    // 1. Gère le Token JWT (pour l'autorisation)
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    // 2. 🟢 Injection dynamique de l'ID UTILISATEUR
    if (userId) {
        config.headers['X-User-Id'] = userId;
    }

    return config;
}, error => {
    return Promise.reject(error);
});

export default apiClient;