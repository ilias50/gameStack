<script setup>
import { ref, onMounted } from 'vue';
import GamesService from '@/services/gamesService';
import CollectionService from '@/services/collectionService';
import SearchGameCard from '@/components/SearchGameCard.vue';
import NavBar from '@/components/NavBar.vue';

const searchQuery = ref('');
const searchResults = ref([]);
const isLoading = ref(false);
const platforms = ref([]);
const platformsLoading = ref(true);
const message = ref('');

// --- LOGIQUE DE CHARGEMENT DES PLATEFORMES ---

/**
 * Charge la liste des plateformes disponibles au montage de la vue.
 */
const fetchPlatforms = async () => {
  platformsLoading.value = true;
  try {
    const data = await CollectionService.getAllPlatforms();
    platforms.value = data;
    if (data.length === 0) {
      message.value = 'Aucune plateforme n\'est disponible pour l\'ajout de jeux. Veuillez contacter l\'administrateur.';
    }
  } catch (error) {
    console.error("Erreur lors du chargement des plateformes:", error);
    message.value = 'Erreur lors du chargement des options de plateforme.';
  } finally {
    platformsLoading.value = false;
  }
};

onMounted(fetchPlatforms);

// --- LOGIQUE DE RECHERCHE ---

const handleSearch = async () => {
  if (searchQuery.value.length < 3) {
    message.value = 'Veuillez entrer au moins 3 caractères pour la recherche.';
    searchResults.value = [];
    return;
  }

  message.value = '';
  isLoading.value = true;

  try {
    const results = await GamesService.searchGames(searchQuery.value);

    // 💡 Correction pour éviter la récursivité :
    // On s'assure que l'état initial des jeux (isAdding/isAdded) est toujours présent.
    searchResults.value = results.map(game => ({
      ...game,
      isAdding: false,
      isAdded: false,
    }));

    if (searchResults.value.length === 0) {
      message.value = 'Aucun jeu trouvé pour cette recherche.';
    }
  } catch (error) {
    message.value = 'Erreur lors de la recherche des jeux.';
    console.error("Erreur de recherche:", error);
  } finally {
    isLoading.value = false;
  }
};

// --- LOGIQUE D'AJOUT (ÉVÉNEMENT DU SearchGameCard) ---

/**
 * Gère l'événement d'ajout provenant du SearchGameCard.
 * @param {object} payload - Contient { game, platformId }
 */
const handleAddToCollection = async ({ game, platformId }) => {

  // Trouver le jeu dans la liste locale pour mettre à jour son état
  const gameToUpdate = searchResults.value.find(g => g.apiId === game.apiId);
  if (!gameToUpdate) return;

  gameToUpdate.isAdding = true;

  try {
    const gameDto = {
      apiId: game.apiId,
      title: game.title,
      releaseDate: game.releaseDate,
      imagePath: game.imagePath,
      // Le platformId est l'ID réel choisi, ou 1 (indéfini) par défaut.
      platformId: platformId
    };

    await CollectionService.addGameToCollection(gameDto);

    // Message d'alerte ajusté pour afficher "indéfinie" si platformId est 1
    const platformMsg = platformId === 1 ? 'Plateforme indéfinie' : `Plateforme ID: ${platformId}`;


    gameToUpdate.isAdded = true;

  } catch (error) {
    console.error("Erreur lors de l'ajout:", error);
    alert(`Échec de l'ajout de ${game.title}. Vérifiez la console.`);
  } finally {
    gameToUpdate.isAdding = false;
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
          :disabled="isLoading || platformsLoading"
      >
      <button @click="handleSearch" :disabled="isLoading || platformsLoading">Rechercher</button>
    </div>
    <div v-if="platformsLoading" class="loading-platforms">
      Chargement des options de plateforme...
    </div>
    <div v-else-if="isLoading" class="loading">Recherche en cours...</div>
    <div v-else-if="message" class="alert info">{{ message }}</div>

    <div v-else class="results-grid">
      <SearchGameCard
          v-for="game in searchResults"
          :key="game.apiId"
          :game="game"
          :platforms="platforms"
          @add-click="handleAddToCollection"
      />
    </div>
  </div>
</template>

<style scoped>
.search-view {
  text-align: center;
  padding: 30px 20px; /* Plus de padding vertical */
}
h1 {
  margin-bottom: 40px; /* Plus d'espace sous le titre */
  color: #333;
}
/* --- Messages d'état (Loading/Alert) --- */
.loading, .alert {
  padding: 15px;
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
.loading-platforms {
  color: #6c757d;
  font-style: italic;
  margin-bottom: 15px;
}
.loading {
  color: #007bff;
}

/* --- Formulaire de recherche --- */
.search-form {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 15px; /* Augmenter l'espace entre l'input et le bouton */
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}
.search-form input {
  padding: 12px 15px; /* Rendre l'input plus grand */
  width: 400px; /* Augmenter la largeur de l'input */
  border: 2px solid #ddd;
  border-radius: 8px;
  transition: border-color 0.3s;
  font-size: 1em;
}
.search-form input:focus {
  border-color: #007bff; /* Bleu au focus */
  outline: none;
}
.search-form button {
  padding: 12px 20px; /* Rendre le bouton plus grand */
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}
.search-form button:hover:not(:disabled) {
  background-color: #0056b3;
}
.search-form button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

/* --- Grille de résultats (Clé de l'esthétique) --- */
.results-grid {
  display: flex;
  flex-wrap: wrap; /* CLÉ : permet aux cartes de passer à la ligne */
  gap: 25px; /* Espacement uniforme entre les cartes */
  justify-content: center; /* Centre les cartes horizontalement */
  margin-top: 30px;
}
/* Media Query pour un meilleur affichage sur les petits écrans */
@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
    gap: 10px;
  }
  .search-form input, .search-form button {
    width: 100%;
  }
  .results-grid {
    gap: 15px;
  }
}
</style>