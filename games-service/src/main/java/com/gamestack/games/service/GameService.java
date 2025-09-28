package com.gamestack.games.service;

import com.gamestack.games.dto.RawgResponse;
import com.gamestack.games.model.Game;
import com.gamestack.games.repository.GameRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Optional;

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
    private final GameRepository gameRepository;
    private final WebClient.Builder webClientBuilder;

    /**
     * Constructeur pour injecter WebClient.Builder et GameRepository.
     * @param builder le constructeur WebClient fourni par Spring.
     * @param gameRepository le repository pour les opérations de base de données.
     */
    public GameService(WebClient.Builder builder, @Autowired GameRepository gameRepository) {
        this.webClientBuilder = builder;
        this.gameRepository = gameRepository;
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
        // Recherche dans la base de données locale
        Optional<Game> localGame = Optional.ofNullable(gameRepository.findByTitleContainingIgnoreCase(query).stream().findFirst().orElse(null));

        if (localGame.isPresent()) {
            // Le jeu est dans la base de données locale, on peut le traiter ici
            System.out.println("Jeu trouvé dans la base de données locale !");
            return Mono.empty();
        } else {
            // Le jeu n'est pas dans la base de données, on interroge l'API RAWG
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
}
