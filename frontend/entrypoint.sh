#!/bin/sh
# Remplacer /app/env-config.js par le chemin de service de Nginx
CONFIG_FILE=/usr/share/nginx/html/config.js

# Crée le fichier de configuration env-config.js
echo "window._env_ = {" > $CONFIG_FILE
# On utilise la variable passée au conteneur, ici BACKEND_URL
echo "  BACKEND_URL: \"$BACKEND_URL\"" >> $CONFIG_FILE
echo "};" >> $CONFIG_FILE

# Affiche le contenu généré pour le débogage (OPTIONNEL)
echo "Généré $CONFIG_FILE avec l'URL: $BACKEND_URL"

# Démarre l'application (la commande CMD)
exec "$@"