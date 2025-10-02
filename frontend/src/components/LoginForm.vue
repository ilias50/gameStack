<script setup>
import { ref, defineEmits, defineProps } from 'vue';

// 🟢 CORRECTION 1 : Remplacer 'email' par 'username'
const username = ref('');
const password = ref('');

// Déclarer la prop 'isLoading'
const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit']);

const handleSubmit = () => {
  // 🟢 CORRECTION 2 : Utiliser 'username.value'
  if (username.value && password.value && !props.isLoading) {

    // 🟢 CORRECTION 3 : Émettre l'événement 'submit' avec la clé 'username'
    emit('submit', {
      username: username.value, // Le parent (LoginView) s'attend maintenant à cette clé
      password: password.value
    });
  }
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="login-form">

    <div class="form-group">
      <label for="username">Nom d'utilisateur</label>
      <input
          id="username"
          type="text"
          v-model="username"
          required
          :disabled="props.isLoading"
      >
    </div>

    <div class="form-group">
      <label for="password">Mot de passe</label>
      <input
          id="password"
          type="password"
          v-model="password"
          required
          :disabled="props.isLoading"
      >
    </div>

    <button type="submit" :disabled="props.isLoading">
      <span v-if="props.isLoading">Connexion en cours...</span>
      <span v-else>Se connecter</span>
    </button>
  </form>
</template>

<style scoped>
/* Les styles sont inchangés, ils restent bons. */
/* ... (Styles précédents) ... */

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-width: 350px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
button {
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background-color: #a4d9c0;
  cursor: not-allowed;
}
</style>