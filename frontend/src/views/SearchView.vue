<script setup>
import { ref } from 'vue';
import GamesService from '@/services/gamesService';
import CollectionService from '@/services/collectionService';

const searchQuery = ref('');
const searchResults = ref([]);
const isLoading = ref(false);
const message = ref('');

const handleSearch = async () => {
  if (searchQuery.value.length < 3) {
    message.value = 'Veuillez entrer au moins 3 caractères pour la recherche.';
    searchResults.value = [];
    return;
  }

  message.value = '';
  isLoading.value = true;

  try {
    // Endpoint: /games?search=...
    searchResults.value = await GamesService.searchGames(searchQuery.value);
    if (searchResults.value.length === 0) {
      message.value = 'Aucun jeu trouvé pour cette recherche.';
    }
  } catch (error) {
    message.value = 'Erreur lors de la recherche des jeux.';
  } finally {
    isLoading.value = false;
  }
};

// Envoie un GameDto au Collection Service (POST /collections/games)
const addToCollection = async (game) => {
  try {
    // Crée le DTO basé sur les propriétés du GameController
    const gameDto = {
      apiId: game.apiId,
      title: game.title,
      releaseDate: game.releaseDate,
      imagePath: game.imagePath,
      platform: 1 // Valeur par défaut
    };

    await CollectionService.addGameToCollection(gameDto);
    alert(`${game.title} a été ajouté à votre collection !`);

  } catch (error) {
    alert(`Échec de l'ajout de ${game.title}. Vérifiez la console.`);
  }
};
</script>

<template>
  <div class="search-view">
    <h1>Rechercher et Ajouter des Jeux</h1>

    <div class="search-form">
      <input
          type="text"
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          placeholder="Entrez le nom d'un jeu..."
          :disabled="isLoading"
      >
      <button @click="handleSearch" :disabled="isLoading">Rechercher</button>
    </div>

    <div v-if="isLoading" class="loading">Recherche en cours...</div>
    <div v-else-if="message" class="alert info">{{ message }}</div>

    <div v-else class="results-list">
      <div v-for="game in searchResults" :key="game.apiId" class="game-item">
        <span class="game-name">{{ game.title }}</span>
        <button @click="addToCollection(game)" class="add-button">
          Ajouter à la bibliothèque
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Styles omis pour la concision */
</style>