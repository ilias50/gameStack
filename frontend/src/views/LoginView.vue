<script setup>
import { ref } from 'vue';
import { useRouter, RouterLink } from 'vue-router'; // 👈 Import de RouterLink
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
  errorMessage.value = null;
  isLoading.value = true;

  try {
    await AuthService.login(username, password);
    router.push({ name: 'library' });

  } catch (error) {
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

    <p class="register-link">
      Pas encore de compte ?
      <RouterLink to="/register">S'inscrire ici</RouterLink>
    </p>
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
.register-link {
  margin-top: 15px;
}
</style>