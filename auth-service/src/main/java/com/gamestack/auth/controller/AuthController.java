package com.gamestack.auth.controller;

import com.gamestack.auth.dto.AuthResponse;
import com.gamestack.auth.dto.LoginRequest;
import com.gamestack.auth.dto.RegisterRequest;
import com.gamestack.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Point d'accès pour l'inscription.
     * @param request Les données d'inscription (username, password).
     * @return Le token d'authentification et les détails de l'utilisateur.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerUser(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Point d'accès pour la connexion.
     * @param request Les données de connexion (username, password).
     * @return Le token d'authentification et les détails de l'utilisateur.
     */
    @PostMapping("/login")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Point d'accès pour la déconnexion.
     * NOTE: L'implémentation côté serveur est souvent facultative pour les tokens JWT.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@RequestHeader(name = "Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        authService.logout(actualToken);
        return ResponseEntity.ok().build();
    }

    /**
     * Point d'accès pour la suppression de compte (désactivation logique).
     * @param userId L'ID de l'utilisateur à désactiver.
     */
    // NOTE: Dans une vraie application, cela devrait être protégé par un token d'admin ou l'utilisateur lui-même.
    @DeleteMapping("/account/{userId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long userId) {
        authService.deactivateAccount(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Point d'accès pour la validation de token (utilisé par l'API Gateway/autres services).
     * @param token Le token à valider.
     * @return Un statut HTTP basé sur la validité.
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader(name = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ") && authService.validateToken(token.replace("Bearer ", ""))) {
            return ResponseEntity.ok().build(); // Token valide
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Token invalide
    }
}