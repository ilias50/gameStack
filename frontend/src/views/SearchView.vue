<script setup>
import { ref } from 'vue';
import GamesService from '@/services/gamesService';
import CollectionService from '@/services/collectionService';
import GameCard from '@/components/GameCard.vue'; // Composant de carte
import NavBar from '@/components/NavBar.vue'; // 👈 NOUVEL IMPORT

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
  game.isAdding = true;

  try {
    const gameDto = {
      apiId: game.apiId,
      title: game.title,
      releaseDate: game.releaseDate,
      imagePath: game.imagePath,
      platform: 1
    };

    await CollectionService.addGameToCollection(gameDto);
    alert(`${game.title} a été ajouté à votre collection !`);

    // Met à jour la propriété pour indiquer que le jeu est ajouté
    game.isAdded = true;

  } catch (error) {
    console.error("Erreur lors de l'ajout:", error);
    alert(`Échec de l'ajout de ${game.title}. Vérifiez la console.`);
  } finally {
    game.isAdding = false;
  }
};
</script>

<template>
  <div class="search-view">
    <NavBar />

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

    <div v-else class="results-grid">
      <div v-for="game in searchResults" :key="game.apiId" class="result-card-container">

        <GameCard :game="game" class="extended-card" />
        <button
            @click="addToCollection(game)"
            class="add-button"
            :disabled="game.isAdding || game.isAdded"
        >
          <span v-if="game.isAdded">Ajouté ✅</span>
          <span v-else-if="game.isAdding">Ajout en cours...</span>
          <span v-else>Ajouter à la bibliothèque</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Les styles restent inchangés... */
.search-view {
  text-align: center;
  padding: 20px;
}
.loading, .alert {
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

/* Styles pour la grille de résultats */
.results-grid {
  display: flex;
  flex-wrap: wrap;
  /* 💡 Augmentation de l'espacement entre les cartes */
  gap: 30px;
  justify-content: center;
  margin-top: 30px;
}

.result-card-container {
  /* 💡 Augmentation de la largeur du conteneur */
  width: 220px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  /* 💡 Le conteneur entier prend la responsabilité du style de bordure/ombre */
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 2px 2px 5px rgba(0,0,0,0.1);
  overflow: hidden; /* Pour que le border-radius s'applique bien aux éléments internes */
}

/* 💡 Nouvelle classe pour modifier GameCard */
.extended-card {
  /* Retire la bordure, l'ombre et le padding du bas du GameCard original */
  border: none !important;
  box-shadow: none !important;
  padding-bottom: 0 !important;
  /* Réduit légèrement le padding latéral pour mieux aligner avec le bouton */
  padding: 15px 10px 0 10px !important;

  /* Assure que la carte prend toute la hauteur nécessaire */
  flex-grow: 1;
  /* Annule l'ancienne marge qui séparait la carte du bouton */
  margin-bottom: 0;
}

/* 💡 Pour aligner le bouton directement sous le contenu de la carte */
.result-card-container > :deep(.game-name) {
  margin-bottom: 10px !important; /* Ajoute un peu d'espace au-dessus du bouton */
}

.add-button {
  width: 100%;
  padding: 12px 10px; /* 💡 Padding augmenté pour une meilleure zone de clic */
  background-color: #28a745;
  color: white;
  border: none;
  /* 💡 Coins inférieurs arrondis pour correspondre au conteneur */
  border-radius: 0 0 8px 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
  /* 💡 S'assure qu'il est bien collé au bas du contenu de la carte */
  margin-top: auto;
}

.add-button:hover:not(:disabled) {
  background-color: #1e7e34;
}

.add-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>