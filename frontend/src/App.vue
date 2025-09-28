<script setup>
import { RouterView } from 'vue-router';
// Nous n'avons pas besoin de RouterLink ni de AuthService pour l'instant
// import { RouterLink } from 'vue-router';
// import AuthService from '@/services/authService';
</script>

<template>
  <main>
    <RouterView />
  </main>
</template>

<style scoped>
/* Supprimez les styles de header/nav si vous avez supprimé la balise */
main {
  /* Assurez-vous que le main n'a pas un padding excessif */
  padding: 0;
}
</style>