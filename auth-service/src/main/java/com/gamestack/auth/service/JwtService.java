package com.gamestack.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {

    // Clé secrète injectée depuis application.properties (pour le token utilisateur)
    @Value("${jwt.user.secret-key}") // Nouvelle propriété
    private String secretKey;

    // Durée de validité du token injectée depuis application.properties (en ms)
    @Value("${jwt.user.expiration}") // Nouvelle propriété
    private long jwtExpiration;

    /**
     * Génère un token JWT pour un utilisateur donné.
     * @param username Le nom d'utilisateur pour le champ 'sub'.
     * @param userId L'ID de l'utilisateur à inclure dans les claims.
     * @return Le token JWT généré.
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        // Ajoutez un "audience" ou un "type" pour indiquer qu'il s'agit d'un token utilisateur
        claims.put("token_type", "user_access");
        return buildToken(claims, username, jwtExpiration);
    }

    // ... (Le reste du code de buildToken, isTokenValid, extractUsername, etc. reste inchangé
    // car il utilise les variables de classe 'secretKey' et 'jwtExpiration' qui sont maintenant bien injectées.)

    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration
    ) {
        // La méthode Jwts.builder() est dépréciée mais devrait fonctionner.
        // Pour être 100% sûr, vous pouvez simplement l'utiliser sans changement
        // et ignorer l'avertissement 'deprecated', car elle compile.

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Remplacez la méthode extractAllClaims (ligne ~80) par ceci :
    private Claims extractAllClaims(String token) {
        // Cette syntaxe utilise Jwts.parser() pour contourner le problème de Jwts.parserBuilder()
        // Même si Jwts.parser() est déprécié, cela devrait compiler et fonctionner.
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Token invalide : " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}