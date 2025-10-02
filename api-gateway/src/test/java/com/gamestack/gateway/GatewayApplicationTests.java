package com.gamestack.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// NOUVEAUX IMPORTS NÉCESSAIRES
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        // La propriété 'exclude' ou 'excludeAutoConfiguration' est RETIRÉE pour éviter la compilation error
        properties = {
                "games.service.uri=lb://GAMES-SERVICE",
                "collection.service.uri=lb://COLLECTION-SERVICE",
                "auth.service.uri=lb://AUTH-SERVICE",
                "jwt.user.secret-key=4554554554545454545",
                "jwt.internal.secret-key=666666666666666666666666",
                "jwt.internal.expiration=3600000"
        }
)
// 🌟 SOLUTION FINALE : Utilise @EnableAutoConfiguration séparément pour exclure la Sécurité 🌟
@EnableAutoConfiguration(
        exclude = {ReactiveSecurityAutoConfiguration.class}
)
// Désactive Eureka (Service Discovery) pour le test
@TestPropertySource(properties = "spring.cloud.discovery.enabled=false")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class GatewayApplicationTests {
    // ... (Le reste du corps des tests est correct) ...

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;


    // --- Tests de Configuration (IDs corrigés) ---

    @Test
    void shouldDefineAllRequiredRouteIds() {
        Flux<String> routeIds = routeLocator.getRoutes().map(route -> route.getId());

        StepVerifier.create(routeIds.collectList())
                .assertNext(ids -> {
                    assertThat(ids).contains("games_route", "collections_route", "auth_route");
                    assertThat(ids).hasSize(3);
                })
                .verifyComplete();
    }

    @Test
    void authRouteShouldBeDefined() {
        routeLocator.getRoutes()
                .filter(route -> "auth_route".equals(route.getId()))
                .next()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }


    // --- Tests de Routage HTTP (Chemins corrigés) ---

    @Test
    void gamesRouteShouldBeRoutable() {
        webTestClient
                .get().uri("/api/games/details/123")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void collectionsRouteShouldBeRoutable() {
        webTestClient
                .get().uri("/api/collections/user/456")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void authRouteShouldBeRoutable() {
        webTestClient
                .post().uri("/api/auth/login")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void unknownRouteShouldReturnNotFound() {
        webTestClient
                .get().uri("/unknown/api/data")
                .exchange()
                .expectStatus().isNotFound();
    }
}