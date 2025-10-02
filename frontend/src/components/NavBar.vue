<template>
  <nav class="navbar">
    <div class="nav-links">
      <router-link to="/library" class="nav-link">
        Bibliothèque 📚
      </router-link>

      <router-link to="/search" class="nav-link">
        Recherche 🔍
      </router-link>
    </div>

    <button @click="handleLogout" class="logout-btn">
      Se Déconnecter 🚪
    </button>
  </nav>
</template>

<script>
import { useRouter } from 'vue-router'; // Pour la redirection après déconnexion
import AuthService from '@/services/authService.js'; // Assurez-vous que ce chemin est correct

export default {
  name: 'NavBar',
  setup() {
    const router = useRouter();

    /**
     * Gère la déconnexion de l'utilisateur.
     */
    const handleLogout = () => {
      // 1. Appel de la méthode de déconnexion
      AuthService.logout();

      // 2. Redirection vers la page de connexion
      router.push({ name: 'login' });
    };

    return {
      handleLogout,
    };
  },
};
</script>

<style scoped>
/* Styles de base pour la barre de navigation */
.navbar {
  display: flex;
  justify-content: space-between; /* Pour séparer les liens du bouton de déconnexion */
  align-items: center;
  padding: 10px 20px;
  background-color: #333; /* Fond sombre */
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-links {
  display: flex;
  gap: 20px; /* Espace entre les liens */
}

/* Styles pour les liens de navigation */
.nav-link {
  color: white;
  text-decoration: none; /* Enlève le soulignement par défaut */
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover,
.router-link-active {
  background-color: #555; /* Changement de couleur au survol ou si le lien est actif */
}

/* Style spécifique pour le lien actif (indiquant la page actuelle) */
.router-link-active {
  font-weight: bold;
  background-color: #007bff; /* Couleur d'accentuation pour l'onglet actif */
}

/* Styles pour le bouton de déconnexion */
.logout-btn {
  background-color: #dc3545; /* Rouge pour l'action de déconnexion */
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: #c82333; /* Rouge plus foncé au survol */
}
</style>