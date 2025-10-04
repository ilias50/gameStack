<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import LoginForm from '@/components/LoginForm.vue';
import AuthService from '@/services/authService';

const router = useRouter();
const errorMessage = ref(null);
const isLoading = ref(false);

/**
 * Gère l'événement de soumission du formulaire d'inscription.
 * 1. Valide le mot de passe.
 * 2. Appelle AuthService.register().
 * 3. Si l'inscription réussit, appelle AuthService.login() pour s'authentifier et rediriger.
 * @param {object} credentials - Contient { username, password }
 */
const handleRegister = async ({ username, password }) => {
  errorMessage.value = null;
  isLoading.value = true;

  // 1. Validation locale de la longueur du mot de passe
  if (password.length < 8) {
    errorMessage.value = 'Le mot de passe doit contenir au moins 8 caractères.';
    isLoading.value = false;
    return;
  }

  try {
    // 2. Tente l'inscription (création du compte)
    // ⚠️ AuthService.register() doit juste appeler le backend pour créer l'utilisateur.
    await AuthService.register(username, password);

    // --- LOGIQUE DE CONNEXION APRÈS INSCRIPTION ---

    // 3. Si l'inscription est réussie (pas d'erreur levée), on appelle login()
    //    pour s'authentifier, stocker les tokens et rediriger.
    await AuthService.login(username, password);

    // 4. Redirection vers la bibliothèque (le login gère la session)
    router.push({ name: 'library' });

  } catch (error) {
    // Gestion des erreurs
    if (error.response && error.response.status === 409) {
      errorMessage.value = 'Ce nom d\'utilisateur est déjà utilisé.';
    } else if (error.message.includes('8')) {
      // Erreur de validation du backend (si le message est pertinent)
      errorMessage.value = 'Le mot de passe doit contenir au moins 8 caractères.';
    }
    else {
      // Peut aussi inclure des erreurs du login si la connexion a échoué juste après l'inscription
      errorMessage.value = 'Échec de l\'opération. Veuillez vérifier vos informations.';
      console.error('Erreur lors de l\'inscription/connexion:', error);
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="register-container">
    <h1>Créer un nouveau compte</h1>

    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

    <LoginForm
        @submit="handleRegister"
        :is-loading="isLoading"
        submit-label="S'inscrire"
        hint-message="Le mot de passe doit avoir au moins 8 caractères."
    />

    <p class="login-link">
      Déjà un compte ?
      <RouterLink to="/login">Se connecter</RouterLink>
    </p>
  </div>
</template>

<style scoped>
/* Les styles restent inchangés... */
.register-container {
  text-align: center;
  padding: 50px 0;
}
.error-message {
  color: #d9534f;
  background-color: #f2dede;
  border: 1px solid #ebccd1;
  padding: 10px;
  border-radius: 4px;
  max-width: 400px;
  margin: 15px auto;
  font-weight: bold;
}
.login-link {
  margin-top: 15px;
}
</style>