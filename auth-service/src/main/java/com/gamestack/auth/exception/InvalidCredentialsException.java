package com.gamestack.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception lancée pour les erreurs de connexion (mot de passe invalide, compte inactif, etc.).
 * Le statut HTTP 401 UNAUTHORIZED est automatiquement retourné.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}