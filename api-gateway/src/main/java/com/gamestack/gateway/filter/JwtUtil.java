package com.gamestack.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Clés pour la validation et la signature des deux types de tokens
    @Value("${jwt.user.secret-key}")
    private String userSecretKey;

    @Value("${jwt.internal.secret-key}")
    private String internalSecretKey;

    @Value("${jwt.internal.expiration}")
    private long internalExpiration;

    // --- LOGIQUE D'EXTRACTION DE CLÉ ---

    // Clé pour valider les tokens utilisateurs (signés par l'Auth Service)
    private Key getUserSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(userSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Clé pour signer les tokens internes (signés par la Gateway)
    private Key getInternalSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(internalSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- LOGIQUE DE VALIDATION DU TOKEN CLIENT ---

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getUserSignInKey()) // Utilise la clé UTILISATEUR
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Loggez l'erreur pour le débogage, mais rejetez le token
            System.err.println("Validation du token client échouée: " + e.getMessage());
            return null; // Retourne null si le token est invalide (expiré, signature incorrecte, etc.)
        }
    }

    // --- LOGIQUE DE GÉNÉRATION DU TOKEN INTERNE ---

    public String generateInternalToken(Claims userClaims) {

        // On ne copie que les claims de sécurité critiques du token client
        Map<String, Object> internalClaims = new HashMap<>();
        internalClaims.put("userId", userClaims.get("userId"));
        internalClaims.put("username", userClaims.getSubject());
        internalClaims.put("token_type", "internal_access"); // Le type CRITIQUE

        return Jwts.builder()
                .setClaims(internalClaims)
                .setSubject(userClaims.getSubject())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + internalExpiration))
                .signWith(getInternalSignInKey(), SignatureAlgorithm.HS256) // Signé avec la clé INTERNE
                .compact();
    }
}