<script setup>
import { ref, defineEmits, defineProps } from 'vue';

const email = ref('');
const password = ref('');

// 1. Déclarer la prop 'isLoading' que nous recevons du parent
const props = defineProps({
  isLoading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['submit']);

const handleSubmit = () => {
  // 2. Vérifier si les champs sont remplis et si l'application n'est PAS en chargement
  // (Bien que le bouton soit désactivé, c'est une bonne double vérification)
  if (email.value && password.value && !props.isLoading) {

    // 3. Émettre l'événement 'submit' avec les données
    // Le parent (LoginView) gérera le changement d'état de chargement à true/false.
    emit('submit', {
      email: email.value,
      password: password.value
    });
  }
};
</script>

<template>
  <form @submit.prevent="handleSubmit" class="login-form">

    <div class="form-group">
      <label for="email">Email</label>
      <input
          id="email"
          type="email"
          v-model="email"
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