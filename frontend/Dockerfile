# Étape 1 : Construire l'application Vue
FROM node:20-alpine AS build

# Définir le répertoire de travail dans le conteneur
WORKDIR /app


# Copier les fichiers package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier le reste des fichiers de l'application
COPY . .

# Construire l'application pour la production
RUN npm run build

# Étape 2 : Servir l'application avec Nginx
FROM nginx:stable-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers de la construction depuis l'étape 1 dans le répertoire de Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Exposer le port par défaut de Nginx
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
