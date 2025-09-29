<script setup>
import { computed } from 'vue';

/**
 * Définition des propriétés (props) que le composant attend de son parent.
 * Nous attendons un objet 'game' qui doit contenir au moins 'title' et 'imagePath'
 * pour être correctement affiché.
 */
const props = defineProps({
  game: {
    type: Object,
    required: true,
    default: () => ({
      id: null,
      title: 'Jeu Inconnu',
      imagePath: 'default-cover.png' // Fallback par défaut si l'objet n'est pas complet
    })
  }
});

// Propriétés calculées pour simplifier l'accès et gérer les valeurs par défaut
const gameTitle = computed(() => props.game.title || 'Jeu Inconnu');
const gameImageSrc = computed(() => props.game.imagePath || 'default-cover.png');
const gameImageAlt = computed(() => `Couverture du jeu ${gameTitle.value}`);

// Événement pour notifier le composant parent lorsque l'utilisateur clique sur le bouton "Détails"
const emit = defineEmits(['details-click']);

const handleDetailsClick = () => {
  // Émet l'événement 'details-click' et passe l'ID du jeu ou l'objet complet
  emit('details-click', props.game.id);
};
</script>

<template>
  <div class="game-card">
    <img
        :src="gameImageSrc"
        :alt="gameImageAlt"
        class="game-image"
    >
    <h3 class="game-name">{{ gameTitle }}</h3>

  </div>
</template>

<style scoped>
/* Styles de la carte de jeu, déplacés ici */
.game-card {
  border: 1px solid #ccc;
  padding: 15px;
  width: 200px;
  box-shadow: 2px 2px 5px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  border-radius: 8px; /* Ajout d'un léger arrondi pour l'esthétique */
}
.game-image {
  width: 100%;
  height: auto;
  aspect-ratio: 3/4; /* Pour maintenir un ratio d'image cohérent (ex: 3:4) */
  object-fit: cover; /* Assure que l'image couvre la zone sans déformation excessive */
  margin-bottom: 10px;
  border-radius: 4px;
}

.game-name {
  margin: 5px 0 15px 0;
  font-size: 1.1em;
  min-height: 2.2em; /* Pour éviter que les cartes ne bougent si le titre est sur 1 ou 2 lignes */
}

</style>
