package com.gamestack.collection.controller;

import com.gamestack.collection.dto.PlatformResponseDto;
import com.gamestack.collection.dto.UserGameRequestDto;
import com.gamestack.collection.dto.UserGameResponseDto;
import com.gamestack.collection.model.UserGame;
import com.gamestack.collection.service.UserGameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid; // Ajout pour la validation du DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class UserGameController {

    private final UserGameService userGameService;
    // Suppression de GameRepository car toute la logique de persistance est dans le service

    @Autowired
    public UserGameController(UserGameService userGameService) {
        this.userGameService = userGameService;
    }

    /**
     * Ajoute un jeu à la collection d'un utilisateur.
     * Le DTO contient l'ID du jeu local et l'ID de la plateforme.
     *
     * @param requestDto DTO contenant les ID du jeu et de la plateforme.
     * @param request Requête HTTP pour obtenir le userId.
     * @return ResponseEntity avec l'objet UserGame ajouté.
     */
    @PostMapping("/games")
    public ResponseEntity<UserGame> addGameToCollection(
            // @Valid active la vérification @NotNull du DTO, empêchant le NullPointerException
            @Valid @RequestBody UserGameRequestDto requestDto,
            HttpServletRequest request) {
        try {
            // Récupère l'ID utilisateur à partir du header X-User-Id
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long userId = Long.parseLong(userIdHeader);

            // Appel au service avec l'ID local du jeu et l'ID de la plateforme
            UserGame savedGame = userGameService.addGameToCollection(
                    userId,
                    requestDto.getApiId(),
                    requestDto.getTitle(),
                    requestDto.getReleaseDate(),
                    requestDto.getImagePath(),
                    requestDto.getPlatformId() // ID numérique
            );


            return new ResponseEntity<>(savedGame, HttpStatus.CREATED);


        } catch (NumberFormatException e) {
            // Si le userId n'est pas un nombre valide
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            // Catché si le jeu ou la plateforme n'est pas trouvé
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            // Catché si le jeu est déjà présent (doublon)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            System.err.println("Error adding game to collection: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupère tous les jeux de la collection d'un utilisateur.
     *
     * @param request Requête HTTP pour obtenir le userId.
     * @return ResponseEntity avec la liste des jeux de la collection.
     */
    @GetMapping("/games")
    public ResponseEntity<List<UserGameResponseDto>> getUserCollection(HttpServletRequest request) {
        try {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long userId = Long.parseLong(userIdHeader);
            List<UserGameResponseDto> userGames = userGameService.getUserGames(userId);
            return new ResponseEntity<>(userGames, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error fetching user collection: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprime un jeu de la collection d'un utilisateur.
     *
     * @param gameId ID du jeu à supprimer.
     * @param request Requête HTTP pour obtenir le userId.
     * @return ResponseEntity avec un statut OK si la suppression a réussi.
     */
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<Void> removeGameFromCollection(
            @PathVariable Long gameId,
            HttpServletRequest request) {
        try {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long userId = Long.parseLong(userIdHeader);
            userGameService.removeGameFromCollection(userId, gameId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error removing game from collection: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
    * Récupère la liste complète des plateformes.
    * Le chemin est défini par @RequestMapping("/api/collections") + @GetMapping("/platforms").
    *
    * @return ResponseEntity avec la liste des DTO de plateformes.
    */
    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformResponseDto>> getAllPlatforms() {
        try {
               // Dans un cas réel, vous utiliseriez un PlatformService:
               // List<PlatformResponseDto> platforms = platformService.findAllPlatforms();

                // Ici, pour l'exemple, on suppose une méthode dans le UserGameService
                // ou un service dédié qui retourne les plateformes.
                List<PlatformResponseDto> platforms = userGameService.findAllPlatforms();

                if (platforms.isEmpty()) {
                    // Retourne 204 No Content si la liste est vide (bonne pratique pour une liste vide)
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(platforms, HttpStatus.OK);
        } catch (Exception e) {
                System.err.println("Error fetching all platforms: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}