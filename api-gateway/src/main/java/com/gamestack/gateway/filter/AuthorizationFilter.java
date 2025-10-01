package com.gamestack.gateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthorizationFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil; // Notre utilitaire pour gérer les tokens

    // Liste des chemins qui ne nécessitent pas de JWT (publics)
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/validate"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();


        // 1. GESTION DES ENDPOINTS PUBLICS
        if (OPEN_API_ENDPOINTS.stream().anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        // 2. EXTRACTION ET VALIDATION DU TOKEN CLIENT
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        final String userToken = authHeader.substring(7);
        Claims userClaims;
        try {
            userClaims = jwtUtil.validateToken(userToken);
        } catch (Exception e) {
            return this.onError(exchange, "Unauthorized access: Token is invalid or expired", HttpStatus.UNAUTHORIZED);
        }

        if (userClaims == null) {
            return this.onError(exchange, "Unauthorized access: Token validation failed", HttpStatus.UNAUTHORIZED);
        }

        // 3. GÉNÉRATION DU TOKEN INTERNE
        String internalToken = jwtUtil.generateInternalToken(userClaims);

        // 4. MODIFICATION ROBUSTE DE LA REQUÊTE
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();

        // *****************************************************************
        // CRITIQUE : RETIRER l'en-tête 'Authorization' du client pour éviter tout conflit
        // Ceci garantit que la requête vers le microservice ne contient qu'UNE SEULE valeur.
        builder.headers(httpHeaders -> httpHeaders.remove("Authorization"));
        // *****************************************************************

        // Ajouter le Token Interne
        builder.header("Authorization", "Bearer " + internalToken);

        // Ajouter l'ID de l'utilisateur comme un header séparé
        builder.header("X-User-ID", userClaims.get("userId").toString());

        ServerHttpRequest modifiedRequest = builder.build();

        // Continue la chaîne de filtres avec la requête modifiée
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
