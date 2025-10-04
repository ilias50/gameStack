<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import CollectionService from '@/services/collectionService';
import LibraryGameCard from '@/components/LibraryGameCard.vue'; // 💡 Rendu clair de l'import
import NavBar from '@/components/NavBar.vue';

const router = useRouter();

const userGames = ref([]);
const isLoading = ref(true);
const error = ref(null);

const fetchCollection = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    const data = await CollectionService.getUserCollection();
    userGames.value = data;
  } catch (err) {
    error.value = 'Impossible de charger votre bibliothèque. Veuillez réessayer plus tard.';
    console.error("Erreur de récupération de la collection:", err);
  } finally {
    isLoading.value = false;
  }
};

/**
 * Gère l'événement de suppression émis par LibraryGameCard.
 * @param {number} gameId L'ID du jeu à supprimer.
 */
const handleRemoveClick = async (gameId) => {
  if (confirm("Êtes-vous sûr de vouloir supprimer ce jeu de votre collection ?")) {
    try {
      // Logique de suppression via le service
      await CollectionService.removeGameFromCollection(gameId);

      // Mise à jour de la liste sans recharger toute la page
      userGames.value = userGames.value.filter(game => game.id !== gameId);
      alert("Jeu supprimé avec succès !");

    } catch (err) {
      console.error("Erreur lors de la suppression:", err);
      alert("Échec de la suppression du jeu. Veuillez vérifier la console.");
    }
  }
};

/**
 * Gère le clic sur le composant entier pour les détails.
 * @param {number} gameId L'ID du jeu passé par l'événement.
 */
const handleDetailsClick = (gameId) => {
  console.log(`Navigation vers les détails pour l'ID: ${gameId}`);
  router.push(`/games/${gameId}`);
};

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
      <LibraryGameCard
          v-for="userGame in userGames"
          :key="userGame.id"
          :game="userGame"
          @details-click="handleDetailsClick"
          @remove-click="handleRemoveClick" /> </div>
  </div>
</template>

<style scoped>
/* Les styles restent les mêmes */
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