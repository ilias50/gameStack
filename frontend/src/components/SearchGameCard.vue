<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import GameCard from '@/components/GameCard.vue';

const props = defineProps({
  game: {
    type: Object,
    required: true,
  },
  platforms: {
    type: Array,
    required: true,
    default: () => [],
  },
});

const emits = defineEmits(['add-click']);

const selectedPlatformId = ref('1');


const handleAddClick = () => {
  const finalPlatformId = Number(selectedPlatformId.value);

  emits('add-click', {
    game: props.game,
    platformId: finalPlatformId
  });
};
</script>

<template>
  <div v-if="game" class="search-game-card">
    <GameCard :game="game" class="extended-card" :style="{ cursor: 'default' }" />

    <div class="action-bar">
      <select
          v-model="selectedPlatformId"
          class="platform-select"
          :disabled="game.isAdding || game.isAdded || platforms.length === 0"
      >
        <option value="1">Plateforme indéfinie (par défaut)</option>

        <option
            v-for="platform in platforms"
            :key="platform.id"
            :value="String(platform.id)"
        >
          {{ platform.name }}
        </option>
      </select>

      <button
          @click="handleAddClick"
          class="add-button"
          :disabled="game.isAdding || game.isAdded"
      >
        <span v-if="game.isAdded">Ajouté ✅</span>
        <span v-else-if="game.isAdding">Ajout en cours...</span>
        <span v-else>Ajouter</span>
      </button>

    </div>
  </div>
</template>

<style scoped>
/* COPIÉ/ADAPTÉ du style de la carte de bibliothèque */
.search-game-card {
  /* Conteneur principal de la carte */
  width: 250px; /* 💡 Taille fixée comme la carte de bibliothèque */
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border: 1px solid #ddd; /* Bordure plus douce */
  border-radius: 8px; /* Rayon de bordure comme la bibliothèque */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Ombre comme la bibliothèque */
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  height: auto;
}

/* Effet de survol (copié de la carte de bibliothèque) */
.search-game-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

/* Style appliqué au GameCard de base */
/* On s'assure qu'il prend l'espace du haut et n'a pas de bordure/ombre en double */
.extended-card {
  border: none !important;
  box-shadow: none !important;
  padding: 10px 10px 5px 10px !important;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;

  /* 💡 CLÉ : Réduire le min-height ici pour compacter la carte verticalement */
  min-height: 150px; /* Essayons 150px pour laisser plus de place au select */
}

/* Conteneur des actions (sélecteur et bouton) */
.action-bar {
  display: flex;
  flex-direction: column;
  padding: 10px; /* Padding uniforme */
  gap: 8px;
  margin-top: auto;
  width: 100%;
}

/* Sélecteur de plateforme */
.platform-select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  background-color: white;
  font-size: 0.9em;
  cursor: pointer;
}

.platform-select:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

/* Bouton Ajouter (Style mis à jour pour être harmonieux) */
.add-button {
  width: 100%;
  padding: 10px;
  background-color: #28a745; /* Vert pour l'ajout, couleur de succès */
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  font-size: 0.95em;
  transition: background-color 0.3s;
}

.add-button:hover:not(:disabled) {
  background-color: #1e7e34;
}

/* Styles spécifiques aux états du bouton */
.add-button span:first-child {
  /* Pas besoin de style ici si c'est le bouton qui a le fond vert */
}

/* Ajouté ✅ */
.add-button:disabled:has(span[v-if="game.isAdded"]) {
  background-color: #28a745;
  color: white;
}
/* En cours... */
.add-button:disabled:has(span[v-else-if="game.isAdding"]) {
  background-color: #ffc107; /* Jaune */
  color: #343a40;
}

/* Désactivé générique */
.add-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>