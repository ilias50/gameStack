<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import CollectionService from '@/services/collectionService';
// 1. Importez le nouveau composant GameCard
import GameCard from '@/components/GameCard.vue'; // 👈 Assurez-vous que le chemin est correct !

const userGames = ref([]);
const isLoading = ref(true);
const error = ref(null);

const fetchCollection = async () => {
  isLoading.value = true;
  error.value = null;
  try {
    // Appel réel à l'endpoint GET /collections/games
    userGames.value = await CollectionService.getUserCollection();
  } catch (err) {
    error.value = 'Impossible de charger votre bibliothèque. Vérifiez la console pour plus de détails.';
    console.error("Erreur de récupération de la collection:", err);
  } finally {
    isLoading.value = false;
  }
};

/**
 * Gère l'événement émis par le composant GameCard lorsque l'utilisateur
 * clique sur le bouton "Détails".
 * @param {number} gameId L'ID du jeu passé par l'événement.
 */
const handleDetailsClick = (gameId) => {
  console.log(`Action de navigation vers les détails pour l'ID: ${gameId}`);
  // 💡 Ici, vous mettriez votre logique pour naviguer (ex: router.push('/games/' + gameId))
  // ou ouvrir une modale de détails.
};

onMounted(fetchCollection);
</script>

<template>
  <div class="library-view">
    <h1>Ma Bibliothèque de Jeux</h1>

    <div v-if="isLoading" class="loading-message">Chargement de votre collection...</div>

    <div v-else-if="error" class="alert error">{{ error }}</div>

    <div v-else-if="userGames && userGames.length === 0" class="alert info">
      Votre bibliothèque est vide. Rendez-vous à la page de <RouterLink to="/search">Recherche</RouterLink> pour ajouter des jeux !
    </div>

    <div v-else class="game-list">
      <GameCard
          v-for="userGame in userGames"
          :key="userGame.id"
          :game="userGame"               @details-click="handleDetailsClick" />
    </div>
  </div>
</template>

<style scoped>
.library-view {
  text-align: center;
  padding: 20px;
}
.loading-message, .alert {
  padding: 20px;
  margin: 20px auto;
  border-radius: 8px;
  max-width: 600px;
}
.alert.info {
  background-color: #e2f4ff;
  border: 1px solid #b3e0ff;
  color: #004085;
}
.game-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  margin-top: 30px;
}
/* 💡 NOTE : Les styles de .game-card, .game-image et .game-name ont été déplacés
   dans le fichier GameCard.vue et n'ont plus besoin d'être ici. */
</style>