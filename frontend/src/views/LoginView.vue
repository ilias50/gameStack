<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import LoginForm from '@/components/LoginForm.vue';
import AuthService from '@/services/authService';

const router = useRouter();
const errorMessage = ref(null);
const isLoading = ref(false);

/**
 * Gère l'événement de soumission du formulaire de connexion.
 * @param {object} credentials - Contient { username, password }
 */
const handleLogin = async ({ username, password }) => {
  // 🟢 CORRECTION APPLIQUÉE : La variable 'email' est remplacée par 'username'.
  // Assurez-vous que le composant LoginForm émet maintenant { username, password }.

  errorMessage.value = null;
  isLoading.value = true;

  try {
    // Appel du service de connexion avec le nom d'utilisateur
    await AuthService.login(username, password);

    // Redirection après succès (vers le nom de route 'library')
    router.push({ name: 'library' });

  } catch (error) {
    // La gestion des erreurs utilise maintenant "Nom d'utilisateur" au lieu de "Email"
    if (error.status === 401 || error.message.includes('invalides')) {
      errorMessage.value = 'Nom d\'utilisateur ou mot de passe incorrect.';
    } else {
      errorMessage.value = 'Une erreur inattendue est survenue. Veuillez réessayer.';
      console.error('Erreur de connexion:', error);
    }
  } finally {
    isLoading.value = false;
  }
};
</script>


<template>
  <div class="login-container">
    <h1>Connexion à votre application</h1>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <LoginForm
        @submit="handleLogin"
        :is-loading="isLoading"
    />

    <p class="hint">Utilisez : <strong>testuser</strong> / <strong>password123</strong></p>
  </div>
</template>

<style scoped>
/* Styles inchangés */
.login-container {
  text-align: center;
  padding: 50px 0;
}
.error-message {
  color: #d9534f; /* Couleur rouge */
  background-color: #f2dede;
  border: 1px solid #ebccd1;
  padding: 10px;
  border-radius: 4px;
  max-width: 400px;
  margin: 15px auto;
}
.hint {
  margin-top: 20px;
  font-size: 0.9em;
  color: #6c757d;
}
</style>