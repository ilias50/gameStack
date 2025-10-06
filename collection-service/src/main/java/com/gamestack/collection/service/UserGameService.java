package com.gamestack.collection.service;

import com.gamestack.collection.dto.PlatformResponseDto;
import com.gamestack.collection.dto.UserGameResponseDto;
import com.gamestack.collection.model.Game;
import com.gamestack.collection.model.Platform;
import com.gamestack.collection.model.UserGame;
import com.gamestack.collection.repository.GameRepository;
import com.gamestack.collection.repository.PlatformRepository;
import com.gamestack.collection.repository.UserGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserGameService {

    private static final Long DEFAULT_PLATFORM_ID = 1L;

    private final UserGameRepository userGameRepository;
    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;

    @Autowired
    public UserGameService(
            UserGameRepository userGameRepository,
            GameRepository gameRepository,
            PlatformRepository platformRepository) {
        this.userGameRepository = userGameRepository;
        this.gameRepository = gameRepository;
        this.platformRepository = platformRepository;
    }

    // --- METHODES DE REQUÊTE (getUserGames inchangée) ---

    public List<UserGameResponseDto> getUserGames(Long userId) {
        List<UserGame> userGames = userGameRepository.findByUserId(userId);
        return userGames.stream()
                .map(UserGameResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Ajoute un jeu à la collection d'un utilisateur.
     */
    @Transactional
    public UserGame addGameToCollection(
            Long userId,
            Long apiId,
            String title,
            String releaseDate,
            String imagePath,
            Long platformId) { // Signature FINALISÉE : PAS DE platform (String)

        Long effectivePlatformId = (platformId != null) ? platformId : DEFAULT_PLATFORM_ID;

        // 2. Trouve ou crée l'entité Game
        Optional<Game> gameOptional = gameRepository.findByApiId(apiId);

        Game game;
        if (gameOptional.isEmpty()) {
            // Création de l'entité Game avec les détails reçus
            game = new Game();
            game.setApiId(apiId);
            game.setTitle(title);

            // Initialisation des champs non-nullables
            game.setReleaseDate(releaseDate != null ? releaseDate : "");
            game.setImagePath(imagePath != null ? imagePath : "");
            game.setPlatform(""); // Initialisation à chaîne vide (NON REÇU DU DTO)

            game = gameRepository.save(game);
        } else {
            game = gameOptional.get();
        }

        // 3. Récupère l'entité Platform (via l'ID numérique)
        Optional<Platform> platformOptional = platformRepository.findById(effectivePlatformId);
        if (platformOptional.isEmpty()) {
            throw new IllegalArgumentException("Platform with ID " + effectivePlatformId + " not found.");
        }
        Platform platformEntity = platformOptional.get();

        // 4. Vérifie l'existence dans la collection de l'utilisateur (doublon)
        Optional<UserGame> existingUserGame = userGameRepository.findByUserIdAndGameAndPlatform(userId, game, platformEntity);

        if (existingUserGame.isPresent()) {
            throw new IllegalStateException(
                    "Game " + apiId + " is already in the collection for platform ID " + effectivePlatformId
            );
        } else {
            // 5. Crée et sauvegarde l'entrée UserGame.
            UserGame userGame = new UserGame();
            userGame.setUserId(userId);
            userGame.setGame(game);
            userGame.setPlatform(platformEntity);
            return userGameRepository.save(userGame);
        }
    }

    /**
     * Supprime un jeu de la collection d'un utilisateur.
     */
    @Transactional
    public void removeGameFromCollection(Long userId, Long gameId) {
        userGameRepository.deleteByUserIdAndGameId(userId, gameId);
    }
    /**
     * Récupère toutes les entités Platform et les mappe en PlatformResponseDto.
     * * @return La liste de toutes les plateformes au format DTO.
     */
    @Transactional(readOnly = true) // Bonne pratique pour les requêtes de lecture
    public List<PlatformResponseDto> findAllPlatforms() {
        // 1. Récupérer toutes les entités Platform depuis la base de données
        List<Platform> platforms = platformRepository.findAll();

        // 2. Mapper chaque entité Platform en PlatformResponseDto
        return platforms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Méthode privée utilitaire pour mapper l'entité Platform vers le DTO.
     */
    private PlatformResponseDto convertToDto(Platform platform) {
        return new PlatformResponseDto(platform.getId(), platform.getName());
    }

    /**
     * Met à jour la plateforme d'un jeu spécifique dans la collection d'un utilisateur.
     * @param userGameId L'ID de l'entrée UserGame à modifier.
     * @param newPlatformId Le nouvel ID de la Platform.
     * @return L'entrée UserGame mise à jour.
     * @throws IllegalArgumentException Si UserGame ou la nouvelle Platform n'est pas trouvée.
     * @throws IllegalStateException Si la nouvelle combinaison userId/game/platform existe déjà (doublon).
     */
    @Transactional
    public UserGame updateUserGamePlatform(Long userGameId, Long newPlatformId) {
        // 1. Récupère l'entrée UserGame existante
        UserGame userGame = userGameRepository.findById(userGameId)
                .orElseThrow(() -> new IllegalArgumentException("UserGame with ID " + userGameId + " not found."));

        // 2. Récupère la nouvelle entité Platform
        Platform newPlatform = platformRepository.findById(newPlatformId)
                .orElseThrow(() -> new IllegalArgumentException("Platform with ID " + newPlatformId + " not found."));

        // 3. Vérifie si la nouvelle plateforme est la même que l'actuelle (pas de changement nécessaire)
        if (userGame.getPlatform().getId() == newPlatformId) {
            return userGame; // Retourne l'objet inchangé
        }

        // 4. Vérifie l'existence d'un doublon pour la NOUVELLE combinaison (userId, game, newPlatform)
        Long userId = userGame.getUserId();
        Game game = userGame.getGame();

        Optional<UserGame> existingUserGameWithNewPlatform =
                userGameRepository.findByUserIdAndGameAndPlatform(userId, game, newPlatform);

        if (existingUserGameWithNewPlatform.isPresent()) {
            throw new IllegalStateException(
                    "Cannot update: Game " + game.getApiId() + " is already in the collection for platform ID " + newPlatformId
            );
        }

        // 5. Met à jour la plateforme et sauvegarde
        userGame.setPlatform(newPlatform);
        return userGameRepository.save(userGame);
    }
}