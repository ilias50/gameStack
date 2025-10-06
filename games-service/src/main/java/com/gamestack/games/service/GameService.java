package com.gamestack.games.service;


import com.gamestack.games.dto.RawgGameDetailsResponse;
import com.gamestack.games.dto.RawgResponse;
import com.gamestack.games.dto.RawgScreenshotResponse;
import com.gamestack.games.dto.RawgTrailerResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



/**
 * Service pour interagir avec l'API RAWG et la base de données locale.
 */
@Service
public class GameService {

    @Value("${api.rawg.url}")
    private String rawgApiUrl;

    @Value("${api.rawg.key}")
    private String rawgApiKey;

    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;

    /**
     * Constructeur pour injecter WebClient.Builder et GameRepository.
     *
     * @param builder        le constructeur WebClient fourni par Spring.
     * @param GameRepository le repository pour les opérations de base de données.
     */
    public GameService(WebClient.Builder builder) {
        this.webClientBuilder = builder;
    }

    /**
     * Cette méthode s'exécute après l'injection des dépendances (@Value).
     * C'est l'endroit sûr pour construire le WebClient.
     */
    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder.baseUrl(rawgApiUrl).build();
    }

    /**
     * Recherche des jeux en interrogeant la base de données locale en premier,
     * puis l'API RAWG si le jeu n'est pas trouvé.
     *
     * @param query la chaîne de recherche pour les jeux.
     * @return un Mono contenant la réponse de l'API RAWG.
     */
    public Mono<RawgResponse> searchGames(String query) {
        // Toujours interroger l'API RAWG (ne pas renvoyer les données locales)
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", rawgApiKey)
                        .queryParam("search", query)
                        .build())
                .retrieve()
                .bodyToMono(RawgResponse.class);
    }

    /**
     * Récupère tous les détails d'un jeu spécifique par son ID.
     * GET /games/{id}
     *
     * @param gameId L'identifiant unique du jeu (Slug ou ID).
     * @return Un Mono contenant les détails complets du jeu.
     */
    public Mono<RawgGameDetailsResponse> getGameDetails(long gameId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        // Le chemin inclut maintenant l'ID du jeu
                        .path("/games/{id}")
                        .queryParam("key", rawgApiKey)
                        .build(gameId)) // .build(gameId) injecte l'ID dans le placeholder {id}
                .retrieve()
                .bodyToMono(RawgGameDetailsResponse.class);
    }

    // --- Nouvelle méthode 2 : Récupérer les bandes-annonces d'un jeu ---

    /**
     * Récupère la liste des bandes-annonces (movies) d'un jeu spécifique par son ID.
     * GET /games/{id}/movies
     *
     * @param gameId L'identifiant unique du jeu.
     * @return Un Mono contenant la réponse avec les données des bandes-annonces.
     */
    public Mono<RawgTrailerResponse> getGameTrailers(long gameId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        // Chemin spécifique pour les bandes-annonces
                        .path("/games/{id}/movies")
                        .queryParam("key", rawgApiKey)
                        .build(gameId))
                .retrieve()
                .bodyToMono(RawgTrailerResponse.class);
    }

    /**
     * Récupère la liste des captures d'écran (screenshots) d'un jeu spécifique par son ID.
     * GET /games/{id}/screenshots
     *
     * @param gameId L'identifiant unique du jeu.
     * @return Un Mono contenant la réponse avec les données des captures d'écran.
     */
    public Mono<RawgScreenshotResponse> getGameScreenshots(long gameId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        // Chemin spécifique pour les captures d'écran
                        .path("/games/{id}/screenshots")
                        .queryParam("key", rawgApiKey)
                        .build(gameId))
                .retrieve()
                .bodyToMono(RawgScreenshotResponse.class);
    }
}





