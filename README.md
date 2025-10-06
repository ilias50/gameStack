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