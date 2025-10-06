// src/main.js (ou main.ts)
import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // <-- Importez le routeur


const app = createApp(App);

app.use(router); // <-- Utilisez-le
app.mount('#app');