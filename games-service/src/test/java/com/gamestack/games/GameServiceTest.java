package com.gamestack.games;

import com.gamestack.games.dto.RawgResponse;
import com.gamestack.games.model.Game;
import com.gamestack.games.repository.GameRepository;
import com.gamestack.games.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Function; // FIX: Ajouté pour résoudre l'ambiguïté de WebClient.uri(any())

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    // FIX: Classe Placeholder pour le DTO de résultat de jeu de l'API RAWG
    // Remplacez cette classe par votre DTO réel si elle est déjà définie dans votre code.
    public static class RawgGameResult {
        // Supposons qu'il a au moins un constructeur par défaut
    }

    @Mock
    private GameRepository gameRepository;

    @Spy
    private WebClient.Builder webClientBuilder = WebClient.builder();

    // Mocks pour simuler les étapes de l'appel WebClient
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    // Le service que nous testons
    @InjectMocks
    private GameService gameService;

    // Données de base
    private final String TEST_QUERY = "Elden Ring";
    private final String RAWG_API_URL = "https://api.rawg.io/api";
    private final String RAWG_API_KEY = "test_key";

    @BeforeEach
    void setUp() {
        // 1. Injecte manuellement les valeurs (@Value)
        ReflectionTestUtils.setField(gameService, "rawgApiUrl", RAWG_API_URL);
        ReflectionTestUtils.setField(gameService, "rawgApiKey", RAWG_API_KEY);

        // 2. Simule la construction du WebClient réel dans la méthode init()
        when(webClientBuilder.baseUrl(RAWG_API_URL)).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClientMock);

        // 3. Appelle manuellement init() pour initialiser le WebClient dans le service
        gameService.init();
    }

    /** Simule la chaîne d'appel WebClient réussie */
    private void mockWebClientSuccess(RawgResponse response) {
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        // FIX: Utilisation de any(Function.class) pour résoudre l'ambiguïté
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(RawgResponse.class)).thenReturn(Mono.just(response));
    }


    // ----------------------------------------------------------------------
    // SCÉNARIO 1 : Jeu trouvé dans la base de données locale (Cache Hit)
    // ----------------------------------------------------------------------
    @Test
    void shouldReturnMonoEmptyWhenGameIsFoundLocally() {
        // Préparation du jeu local
        Game localGame = new Game();
        localGame.setTitle(TEST_QUERY);

        // 1. Configurer le Mock du Repository pour renvoyer le jeu local
        when(gameRepository.findByTitleContainingIgnoreCase(TEST_QUERY))
                .thenReturn(Collections.singletonList(localGame));

        // Exécution
        Mono<RawgResponse> resultMono = gameService.searchGames(TEST_QUERY);

        // Vérification
        assertTrue(resultMono.blockOptional().isEmpty(),
                "Le Mono devrait être vide quand le jeu est trouvé localement.");

        // 2. S'assurer que le WebClient n'a jamais été appelé
        verify(webClientMock, never()).get();
        verify(gameRepository, times(1)).findByTitleContainingIgnoreCase(TEST_QUERY);
    }

    // ----------------------------------------------------------------------
    // SCÉNARIO 2 : Jeu non trouvé localement et appel RAWG réussi (Cache Miss)
    // ----------------------------------------------------------------------
    @Test
    void shouldReturnRawgResponseWhenGameIsNotFoundLocallyAndApiSucceeds() {
        // Préparation de la réponse de l'API RAWG
        RawgResponse apiResponse = new RawgResponse();
        // FIX: Utilisation du DTO placeholder et hypothèse que setResults existe
        // Note: Vous devrez vous assurer que RawgResponse a bien une méthode setResults(List<? extends RawgGameResult>)
        ReflectionTestUtils.setField(apiResponse, "results", Collections.singletonList(new RawgGameResult()));

        // 1. Configurer le Mock du Repository pour renvoyer une liste vide
        when(gameRepository.findByTitleContainingIgnoreCase(TEST_QUERY))
                .thenReturn(Collections.emptyList());

        // 2. Configurer le WebClient pour renvoyer la réponse RAWG simulée
        mockWebClientSuccess(apiResponse);

        // Exécution
        Mono<RawgResponse> resultMono = gameService.searchGames(TEST_QUERY);

        // Vérification
        RawgResponse result = resultMono.block();
        // FIX: Vérification par la taille de la liste, car getCount causait une erreur de résolution
        List<?> resultsList = (List<?>) ReflectionTestUtils.getField(result, "results");
        assertTrue(resultsList != null && resultsList.size() == 1,
                "Le Mono doit contenir la RawgResponse avec 1 résultat après l'appel API réussi.");

        // 2. S'assurer que le WebClient a bien été appelé (une fois)
        verify(webClientMock, times(1)).get();
        verify(gameRepository, times(1)).findByTitleContainingIgnoreCase(TEST_QUERY);
    }

    // ----------------------------------------------------------------------
    // SCÉNARIO 3 : Jeu non trouvé localement et appel RAWG échoue (Erreur API)
    // ----------------------------------------------------------------------
    @Test
    void shouldPropagateExceptionWhenRawgApiFails() {
        // 1. Configurer le Repository pour renvoyer vide
        when(gameRepository.findByTitleContainingIgnoreCase(TEST_QUERY))
                .thenReturn(Collections.emptyList());

        // 2. Configurer le WebClient pour lancer une exception (simulant une erreur HTTP 5xx ou Timeout)
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock); // FIX: Ajout de Function.class
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(RawgResponse.class)).thenReturn(Mono.error(new RuntimeException("API Timeout")));

        // Vérification
        assertThrows(RuntimeException.class, () -> {
            gameService.searchGames(TEST_QUERY).block(); // block() lance l'exception de Mono
        });

        // 2. S'assurer que le WebClient a bien été appelé
        verify(webClientMock, times(1)).get();
    }
}
