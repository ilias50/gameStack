import { createRouter, createWebHistory } from 'vue-router';
import LibraryView from '../views/LibraryView.vue';
import SearchView from '../views/SearchView.vue';
import LoginView from "@/views/LoginView.vue";
import AuthService from "@/services/authService.js";
import RegisterView from "@/views/RegisterView.vue";
import GameDetailsView from "@/views/GameDetailsView.vue";
// Nouvelle vue Recherche

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/library',
            name: 'library',
            component: LibraryView,
            meta: { requiresAuth: true }
        },
        {
            path: '/games/:id', // L'ID RAWG sera accessible via route.params.id
            name: 'game-details',
            component: GameDetailsView,
            meta: { requiresAuth: true } // L'accès aux détails de la collection nécessite l'authentification
        },
        {
            path: '/search',
            name: 'search',
            component: SearchView,
            meta: { requiresAuth: true }
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: { requiresAuth: false }
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
            meta: { requiresAuth: false }
        }
    ]
});


router.beforeEach((to, from, next) => {
    const requiresAuth = to.meta.requiresAuth;
    const isAuthenticated = AuthService.isLoggedIn();

    if (requiresAuth && !isAuthenticated) {
        next({ name: 'login' });
    } else if (isAuthenticated && (to.name === 'login' || to.name === 'register')) {
        // Redirection vers la 'library' si l'utilisateur est déjà connecté et essaie de se connecter/s'inscrire
        next({ name: 'library' });
    } else {
        next();
    }
});

export default router;