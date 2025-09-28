import { createRouter, createWebHistory } from 'vue-router'; // Plus besoin de HomeView
import LibraryView from '../views/LibraryView.vue'; // Nouvelle vue Bibliothèque
import SearchView from '../views/SearchView.vue';
import LoginView from "@/views/LoginView.vue";
import AuthService from "@/services/authService.js";
// Nouvelle vue Recherche

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/library', // Nouvelle route principale
            name: 'library',
            component: LibraryView,
            meta: { requiresAuth: true }
        },
        {
            path: '/search', // Route pour la recherche
            name: 'search',
            component: SearchView,
            meta: { requiresAuth: true }
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: { requiresAuth: false }
        }
    ]
});

// **Ajustement de la Garde de Navigation pour la nouvelle route 'library'**
router.beforeEach((to, from, next) => {
    const requiresAuth = to.meta.requiresAuth;
    const isAuthenticated = AuthService.isLoggedIn();

    if (requiresAuth && !isAuthenticated) {
        next({ name: 'login' });
    } else if (isAuthenticated && to.name === 'login') {
        // Redirection vers la 'library' après login si l'utilisateur est déjà connecté
        next({ name: 'library' });
    } else {
        next();
    }
});

export default router;