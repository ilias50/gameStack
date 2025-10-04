<template>
  <div class="library-game-card-container">
    <div class="game-display" @click="$emit('details-click', game.id)">
      <img :src="game.imagePath" :alt="game.title" class="game-image" />
      <div class="game-info">
        <h3 class="game-name">{{ game.title }}</h3>

        <p v-if="game.platform" class="game-platform">
          Plateforme : {{ game.platform }}
        </p>
        <p v-else class="game-platform-none">
          Plateforme indéfinie
        </p>
      </div>
    </div>

    <button @click.stop="$emit('remove-click', game.id)" class="remove-btn">
      Supprimer 🗑️
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
// S'il n'importe pas d'autres composants, il est plus stable.

const props = defineProps({
  game: {
    type: Object,
    required: true,
    // Attendu : { id, title, imagePath, platform: { id, name }... }
  },
});

const emits = defineEmits(['details-click', 'remove-click']);
console.log('LibraryGameCard props:', props.game);
// C'est le parent (LibraryView) qui va implémenter handleRemoveClick
</script>

<style scoped>
.library-game-card-container {
  width: 250px; /* Taille fixée pour la grille */
  display: flex;
  flex-direction: column;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.library-game-card-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.game-display {
  cursor: pointer;
  flex-grow: 1; /* Permet à la zone d'affichage de prendre l'espace */
  padding-bottom: 10px;
}

.game-image {
  width: 100%;
  height: auto;
  min-height: 150px;
  object-fit: cover;
}

.game-info {
  padding: 10px;
  text-align: left;
}

.game-name {
  font-size: 1.1em;
  margin: 0 0 5px 0;
  color: #333;
}

.game-platform, .game-platform-none {
  font-size: 0.9em;
  color: #6c757d;
  margin: 0;
}

.remove-btn {
  width: 100%;
  padding: 10px;
  background-color: #dc3545;
  color: white;
  border: none;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
  border-radius: 0 0 8px 8px; /* Seulement en bas */
  margin-top: auto;
}

.remove-btn:hover {
  background-color: #c82333;
}
</style>