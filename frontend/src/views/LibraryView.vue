<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import CollectionService from '@/services/collectionService';

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

      <div v-for="userGame in userGames" :key="userGame.id" class="game-card">

        <img
            :src="userGame.imagePath || 'default-cover.png'"
            :alt="userGame.title || 'Couverture inconnue'"
            class="game-image"
        >
        <h3 class="game-name">{{ userGame.title || 'Jeu Inconnu' }}</h3>

        <button class="detail-button">Détails</button>
      </div>
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
.game-card {
  border: 1px solid #ccc;
  padding: 15px;
  width: 200px;
  box-shadow: 2px 2px 5px rgba(0,0,0,0.1);
}
.game-image {
  width: 100%;
  height: auto;
  margin-bottom: 10px;
}
</style>