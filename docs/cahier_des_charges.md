# 📝 Projet : Application de Gestion de Collections Évolutives (Focus Jeux Vidéo)

## 1. Définition du Projet et du Cahier des Charges

Le projet est une **application web modulaire et évolutive** conçue pour organiser des collections d'objets. Le **Produit Minimum Viable (MVP)** se concentre sur les **jeux vidéo**, avec une architecture pensée pour intégrer facilement d'autres types de collections (films, livres, etc.).

### Fonctionnalités Clés (MVP)

* **Authentification & Profil** : Inscription, connexion sécurisée, et gestion du profil.
* **Recherche & Catalogue** : Consultation du catalogue de jeux via l'API externe **RAWG**.
* **Gestion de Collection Personnelle** :
    * CRUD (Ajout, Modification, Suppression) des jeux.
    * Attributs personnalisés : **Plateforme** et **Statut** (*Pas joué, En cours, Fini*).
    * Filtrage et tri basés sur ces attributs.

---

## 2. Architecture et Choix Techniques

Nous adoptons une **architecture en microservices** basée sur une pile technologique moderne (Spring Boot, Vue.js, MySQL).

### Architecture des Microservices

| Service | Rôle Principal | Technologies Clés |
| :--- | :--- | :--- |
| **`api-gateway`** | **Point d'entrée unique** ; Routage, sécurité préliminaire (validation JWT) et identification interne. | **Spring Cloud Gateway** |
| **`auth-service`** | Gestion de l'authentification (Inscription, connexion, validation **JWT**). | **Java Spring Boot**, Bcrypt |
| **`collection-service`** | Gestion dédiée de la collection personnelle (`user_games`). | **Java Spring Boot** |
| **`games-service`** | **Service proxy** pour l'API RAWG, gestion du cache (mémoire et persistant). | **Java Spring Boot**, Caffeine |

#### Sécurité Interne
L'API Gateway utilise un **Token Interne de Service** (via un en-tête `X-Gateway-Auth`) pour s'authentifier auprès des autres microservices, empêchant ainsi les appels directs (modèle **BFF**).

### Pile Technologique

* **Back-end** : **Java Spring Boot** (avec Maven) pour des microservices robustes.
* **Front-end** : **Vue.js** pour une interface utilisateur dynamique et réactive.
* **Base de données** : **MySQL** (instance unique partagée pour ce projet).
* **Gestion des Schémas** : **Flyway** pour des migrations de base de données fiables et versionnées.

### Schéma de la Base de Données

Le schéma est évolutif et optimisé :

* **`users`** : Informations utilisateur (`auth-service`).
* **`games`** : **Cache persistant** des données externes (`rawg_id`) pour le `games-service`.
* **`user_games`** : Table de liaison pour la collection et ses métadonnées (statut, notes) (`collection-service`).

### Stratégie de Cache (`games-service`)
Le `games-service` minimise les appels RAWG grâce à un cache à deux niveaux :
1.  **Niveau 1 (Mémoire)** : **Caffeine** pour les données fréquemment consultées (accès quasi-instantanés).
2.  **Niveau 2 (Persistant)** : Table **`games`** en base de données pour la persistance des données après redémarrage.

---

## 3. Déploiement et CI/CD

* **Conteneurisation** : Chaque composant (services + MySQL) est **Dockerisé**.
* **Orchestration Locale** : Utilisation de **`docker-compose`** pour lancer l'environnement complet.
* **Pipeline CI/CD** : **GitHub Actions** pour automatiser le *build*, le test et la publication des images Docker.
* **Gestion des Versions** : **Release-please** pour l'automatisation des releases et la génération des notes de version.

---

## 4. Gestion de Projet et Sécurité

### Gestion de Projet
Le développement suivra une **méthodologie agile**, en utilisant **GitHub Projects** pour l'organisation du travail en sprints et la priorisation du MVP.

### Sécurité

* **Authentification** : Mots de passe hachés avec **Bcrypt** et usage des **JWT** pour les sessions.
* **Gestion des Secrets** : Les informations sensibles (clés API) sont gérées via **variables d'environnement** et non codées en dur.
* **Sécurité Inter-Services** : Le **Token Interne de Service** dans l'API Gateway garantit qu'aucun client ne peut appeler directement les microservices internes.