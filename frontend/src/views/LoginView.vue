<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import LoginForm from '@/components/LoginForm.vue';
import AuthService from '@/services/authService';

const router = useRouter();
const errorMessage = ref(null);
const isLoading = ref(false);


const handleLogin = async ({ email, password }) => {
  errorMessage.value = null;
  isLoading.value = true;

  try {
    await AuthService.login(email, password);

    // 🟢 CORRECTION ICI : Redirection vers le nom de route 'library'
    router.push({ name: 'library' });

  } catch (error) {
    // ... (Logique de gestion des erreurs inchangée)
    if (error.status === 401) {
      errorMessage.value = 'Email ou mot de passe incorrect.';
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

    <p class="hint">Utilisez : <strong>test@example.com</strong> / <strong>password123</strong></p>
  </div>
</template>

<style scoped>
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