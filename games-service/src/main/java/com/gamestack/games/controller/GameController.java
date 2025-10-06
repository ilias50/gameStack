package com.gamestack.games.controller;


import com.gamestack.games.dto.RawgGameDetailsResponse;
import com.gamestack.games.dto.RawgScreenshotResponse;
import com.gamestack.games.dto.RawgTrailerResponse;
import com.gamestack.games.model.Game;
import com.gamestack.games.service.GameService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Contrôleur REST pour la gestion des requêtes liées aux jeux.
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    /**
     * Constructeur pour l'injection de dépendances.
     * @param gameService le service de jeu à utiliser.
     */
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Point d'entrée pour la recherche de jeux.
     * @param search la chaîne de caractères à rechercher.
     * @return un Mono contenant une liste de jeux correspondants.
     */
    @GetMapping
    public Mono<List<Game>> searchGames(@RequestParam String search) {
        return gameService.searchGames(search)
                .map(rawgResponse -> rawgResponse.getResults().stream()
                        .map(rawgGameResult -> {
                            Game game = new Game();
                            game.setTitle(rawgGameResult.getName());
                            game.setApiId(rawgGameResult.getId());
                            game.setImagePath(rawgGameResult.getBackgroundImage());
                            game.setReleaseDate(rawgGameResult.getReleased());
                            return game;
                        })
                        .collect(Collectors.toList()));
    }

    /**
     * Récupère les détails complets d'un jeu par son ID.
     * Le chemin est /api/games/{gameId}/details
     * @param gameId l'ID du jeu dans l'API RAWG.
     * @return un ResponseEntity contenant les détails du jeu (avec Cache-Control).
     */
    @GetMapping("/{gameId}/details")
    public Mono<ResponseEntity<RawgGameDetailsResponse>> getGameDetails(@PathVariable long gameId) {
        System.out.println("getGameDetails()");
        return gameService.getGameDetails(gameId)
                .map(details -> ResponseEntity.ok()
                        // Ajout d'un Cache-Control pour que le client (navigateur) puisse mettre en cache la réponse
                        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).noTransform().sMaxAge(1, TimeUnit.HOURS))
                        .body(details));
    }

    /**
     * Récupère les bandes-annonces d'un jeu par son ID.
     * Le chemin est /api/games/{gameId}/trailers
     * @param gameId l'ID du jeu dans l'API RAWG.
     * @return un ResponseEntity contenant les bandes-annonces (avec Cache-Control).
     */
    @GetMapping("/{gameId}/trailers")
    public Mono<ResponseEntity<RawgTrailerResponse>> getGameTrailers(@PathVariable long gameId) {
        System.out.println("getGameTrailers");
        return gameService.getGameTrailers(gameId)
                .map(trailers -> ResponseEntity.ok()
                        // Les trailers changent rarement, on peut mettre un cache long
                        .cacheControl(CacheControl.maxAge(3, TimeUnit.HOURS).noTransform().sMaxAge(3, TimeUnit.HOURS))
                        .body(trailers));
    }

    /**
     * Récupère les captures d'écran d'un jeu par son ID.
     * Le chemin est /api/games/{gameId}/screenshots
     * @param gameId l'ID du jeu dans l'API RAWG.
     * @return un ResponseEntity contenant les captures d'écran (avec Cache-Control).
     */
    @GetMapping("/{gameId}/screenshots")
    public Mono<ResponseEntity<RawgScreenshotResponse>> getGameScreenshots(@PathVariable long gameId) {
        System.out.println("getGameScreenshots");
        return gameService.getGameScreenshots(gameId)
                .map(screenshots -> ResponseEntity.ok()
                        // Les screenshots ne changent pas après la sortie du jeu, cache long.
                        .cacheControl(CacheControl.maxAge(3, TimeUnit.HOURS).noTransform().sMaxAge(3, TimeUnit.HOURS))
                        .body(screenshots));
    }


}

