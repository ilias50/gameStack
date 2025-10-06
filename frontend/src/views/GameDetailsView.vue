<template>
  <div class="game-details-view">
    <NavBar />

    <div v-if="isLoading" class="loading-message">Chargement des détails du jeu...</div>

    <div v-else-if="error" class="alert error">{{ error }}</div>

    <div v-else-if="gameDetails">
      <h1>{{ gameDetails.name }}</h1>

      <!-- Le bouton affiche l'état isInCollection pour le débogage -->
      <button @click="logDetails" class="debug-btn">
        Afficher les données brutes dans la console (Collection: {{ isInCollection ? 'Oui' : 'Non' }})
      </button>

      <!-- NOUVELLE POSITION DU SYNOPSIS : Pleine largeur sous le titre -->
      <div class="synopsis-section">
        <h2>Description</h2>
        <!-- Utilisation de v-html pour permettre l'affichage du formatage RAWG (gras, italique, etc.) -->
        <p class="description-text" v-html="gameDetails.description_raw"></p>
        <hr>
      </div>

      <div class="content-wrapper">

        <!-- SECTION MÉDIA PRINCIPALE (CAROUSEL / LECTEUR VIDÉO) -->
        <div class="media-section">
          <h2>Média</h2>

          <!-- Conteneur principal pour le média actif -->
          <div class="main-media-viewer">

            <!-- Affichage de la CAPTURE D'ÉCRAN (Carrousel) -->
            <div
                v-if="currentMediaType === 'screenshot' && currentImageSrc"
                class="image-carousel"
            >
              <button @click="prevMediaItem" class="carousel-btn prev-btn">❮</button>
              <img :src="currentImageSrc" :alt="gameDetails.name" class="game-image-detail" />
              <button @click="nextMediaItem" class="carousel-btn next-btn">❯</button>
              <div class="carousel-indicators">
                Capture {{ currentImageIndex + 1 }} / {{ gameScreenshots.length }}
              </div>
            </div>

            <!-- Affichage du TRAILER (Lecteur Vidéo) -->
            <div
                v-else-if="currentMediaType === 'trailer' && currentTrailerUrl"
                class="trailer-player"
            >
              <!-- Retiré le <h3> pour une intégration plus fluide dans le carrousel -->
              <video controls autoplay :src="currentTrailerUrl" class="game-trailer-video">
                Votre navigateur ne supporte pas la balise vidéo.
              </video>
              <div class="carousel-indicators">
                Trailer {{ currentTrailerIndex + 1 }} / {{ gameTrailers.length }}
              </div>
              <!-- Ajout des boutons pour naviguer entre les trailers -->
              <!-- Les boutons ici sont redondants si on les a déjà dans la navigation générale du carrousel -->
            </div>

            <div v-else class="no-media-message">
              Aucun média disponible.
            </div>

          </div> <!-- /main-media-viewer -->

          <!-- BARRE DE VIGNETTES (THUMBNAILS) -->
          <div class="thumbnails-strip" v-if="allMediaThumbnails.length > 0">
            <div
                v-for="(item, index) in allMediaThumbnails"
                :key="item.type + item.index"
                :class="['thumbnail-item', {
                    'active-thumb': isCurrentMedia(item.type, item.index),
                    'is-trailer': item.type === 'trailer'
                }]"
                @click="selectMedia(item.type, item.index)"
                :title="item.name"
            >
              <img :src="item.thumbUrl" :alt="item.name" class="thumbnail-image" />
              <span v-if="item.type === 'trailer'" class="play-icon">▶</span>
            </div>
          </div>
        </div>

        <!-- SECTION INFO ET ÉDITION (Maintenant seulement l'info de la plateforme) -->
        <div class="info-section">
          <h2>Informations</h2>
          <!-- Affiche l'ID RAWG depuis la route, qui a servi à charger la page -->
          <p><strong>ID RAWG:</strong> {{ route.params.id }}</p>
          <!-- AJOUT DU MESSAGE D'ÉTAT DE L'ID DE COLLECTION POUR LE DEBUG -->
          <p v-if="collectionEntryId">
            <!-- Mise à jour du style tailwind pour la couleur verte -->
            <strong>ID Collection (UserGameId):</strong> <span class="collection-id-found">{{ collectionEntryId }}</span>
          </p>
          <p v-else>
            <!-- Mise à jour du style tailwind pour la couleur rouge -->
            <strong>ID Collection:</strong> <span class="collection-id-not-found">Non trouvé</span>
          </p>

          <div class="platform-editor">
            <!-- LOGIQUE CONDITIONNELLE pour afficher l'éditeur ou le bouton d'ajout -->
            <div v-if="isInCollection" class="collection-status-box">
              <!-- Affichage mis à jour -->
              <h3>Plateforme Actuelle : <span class="current-platform">{{ currentPlatformName || 'Chargement...' }}</span></h3>

              <select v-model="selectedPlatformId" class="platform-select-edit">
                <option value="" disabled>Choisir une nouvelle plateforme</option>
                <option
                    v-for="platform in availablePlatforms"
                    :key="platform.id"
                    :value="platform.id"
                >
                  {{ platform.name }}
                </option>
              </select>

              <button
                  @click="updatePlatform"
                  :disabled="!selectedPlatformId || isUpdating"
                  class="save-btn"
              >
                {{ isUpdating ? 'Mise à jour...' : 'Modifier la plateforme' }}
              </button>
            </div>

            <div v-else class="collection-status-box not-in-collection">
              <p>Ce jeu n'est pas encore dans votre collection.</p>
              <button class="add-btn">Ajouter à la collection (Placeholder)</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import GamesService from '@/services/gamesService';
import CollectionService from '@/services/collectionService'; // Import du service de collection
import NavBar from '@/components/NavBar.vue';

const route = useRoute();
const gameDetails = ref(null);
const availablePlatforms = ref([]);
const currentPlatformName = ref(null);
const selectedPlatformId = ref('');

// NOUVELLES PROPRIÉTÉS POUR LA COLLECTION
const collectionEntryId = ref(null); // ID dans la collection de l'utilisateur (UserGameId)
const isInCollection = computed(() => !!collectionEntryId.value);

const gameTrailers = ref(null);
const gameScreenshots = ref(null);

const currentImageIndex = ref(0); // Index de la capture d'écran active
const currentTrailerIndex = ref(0); // Index du trailer actif
const currentMediaType = ref('screenshot'); // 'screenshot' ou 'trailer'

const isLoading = ref(true);
const isUpdating = ref(false);
const error = ref(null);

const logDetails = () => {
  console.log("--- Détails bruts du jeu (GameDetails) ---");
  console.log(gameDetails.value);
  console.log("--- Collection Entry ID (UserGameId) ---");
  console.log(collectionEntryId.value);
  console.log("--- Statut isInCollection ---");
  console.log(isInCollection.value); // AJOUTÉ
  console.log("--- Nom de la plateforme affichée ---");
  console.log(currentPlatformName.value);
  console.log("--- Trailers reçus ---");
  console.log(gameTrailers.value);
  console.log("--- Captures d'écran reçues ---");
  console.log(gameScreenshots.value);
  console.log("--- Plateformes disponibles (AvailablePlatforms) ---");
  console.log(availablePlatforms.value);
};

// --- LOGIQUE CAROUSEL ET TRAILER ---

// Source de l'image actuellement affichée dans le carrousel (uniquement si 'screenshot' est actif)
const currentImageSrc = computed(() => {
  // Gère le cas où gameScreenshots est null/vide
  if (!gameScreenshots.value || gameScreenshots.value.length === 0) {
    return gameDetails.value?.background_image || null;
  }
  // Utilise le champ 'image' du DTO RawgScreenshotResponse
  return gameScreenshots.value[currentImageIndex.value].image;
});

// URL du trailer actuellement affiché (uniquement si 'trailer' est actif)
const currentTrailerUrl = computed(() => {
  if (gameTrailers.value && gameTrailers.value.length > currentTrailerIndex.value) {
    const trailer = gameTrailers.value[currentTrailerIndex.value];
    // Cherche la résolution maximale ('max'), sinon on prend le '480p'
    if (trailer.data) {
      // L'accès aux champs 'max' et 'video480p' est correct
      return trailer.data.max || trailer.data.video480p;
    }
  }
  return null;
});

// Propriété fusionnée pour la barre de vignettes
const allMediaThumbnails = computed(() => {
  const thumbnails = [];
  if (gameScreenshots.value) {
    gameScreenshots.value.forEach((screenshot, index) => {
      thumbnails.push({
        type: 'screenshot',
        index: index,
        // Utilise le champ 'image' du DTO RawgScreenshotResponse pour la vignette
        thumbUrl: screenshot.image,
        name: `Capture ${index + 1}`
      });
    });
  }
  if (gameTrailers.value) {
    gameTrailers.value.forEach((trailer, index) => {
      thumbnails.push({
        type: 'trailer',
        index: index,
        // Utilise l'image de prévisualisation du trailer ou l'image de fond en fallback
        thumbUrl: trailer.preview || gameDetails.value?.background_image || 'https://placehold.co/120x68/cccccc/333333?text=Trailer',
        name: `Trailer ${index + 1}`
      });
    });
  }
  return thumbnails;
});

// Fonction pour sélectionner un média par clic sur la vignette
const selectMedia = (type, index) => {
  currentMediaType.value = type;
  if (type === 'screenshot') {
    currentImageIndex.value = index;
  } else if (type === 'trailer') {
    currentTrailerIndex.value = index;
  }
};

// Vérifie si la vignette est active
const isCurrentMedia = (type, index) => {
  return currentMediaType.value === type &&
      (type === 'screenshot' ? currentImageIndex.value === index : currentTrailerIndex.value === index);
};

// Navigation droite (NEXT) : passe de la dernière capture au premier trailer, ou boucle.
const nextMediaItem = () => {
  // Cas 1: Actuellement sur les captures d'écran
  if (currentMediaType.value === 'screenshot' && gameScreenshots.value && gameScreenshots.value.length > 0) {
    const nextIndex = currentImageIndex.value + 1;

    // Si c'est la dernière capture
    if (nextIndex >= gameScreenshots.value.length) {
      // Si des trailers existent, bascule vers le premier trailer
      if (gameTrailers.value && gameTrailers.value.length > 0) {
        currentMediaType.value = 'trailer';
        currentTrailerIndex.value = 0;
      } else {
        // Sinon, boucle vers la première capture
        currentImageIndex.value = 0;
      }
    } else {
      // Passe à la capture suivante
      currentImageIndex.value = nextIndex;
    }

    // Cas 2: Actuellement sur les trailers
  } else if (currentMediaType.value === 'trailer' && gameTrailers.value && gameTrailers.value.length > 0) {
    const nextIndex = currentTrailerIndex.value + 1;

    // Si c'est le dernier trailer
    if (nextIndex >= gameTrailers.value.length) {
      // Si des captures d'écran existent, bascule vers la première capture
      if (gameScreenshots.value && gameScreenshots.value.length > 0) {
        currentMediaType.value = 'screenshot';
        currentImageIndex.value = 0;
      } else {
        // Sinon, boucle vers le premier trailer
        currentTrailerIndex.value = 0;
      }
    } else {
      // Passe au trailer suivant
      currentTrailerIndex.value = nextIndex;
    }
  }
};

// Navigation gauche (PREV) : passe du premier élément au dernier élément précédent (boucle inversée).
const prevMediaItem = () => {
  // Cas 1: Actuellement sur les captures d'écran
  if (currentMediaType.value === 'screenshot' && gameScreenshots.value && gameScreenshots.value.length > 0) {
    const prevIndex = currentImageIndex.value - 1;

    // Si c'est la première capture
    if (prevIndex < 0) {
      // Si des trailers existent, bascule vers le dernier trailer
      if (gameTrailers.value && gameTrailers.value.length > 0) {
        currentMediaType.value = 'trailer';
        currentTrailerIndex.value = gameTrailers.value.length - 1;
      } else {
        // Sinon, boucle vers la dernière capture
        currentImageIndex.value = gameScreenshots.value.length - 1;
      }
    } else {
      // Passe à la capture précédente
      currentImageIndex.value = prevIndex;
    }

    // Cas 2: Actuellement sur les trailers
  } else if (currentMediaType.value === 'trailer' && gameTrailers.value && gameTrailers.value.length > 0) {
    const prevIndex = currentTrailerIndex.value - 1;

    // Si c'est le premier trailer
    if (prevIndex < 0) {
      // Si des captures d'écran existent, bascule vers la dernière capture
      if (gameScreenshots.value && gameScreenshots.value.length > 0) {
        currentMediaType.value = 'screenshot';
        currentImageIndex.value = gameScreenshots.value.length - 1;
      } else {
        // Sinon, boucle vers le dernier trailer
        currentTrailerIndex.value = gameTrailers.value.length - 1;
      }
    } else {
      // Passe au trailer précédent
      currentTrailerIndex.value = prevIndex;
    }
  }
};

// --- LOGIQUE DE FETCHING ---

const fetchScreenshotsAndTrailers = async (gameRawgId, details) => {
  try {
    // 1. Récupération des Trailers
    const trailersResponse = await GamesService.getGameTrailers(gameRawgId);
    gameTrailers.value = trailersResponse.results;

    console.log(`[DEBUG TRAILERS] Nombre de trailers reçus: ${gameTrailers.value ? gameTrailers.value.length : 0}`);

    // 2. RÉCUPÉRATION DES VRAIES CAPTURES D'ÉCRAN
    let fetchedScreenshots = [];

    try {
      const screenshotsResponse = await GamesService.getGameScreenshots(gameRawgId);
      // Utilise les vrais résultats du backend
      fetchedScreenshots = screenshotsResponse.results || [];
      console.log(`[DEBUG SCREENSHOTS] Nombre de vraies captures d'écran reçues: ${fetchedScreenshots.length}`);

    } catch (e) {
      // Fallback si l'API échoue: on utilise les low-res (short_screenshots) de l'objet de détail
      console.warn("[DetailsView] Échec de l'appel getGameScreenshots. Utilisation du fallback (short_screenshots).", e);
      fetchedScreenshots = details.short_screenshots || [];
    }

    // NOUVELLE LOGIQUE : AJOUTER L'IMAGE DE FOND EN PREMIER ÉLÉMENT (pour garder l'image "initiale")
    if (details.background_image) {
      // Objet standard pour l'image principale
      const mainImage = { id: 'bg', image: details.background_image };

      // Vérifie si cette image n'est pas déjà présente dans la liste (pour éviter les doublons)
      const isAlreadyPresent = fetchedScreenshots.some(s => s.image === details.background_image);

      if (!isAlreadyPresent) {
        // Prépare la liste avec l'image principale en premier
        gameScreenshots.value = [mainImage, ...fetchedScreenshots];
      } else {
        // Si l'image de fond est déjà là, on garde la liste telle quelle
        gameScreenshots.value = fetchedScreenshots;
      }

    } else {
      gameScreenshots.value = fetchedScreenshots;
    }

    // Si des trailers existent et qu'il n'y a pas de captures d'écran, basculez l'affichage par défaut sur le premier trailer.
    if ((!gameScreenshots.value || gameScreenshots.value.length === 0) && gameTrailers.value.length > 0) {
      currentMediaType.value = 'trailer';
    }


  } catch (err) {
    console.error("[DetailsView] ERREUR lors de la récupération des trailers/screenshots:", err);
  }
}

/**
 * Récupère les détails du jeu et initialise l'état de la collection.
 * @param {number} gameRawgId ID du jeu RAWG.
 * @param {string} initialCollectionId ID de l'entrée dans la collection (optionnel, vient de l'URL).
 * @param {string} initialPlatformName Nom de la plateforme (optionnel, vient de l'URL).
 */
const fetchDetails = async (gameRawgId, initialCollectionId, initialPlatformName) => {
  console.log(`[DetailsView] Début du chargement pour l'ID RAWG: ${gameRawgId}`);

  // 1. Initialiser l'état de la collection et de la plateforme avec les données de l'URL (si présentes)
  if (initialCollectionId) {
    collectionEntryId.value = initialCollectionId;
    console.log(`[DetailsView] ID Collection initialisé via URL: ${initialCollectionId}`);
  }

  if (initialPlatformName) {
    currentPlatformName.value = initialPlatformName;
    console.log(`[DetailsView] Nom de la Plateforme initialisé via URL: ${initialPlatformName}`);
  }

  try {
    const details = await GamesService.getGameDetails(gameRawgId);
    gameDetails.value = details;

    console.log("[DetailsView] Réponse complète du backend (Détails + Collection):", details);

    // 2. Vérifier si le Gateway a enrichi l'objet avec l'ID d'entrée de collection
    if (details.userGameId && !collectionEntryId.value) {
      collectionEntryId.value = details.userGameId;
      console.log(`[DetailsView] ID Collection mis à jour via l'objet détails du backend: ${details.userGameId}`);
    }

    // 3. Logique d'affichage du nom de la plateforme : Priorité à l'URL, puis au Gateway, puis au Fallback CollectionService

    if (collectionEntryId.value) {
      // On a un ID de collection. On s'assure d'avoir le nom de la plateforme.

      if (!currentPlatformName.value) {
        // Nom de la plateforme manquant de l'URL. On essaie le Gateway.
        if (details.platform) {
          currentPlatformName.value = details.platform;
          console.log(`[DetailsView] Plateforme définie via la réponse enrichie du Gateway: ${details.platform}`);
        } else {
          // Nom de la plateforme manquant de l'URL ET du Gateway. Fallback via CollectionService.
          console.log(`[DetailsView] Nom de la plateforme manquant. Tentative de récupération directe via CollectionService (Fallback) pour l'ID: ${collectionEntryId.value}`);

          const entryDetails = await CollectionService.getUserGameEntry(collectionEntryId.value);

          if (entryDetails && entryDetails.platformName) {
            currentPlatformName.value = entryDetails.platformName;
            console.log(`[DetailsView] Succès du fallback. Plateforme définie à: ${currentPlatformName.value}`);
          } else {
            currentPlatformName.value = "Données collection incomplètes";
            console.warn(`[DetailsView] Échec du fallback. L'entrée de collection ${collectionEntryId.value} n'a pas retourné de nom de plateforme.`);
          }
        }
      }
      // Si currentPlatformName.value a été défini par l'URL, il est préservé ici.

    } else {
      // Si, après toutes les vérifications, nous n'avons AUCUN ID de collection, on réinitialise le nom.
      currentPlatformName.value = null;
      console.log("[DetailsView] Le jeu n'est pas dans la collection (ID manquant). Réinitialisation de la plateforme affichée.");
    }

    // Récupération des plateformes disponibles (la liste complète est nécessaire pour le <select>)
    availablePlatforms.value = await CollectionService.getAllPlatforms();

    // Récupération des médias
    await fetchScreenshotsAndTrailers(gameRawgId, details);

  } catch (err) {
    const status = err.response ? err.response.status : 'inconnu';
    error.value = `Impossible de charger les détails du jeu. (Statut: ${status}). Vérifiez le Gateway.`;
    console.error("[DetailsView] ERREUR lors de la récupération des données:", err);
  } finally {
    isLoading.value = false;
    // Log FINAL pour aider au débogage
    console.log(`[DetailsView] CHARGEMENT TERMINÉ. État final: ID Collection=${collectionEntryId.value}, Nom Plateforme=${currentPlatformName.value}, Dans Collection=${isInCollection.value}`);
  }
};

const updatePlatform = async () => {
  // Assurez-vous que nous avons un jeu sélectionné, une nouvelle plateforme, ET un ID d'entrée de collection
  if (!selectedPlatformId.value) {
    console.warn("Veuillez choisir une plateforme.");
    return;
  }

  if (!collectionEntryId.value) {
    console.error("Erreur: Tentative de mise à jour d'un jeu sans ID d'entrée de collection (UserGameId).");
    // Message à l'utilisateur au lieu de l'alerte
    error.value = "Impossible de mettre à jour. L'ID de collection est manquant. Rechargez la page.";
    return;
  }

  // Utilise collectionEntryId.value (l'ID de l'entrée dans la collection)
  const userGameId = collectionEntryId.value;
  const newPlatformId = selectedPlatformId.value;

  isUpdating.value = true;

  try {

    // Appel du service pour mettre à jour la plateforme
    await CollectionService.updateGamePlatform(userGameId, newPlatformId);

    const newPlatform = availablePlatforms.value.find(p => p.id === newPlatformId);

    if (newPlatform) {
      // Mise à jour de l'affichage local si la modification a réussi
      currentPlatformName.value = newPlatform.name;
      selectedPlatformId.value = '';
      error.value = null; // Efface l'erreur précédente si la mise à jour réussit
      console.log(`Plateforme mise à jour avec succès : ${newPlatform.name} pour le jeu ${userGameId}`);
    }

  } catch (err) {
    console.error("[DetailsView] Échec de la mise à jour de la plateforme:", err);
    // Affichage d'un message d'erreur plus clair en cas d'échec
    error.value = `Échec de la mise à jour: ${err.message}. L'ID utilisé était ${userGameId}.`;
  } finally {
    isUpdating.value = false;
  }
};


onMounted(() => {
  // Lit l'ID RAWG depuis l'URL (ex: /games/12345)
  const gameRawgId = parseInt(route.params.id);
  // Lit l'ID de collection depuis le query param (ex: ?collectionId=XYZ)
  const initialCollectionId = route.query.collectionId;
  // NOUVEAU : Lit le nom de la plateforme depuis le query param (ex: ?platformName=PlayStation%205)
  const initialPlatformName = route.query.platformName;


  if (isNaN(gameRawgId)) {
    error.value = "ID de jeu invalide dans l'URL.";
    isLoading.value = false;
  } else {
    // Passe les deux informations à fetchDetails
    fetchDetails(gameRawgId, initialCollectionId, initialPlatformName);
  }
});
</script>

<style scoped>
.game-details-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

/* Nouveau style pour le synopsis pleine largeur */
.synopsis-section {
  padding: 10px 0;
  margin-bottom: 20px;
  /* La description n'est plus dans la info-section, donc on enlève la bordure et le fond */
}

.content-wrapper {
  display: flex;
  flex-direction: row;
  gap: 30px;
  margin-top: 20px; /* Moins de marge car le synopsis est au-dessus */
}

/* Flexbox pour le responsive */
@media (max-width: 900px) {
  .content-wrapper {
    flex-direction: column;
  }
}

/* --- MEDIA SECTION (Screenshots & Trailer) --- */
.media-section {
  flex: 2;
}

/* Conteneur pour le média principal (Carrousel ou Vidéo) */
.main-media-viewer {
  position: relative;
}

/* Carrousel/Vidéo */
.image-carousel, .trailer-player {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
  background-color: #000;
}

.game-image-detail {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.game-trailer-video {
  width: 100%;
  height: 100%;
  object-fit: contain; /* Utiliser contain pour éviter le crop sur la vidéo */
  display: block;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(44, 62, 80, 0.7);
  color: white;
  border: none;
  padding: 15px 18px;
  cursor: pointer;
  z-index: 10;
  font-size: 1.2rem;
  line-height: 1;
  border-radius: 50%;
  transition: background 0.3s;
}
.carousel-btn:hover {
  background: rgba(52, 152, 219, 0.9);
}
.prev-btn { left: 15px; }
.next-btn { right: 15px; }

.carousel-indicators {
  position: absolute;
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 0.85rem;
  z-index: 11;
}

.no-media-message {
  text-align: center;
  padding: 50px;
  background-color: #eee;
  border-radius: 8px;
  aspect-ratio: 16 / 9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #777;
}

/* --- THUMBNAILS STRIP --- */
.thumbnails-strip {
  display: flex;
  gap: 10px;
  overflow-x: auto; /* Permet le défilement horizontal */
  padding-bottom: 10px; /* Espace pour la barre de défilement */
  margin-top: 10px;
}

.thumbnail-item {
  flex-shrink: 0; /* Empêche les vignettes de se compresser */
  width: 120px;
  height: 68px; /* 16:9 ratio (120x67.5), 68 pour la marge */
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.2s ease;
  position: relative;
}

.thumbnail-item:hover {
  border-color: #2980b9;
}

.active-thumb {
  border-color: #2ecc71 !important;
  box-shadow: 0 0 10px rgba(46, 204, 113, 0.8);
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.is-trailer .play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 24px;
  text-shadow: 0 0 5px rgba(0, 0, 0, 0.8);
}


/* --- INFO SECTION (Maintenant, plus compact) --- */
.info-section {
  flex: 1;
  padding: 20px;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  min-width: 300px; /* Pour éviter de trop compresser sur les petits écrans */
}

.description-text {
  /* Utilise v-html dans le template, donc on s'assure que le contenu est bien affiché */
  white-space: pre-wrap;
  text-align: justify;
  margin-top: 10px;
}

.debug-btn {
  margin-bottom: 20px;
  padding: 8px 15px;
  background-color: #2c3e50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* Collection ID status colors */
.collection-id-found {
  font-weight: 600; /* font-semibold */
  color: #16a34a; /* text-green-600 */
}
.collection-id-not-found {
  font-weight: 600; /* font-semibold */
  color: #dc2626; /* text-red-600 */
}


/* Platform Editor Styles */
.platform-editor {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  margin-top: 20px;
  background-color: #fff;
}
.current-platform {
  font-weight: bold;
  color: #2980b9;
}
.platform-select-edit, .save-btn {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  border-radius: 4px;
}
.save-btn {
  background-color: #2ecc71;
  color: white;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}
.save-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.add-btn {
  width: 100%;
  padding: 10px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.collection-status-box {
  padding-top: 10px;
}
.not-in-collection {
  text-align: center;
  padding: 10px 0;
}
</style>
