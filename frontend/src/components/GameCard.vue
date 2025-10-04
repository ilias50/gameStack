<script setup>
import { defineProps } from 'vue';

// 💡 Assurez-vous que le composant attend la prop 'game'
const props = defineProps({
  game: {
    type: Object,
    required: true,
  },
});

// URL de l'image de fallback si game.imagePath est vide ou null
const fallbackImage = 'chemin/vers/votre/image_par_defaut.png';
// (Remplacez par le chemin réel de votre image de remplacement)

</script>

<template>
  <div class="game-card">

    <img
        :src="game.imagePath || fallbackImage"
        :alt="'Image de ' + game.title"
        class="game-image"
    />

    <div class="game-info">
      <h3 class="game-title">{{ game.title || 'Titre inconnu' }}</h3>
      <p v-if="game.releaseDate" class="game-release-date">
        Sortie : {{ game.releaseDate }}
      </p>
    </div>

  </div>
</template>

<style scoped>
/* (Les styles restent les mêmes) */
.search-game-card {
  width: 220px;
  display: flex;
  flex-direction: column;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.extended-card {
  border: none !important;
  box-shadow: none !important;
  padding-bottom: 0 !important;
  padding: 15px 10px 10px 10px !important;
  flex-grow: 1;
}

.action-bar {
  display: flex;
  flex-direction: column;
  padding: 10px;
  gap: 8px;
  margin-top: auto;
}

.platform-select {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #ced4da;
  border-radius: 5px;
  background-color: white;
  font-size: 1em;
}

.add-button {
  width: 100%;
  padding: 12px 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
  border-radius: 0 0 8px 8px;
}

.add-button:hover:not(:disabled) {
  background-color: #1e7e34;
}

.add-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.game-image {
  width: 100%;
  /* 💡 CLÉ : Fixer une hauteur maximale pour éviter l'étirement vertical */
  max-height: 180px;
  /* Si possible, utilisez 'cover' pour remplir l'espace, ou 'contain' si les images sont de mauvaises qualités */
  object-fit: contain;
  height: auto;
  border-radius: 4px;
  margin-bottom: 10px;
}
</style>