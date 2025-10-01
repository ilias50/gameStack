package com.gamestack.games.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class InternalJwtUtil {

    // Clé secrète INTERNE injectée
    @Value("${jwt.internal.secret-key}")
    private String internalSecretKey;

    private Key getInternalSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(internalSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valide le token interne généré par la Gateway.
     * @param token Le Token Interne (sans le préfixe "Bearer ").
     * @return Les Claims du token si valide, null sinon.
     */
    public Claims validateInternalToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getInternalSignInKey()) // Utilise la clé INTERNE
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Vérification de sécurité CRITIQUE : s'assurer que c'est bien un token interne
            if (!"internal_access".equals(claims.get("token_type"))) {
                System.err.println("ERREUR SÉCURITÉ : Token de type client reçu par erreur.");
                return null;
            }
            return claims;
        } catch (Exception e) {
            // Le token est invalide (expiré, signature incorrecte, etc.)
            System.err.println("Validation du token interne échouée: " + e.getMessage());
            return null;
        }
    }
}