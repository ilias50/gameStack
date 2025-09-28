package com.gamestack.games.controller;

import com.gamestack.games.model.Game;
import com.gamestack.games.service.GameService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
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
}
