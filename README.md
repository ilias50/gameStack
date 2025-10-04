# GameStack — Guide d'exécution (français)

Ce dépôt contient une application multi-modules Java (Spring Boot) + frontend (Vite + Vue) pour un petit service de gestion de collections de jeux.

Contenu principal
- `api-gateway/` — passerelle API (Spring Cloud Gateway) qui valide le JWT utilisateur et émet un token interne.
- `auth-service/` — service d'authentification (inscription, login, validation JWT utilisateur).
- `collection-service/` — gestion des collections utilisateur (endpoints /api/collections/**).
- `games-service/` — recherche et informations sur les jeux (via RAWG ou donnée mock).
- `frontend/` — application Vite + Vue, buildé en image nginx dans `vue-service`.
- `docker-compose.yml` — orchestration locale pour lancer l'ensemble (MySQL, services backend, frontend).

Prerequis
- Docker & Docker Compose (v2+) installés.
- Java/Maven sont nécessaires uniquement si vous voulez builder localement sans Docker.

Lancer l'application localement (recommandé via Docker Compose)

1. Construire et lancer tous les services :

```powershell
cd <chemin-du-projet>
docker compose up --build -d
```

2. Vérifier les logs si nécessaire :

```powershell
docker compose logs -f api-gateway
docker compose logs -f auth-service
docker compose logs -f collection-service
```

Endpoints utiles
- Gateway (porte d'entrée) : http://localhost:8083
  - POST /api/auth/register  — inscription
  - POST /api/auth/login     — connexion (retourne JWT utilisateur)
  - GET  /api/auth/validate  — validation du JWT utilisateur
  - GET  /api/games?search=  — recherche de jeux
  - GET  /api/collections/*  — endpoints collections (déroutés vers `collection-service`)

Flux de sécurité (résumé)
- L'utilisateur s'authentifie via `auth-service` et reçoit un JWT utilisateur (claim `token_type=user_access`).
- La `api-gateway` valide ce JWT, puis génère un token interne signé (claim `token_type=internal_access`) pour appeler les services internes (collection/games).
- Les services internes ne font pas confiance directement au JWT utilisateur : ils attendent le token interne émis par la gateway.
- La gateway injecte aussi l'en-tête `X-User-Id` contenant l'ID numérique de l'utilisateur.

Contract important : en-têtes et headers
- `Authorization: Bearer <token>`
  - Sur le client → gateway : c'est le JWT utilisateur.
  - Entre gateway → services internes : c'est le token interne généré par la gateway.
- `X-User-Id: <userId>`
  - Ajouté par la gateway aux requêtes internes pour indiquer l'ID de l'utilisateur.
  - Dans le code, les services préfèrent lire l'ID depuis le `SecurityContext` (Authentication principal) ; `X-User-Id` reste supporté en secours.

Variables d'environnement critiques (définies dans `docker-compose.yml`)
- `JWT_USER_SECRET_KEY` (Base64) — clé secrète pour signer/valider les JWT utilisateurs.
- `JWT_INTERNAL_SECRET_KEY` (Base64) — clé secrète pour signer/valider les tokens internes.

Lecture sécurisée des secrets (Docker secrets)
- Le projet supporte le pattern `*_FILE` pour charger des secrets depuis des fichiers (par ex. Docker secrets). Exemple : `JWT_USER_SECRET_KEY_FILE=/run/secrets/jwt_user_key`.
- Si vous utilisez Docker secrets, mettez la valeur dans un fichier et référencez la variable `*_FILE`. Le service lit automatiquement le contenu du fichier.

Bonnes pratiques et dépannage rapide
- Si Spring Boot génère un mot de passe par défaut au démarrage : vérifiez que `AuthenticationManager`/`UserDetailsService` sont correctement déclarés dans `auth-service`.
- Erreurs 403 sur services internes : vérifiez que la `api-gateway` envoie bien un `Authorization: Bearer <internal-token>` et que `JWT_INTERNAL_SECRET_KEY` est identique (ou Base64 équivalent) dans la gateway et les services.
- Si `GET /api/games` retourne 400 : ce contrôleur exige le paramètre `search` (ex. `?search=zelda`).

Prochaines améliorations proposées
- Propager le loader `*_FILE` sur tous les services (si vous utilisez Docker secrets) — j'ai déjà ajouté cela dans `auth-service`.
- Ajouter un test d'intégration (Testcontainers) pour couvrir register→login→validate→collections.
- Ajouter un guide de déploiement et un script de build/publish d'images pour CI.

Contribuer
- Pour modifier ou exécuter localement sans Docker : entrez dans un module (par ex. `auth-service`) et utilisez `./mvnw spring-boot:run`.

Contacts
- Si tu veux que j'applique d'autres changements (propager `_FILE`, ajouter tests, mettre à jour la doc de déploiement), dis-moi lequel et je le fais.

---
Fichier généré automatiquement par l'assistant pour documenter l'exécution locale et le contrat d'API (français).
