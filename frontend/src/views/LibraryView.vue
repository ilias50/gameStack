<script setup>
import { ref, onMounted, computed } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import CollectionService from '@/services/collectionService';
import LibraryGameCard from '@/components/LibraryGameCard.vue';
import NavBar from '@/components/NavBar.vue';

const router = useRouter();

// --- ETAT PRINCIPAL ---
const userGames = ref([]);
// REMARQUE: La liste "platforms" du backend n'est plus utile pour le FILTRE,
// car les jeux ne contiennent que le nom de la plateforme (string).
const selectedPlatformName = ref(''); // <-- On stocke le NOM de la plateforme sélectionnée

const isLoading = ref(true);
const error = ref(null);

// --- FONCTIONS DE RÉCUPÉRATION DES DONNÉES ---

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
  // On peut supprimer fetchPlatforms, car on n'en a plus besoin pour le filtre !
  // Si vous l'utilisez ailleurs, gardez-la, mais pour le filtre ici, elle n'est pas nécessaire.
};

onMounted(fetchCollection);


// --- LOGIQUE DE FILTRAGE ET DE TRI ---

/**
 * Propriété calculée qui retourne la liste des NOMS de plateformes uniques.
 */
const platformNamesInCollection = computed(() => {
  // 1. Récupérer tous les noms de plateformes
  const names = userGames.value
      .map(game => game.platform)
      .filter(name => name); // Retirer les plateformes nulles/vides

  // 2. Créer un Set pour obtenir les noms uniques
  const uniqueNames = [...new Set(names)];

  // 3. Trier les noms par ordre alphabétique
  return uniqueNames.sort((a, b) => a.localeCompare(b));
});


/**
 * Propriété calculée qui retourne la liste des jeux filtrée ET triée.
 */
const filteredCollection = computed(() => {
  let filtered = userGames.value;

  // 1. FILTRAGE
  if (selectedPlatformName.value) { // <-- On filtre par le nom de la plateforme
    const selectedName = selectedPlatformName.value;
    filtered = filtered.filter(game =>
        game.platform === selectedName
    );
  }

  // 2. TRI PAR ORDRE ALPHABÉTIQUE (Titre)
  // On utilise slice() pour créer une copie du tableau avant de le trier
  return filtered.slice().sort((a, b) => {
    const titleA = a.title.toUpperCase();
    const titleB = b.title.toUpperCase();

    if (titleA < titleB) {
      return -1;
    }
    if (titleA > titleB) {
      return 1;
    }
    return 0;
  });
});

/**
 * Fonction pour compter les jeux par nom de plateforme.
 */
const countGamesByPlatform = (platformName) => {
  if (!platformName) return userGames.value.length;
  return userGames.value.filter(game =>
      game.platform === platformName
  ).length;
};

const handleRemoveClick = async (gameId) => {
  if (confirm("Êtes-vous sûr de vouloir supprimer ce jeu de votre collection ?")) {
    try {
      await CollectionService.removeGameFromCollection(gameId);
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
 * @param {number} gameId L'ID LOCAL du jeu passé par l'événement.
 */
const handleDetailsClick = (gameId) => {
  // Trouver le jeu dans la collection pour obtenir son apiId (ID RAWG)
  const game = userGames.value.find(g => g.id === gameId);

  if (game && game.apiId) {
    console.log(`Navigation vers les détails pour l'ID RAWG: ${game.apiId}`);
    // Utiliser l'ID RAWG (apiId) pour la route de détails
    router.push(`/games/${game.apiId}?collectionId=${gameId}&platformName=${game.platform}`);
  } else {
    console.error(`Jeu non trouvé ou apiId manquant pour l'ID local: ${gameId}`);
    // Gérer l'erreur ou naviguer vers une page d'erreur
  }
};
</script>

<template>
  <div class="library-view">
    <NavBar />

    <h1>Ma Bibliothèque de Jeux 🎮</h1>

    <div v-if="isLoading" class="loading-message">Chargement de votre collection...</div>

    <div v-else-if="error" class="alert error">{{ error }}</div>

    <div v-else>
      <div class="filter-controls">
        <select v-model="selectedPlatformName" class="platform-select">
          <option value="">Toutes les plateformes ({{ userGames.length }})</option>
          <option
              v-for="platformName in platformNamesInCollection"
              :key="platformName"
              :value="platformName"
          >
            {{ platformName }} ({{ countGamesByPlatform(platformName) }})
          </option>
        </select>

        <div class="platform-buttons">
          <button
              @click="selectedPlatformName = ''"
              :class="{ active: selectedPlatformName === '' }"
              class="platform-btn all-btn"
          >
            Tous les jeux ({{ userGames.length }})
          </button>

          <button
              v-for="platformName in platformNamesInCollection"
              :key="'btn-' + platformName"
              @click="selectedPlatformName = platformName"
              :class="{ active: selectedPlatformName === platformName }"
              class="platform-btn"
          >
            {{ platformName }} ({{ countGamesByPlatform(platformName) }})
          </button>
        </div>
      </div>
      <div v-if="filteredCollection.length === 0" class="alert info empty-filter-message">
            <span v-if="userGames.length === 0">
                Votre bibliothèque est vide. Rendez-vous à la page de <RouterLink to="/search">Recherche</RouterLink> pour ajouter des jeux !
            </span>
        <span v-else>
                Aucun jeu trouvé pour la plateforme sélectionnée. 😔
            </span>
      </div>

      <div v-else class="game-list">
        <LibraryGameCard
            v-for="userGame in filteredCollection"
            :key="userGame.id"
            :game="userGame"
            @details-click="handleDetailsClick"
            @remove-click="handleRemoveClick"
        />
      </div>
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

.library-view {
     text-align: center;
     padding: 20px;
}

h1 {
  margin-bottom: 40px;
}

.filter-controls {
  margin: 0 auto 30px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #f9f9f9;
  max-width: 900px; /* Adaptez à la largeur de votre conteneur */
}

.platform-select {
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 1em;
  margin-bottom: 15px;
  display: block;
  width: 100%;
  max-width: 300px; /* Limite la largeur du dropdown */
  margin: 0 auto 15px auto;
}

.platform-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.platform-btn {
  padding: 8px 15px;
  border: 1px solid #007bff;
  border-radius: 20px;
  background-color: white;
  color: #007bff;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s, transform 0.1s;
  font-weight: bold;
  white-space: nowrap; /* Empêche la coupure du texte des boutons */
}

.platform-btn:hover {
  background-color: #e2f0ff;
  transform: translateY(-1px);
}

.platform-btn.active, .platform-btn.active:hover {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
  transform: none;
}

.all-btn {
  border-color: #6c757d;
  color: #6c757d;
}

.all-btn.active {
  background-color: #6c757d;
  border-color: #6c757d;
  color: white;
}

.empty-filter-message {
  max-width: 800px;
  /* Assurez-vous que le message s'affiche au centre sous les filtres */
}

.game-list {
  /* ... (Gardez vos styles game-list existants pour la grille) */
}
</style>
