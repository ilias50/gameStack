<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink, useRouter } from 'vue-router'; // Import de useRouter pour la navigation
import CollectionService from '@/services/collectionService';
import GameCard from '@/components/GameCard.vue'; // Composant de carte
import NavBar from '@/components/NavBar.vue'; // Composant de navigation

// Initialisation du routeur pour les actions de navigation
const router = useRouter();

const userGames = ref([]);
const isLoading = ref(true);
const error = ref(null);

const fetchCollection = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    // Appel réel à l'endpoint GET /collections/games
    const data = await CollectionService.getUserCollection();
    userGames.value = data; // Assurez-vous que le service retourne un tableau
  } catch (err) {
    error.value = 'Impossible de charger votre bibliothèque. Veuillez réessayer plus tard.';
    console.error("Erreur de récupération de la collection:", err);
  } finally {
    isLoading.value = false;
  }
};

/**
 * Gère l'événement émis par le composant GameCard lorsque l'utilisateur
 * clique sur le bouton "Détails" et navigue vers la page de détails.
 * @param {number} gameId L'ID du jeu passé par l'événement.
 */
const handleDetailsClick = (gameId) => {
  console.log(`Navigation vers les détails pour l'ID: ${gameId}`);
  // 💡 Assurez-vous d'avoir une route '/games/:id' configurée dans votre router.js
  router.push(`/games/${gameId}`);
};

// Exécuter la fonction de chargement au montage du composant
onMounted(fetchCollection);
</script>

<template>
  <div class="library-view">
    <NavBar />

    <h1>Ma Bibliothèque de Jeux 🎮</h1>

    <div v-if="isLoading" class="loading-message">Chargement de votre collection...</div>

    <div v-else-if="error" class="alert error">{{ error }}</div>

    <div v-else-if="userGames && userGames.length === 0" class="alert info">
      Votre bibliothèque est vide. Rendez-vous à la page de <RouterLink to="/search">Recherche</RouterLink> pour ajouter des jeux !
    </div>

    <div v-else class="game-list">
      <GameCard
          v-for="userGame in userGames"
          :key="userGame.id"
          :game="userGame"
          @details-click="handleDetailsClick" />
    </div>
  </div>
</template>

<style scoped>
.library-view {
  text-align: center;
  padding: 20px;
}
h1 {
  margin-bottom: 40px;
}
.loading-message, .alert {
  padding: 20px;
  margin: 20px auto;
  border-radius: 8px;
  max-width: 600px;
  font-weight: bold;
}
.alert.info {
  background-color: #e2f4ff;
  border: 1px solid #b3e0ff;
  color: #004085;
}
.alert.error {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}
.game-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  margin-top: 30px;
}
</style>