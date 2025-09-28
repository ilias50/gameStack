package com.gamestack.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Tests unitaires basés sur Spring pour vérifier que les routes sont correctement configurées.
 * Ces tests valident la structure des routes sans faire d'appels HTTP externes.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // Ajouté RANDOM_PORT pour garantir l'environnement web
@AutoConfigureWebTestClient // Ajouté pour garantir le chargement complet du contexte WebFlux/Gateway
class GatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    // Le WebTestClient n'est pas encore utilisé, mais son autoconfiguration est utile ici
    @Autowired
    private WebTestClient webTestClient;

    /**
     * Vérifie que toutes les routes nécessaires (games, collections, auth) sont bien définies
     * dans le RouteLocator et que les IDs correspondent à la configuration (games_route, collections_route, auth-service).
     */
    @Test
    void shouldDefineAllRequiredRoutes() {
        // Obtient toutes les routes définies dans le RouteLocator
        Flux<String> routeIds = routeLocator.getRoutes()
                .map(route -> route.getId());

        // Attend les IDs de route spécifiques.
        // ATTENTION : L'ordre peut être différent de celui défini dans le YAML, nous allons donc vérifier la présence.
        StepVerifier.create(routeIds.collectList())
                .assertNext(ids -> {
                    // Vérifie que la liste des IDs contient toutes les routes requises
                    assertThat(ids).contains("games_route", "collections_route", "auth-service");
                    // Et qu'il n'y a que ces trois routes définies
                    assertThat(ids).hasSize(3);
                })
                .verifyComplete();
    }

    /**
     * Vérifie spécifiquement la configuration de l'URI et du filtre de la route 'games_route'.
     */
    @Test
    void gamesRouteShouldBeConfiguredCorrectly() {
        // Filtrer pour trouver la route 'games_route'
        routeLocator.getRoutes()
                .filter(route -> "games_route".equals(route.getId()))
                .next() // Prendre le premier (et unique) résultat
                .as(StepVerifier::create)
                .expectNextMatches(route -> {
                    // 1. Vérifie l'URI cible (doit pointer vers le service de jeux via le Load Balancer)
                    boolean uriCheck = route.getUri().toString().equals("lb://GAMES-SERVICE");

                    // 2. Vérifie la présence du filtre StripPrefix=1.
                    // Le test précédent échouait ici car le filtre manquait dans le YAML.
                    boolean hasStripFilter = route.getFilters().stream()
                            .anyMatch(filter -> filter.toString().contains("StripPrefix"));

                    // Assurez-vous que l'URI est correcte ET que le filtre est présent
                    return uriCheck && hasStripFilter;

                })
                .verifyComplete();
    }

    /**
     * Test de validation simple de l'existence de la route collections.
     */
    @Test
    void collectionsRouteShouldBeDefined() {
        routeLocator.getRoutes()
                .filter(route -> "collections_route".equals(route.getId()))
                .next()
                .as(StepVerifier::create)
                .expectNextCount(1) // On s'attend à trouver la route
                .verifyComplete();
    }

    /**
     * Test de validation simple de l'existence de la route auth.
     * ATTENTION : L'ID de route est corrigé pour correspondre à 'auth-service' du YAML.
     */
    @Test
    void authRouteShouldBeDefined() {
        routeLocator.getRoutes()
                // Correction de l'ID attendu de 'auth_route' à 'auth-service'
                .filter(route -> "auth-service".equals(route.getId()))
                .next()
                .as(StepVerifier::create)
                .expectNextCount(1) // On s'attend à trouver la route
                .verifyComplete();
    }
}
