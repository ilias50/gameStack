package com.gamestack.games.service;

import com.gamestack.games.dto.RawgResponse;
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
     * @param builder le constructeur WebClient fourni par Spring.
     * @param gameRepository le repository pour les opérations de base de données.
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
}
