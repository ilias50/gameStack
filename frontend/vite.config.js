import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    // Redirige toutes les requêtes commençant par /api vers la Gateway
    proxy: {
      '/api': {
        // ⚠️ Assurez-vous que cette adresse est correcte pour votre Gateway lancée en local
        target: 'http://localhost:8083',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
        // La réécriture dépend de si votre Gateway s'attend à recevoir /api ou non.
        // Souvent, on la garde simple:
        // target: 'http://localhost:8083/api'
      }
    }
  }
})


