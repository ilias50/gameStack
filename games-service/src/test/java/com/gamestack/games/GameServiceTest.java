package com.gamestack.games;

import com.gamestack.games.dto.RawgResponse;
// Le repository local n'est plus utilisé par GameService : les tests ont été ajustés en conséquence
import com.gamestack.games.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings; // Importation pour la strictezza
import org.mockito.quality.Strictness; // Importation pour la strictezza
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Permet de tolérer les stubbings inutiles. Cela corrige UnnecessaryStubbingException.
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    // Placeholder class for the RAWG API game result DTO
    public static class RawgGameResult {
        // Supposons qu'il a au moins un constructeur par défaut
    }

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
        // C'est ici que l'appel webClientMock.get() est simulé,
        // ce qui résout la NullPointerException du test shouldReturnMonoEmptyWhenGameIsFoundLocally
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        // Utilisation de any(Function.class) pour résoudre l'ambiguïté de WebClient.uri(any())
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(RawgResponse.class)).thenReturn(Mono.just(response));
    }


    // ----------------------------------------------------------------------
    // SCÉNARIO 1 : Appel API RAWG réussi (le service interroge toujours l'API)
    // ----------------------------------------------------------------------
    @Test
    void shouldReturnRawgResponseWhenApiSucceeds() {
    // Préparation de la réponse de l'API RAWG
    RawgResponse apiResponse = new RawgResponse();
    ReflectionTestUtils.setField(apiResponse, "results", Collections.singletonList(new RawgGameResult()));

    // Configurer le WebClient pour renvoyer la réponse RAWG simulée
    mockWebClientSuccess(apiResponse);

    // Exécution
    Mono<RawgResponse> resultMono = gameService.searchGames(TEST_QUERY);

    // Vérification
    RawgResponse result = resultMono.block();
    List<?> resultsList = (List<?>) ReflectionTestUtils.getField(result, "results");
    assertTrue(resultsList != null && resultsList.size() == 1,
        "Le Mono doit contenir la RawgResponse avec 1 résultat après l'appel API réussi.");

    // S'assurer que le WebClient a bien été appelé (une fois)
    verify(webClientMock, times(1)).get();
    }

    // (Test supprimé) Le service n'utilise plus le repository local — les scénarios sont testés via WebClient

    // ----------------------------------------------------------------------
    // SCÉNARIO 2 : Appel RAWG échoue (Erreur API)
    // ----------------------------------------------------------------------
    @Test
    void shouldPropagateExceptionWhenRawgApiFails() {
        // Configurer le WebClient pour lancer une exception (simulant une erreur HTTP 5xx ou Timeout)
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        // La chaîne de simulation se termine par une erreur
        when(responseSpecMock.bodyToMono(RawgResponse.class)).thenReturn(Mono.error(new RuntimeException("API Timeout")));

        // Vérification
        assertThrows(RuntimeException.class, () -> {
            gameService.searchGames(TEST_QUERY).block(); // block() lance l'exception de Mono
        });

        // S'assurer que le WebClient a bien été appelé
        verify(webClientMock, times(1)).get();
    }
}
