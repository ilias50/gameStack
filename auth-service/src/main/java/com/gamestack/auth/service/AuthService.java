package com.gamestack.auth.service;

import com.gamestack.auth.dto.AuthResponse;
import com.gamestack.auth.dto.LoginRequest;
import com.gamestack.auth.dto.RegisterRequest;
import com.gamestack.auth.exception.UserAlreadyExistsException;
import com.gamestack.auth.exception.UserNotFoundException;
import com.gamestack.auth.exception.InvalidCredentialsException;
import com.gamestack.auth.model.User;
import com.gamestack.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // NOUVEAU

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) { // NOUVEAU
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService; // NOUVEAU
    }

    /**
     * Gère l'inscription d'un nouvel utilisateur.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("L'utilisateur '" + request.getUsername() + "' existe déjà.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        // Hacher le mot de passe avant de le sauvegarder
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);

        // TODO: Implémenter la génération de token JWT
        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getId());

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .build();
    }

    /**
     * Gère la connexion de l'utilisateur.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Nom d'utilisateur ou mot de passe invalide."));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Nom d'utilisateur ou mot de passe invalide.");
        }

        if (!user.getIsActive()) {
            throw new InvalidCredentialsException("Le compte utilisateur est inactif.");
        }

        // TODO: Implémenter la génération de token JWT
        String token = jwtService.generateToken(user.getUsername(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    /**
     * Supprime un compte utilisateur (désactivation logique).
     */
    public void deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * La déconnexion est généralement gérée côté client (suppression du token)
     * mais on peut ajouter une logique côté serveur si on gère une liste noire de tokens (blacklist).
     */
    public void logout(String token) {
        // Logique de déconnexion côté serveur (p. ex., ajout du token à une blacklist Redis)
        System.out.println("Déconnexion simulée pour le token: " + token);
    }

    /**
     * Valide la présence et l'authenticité d'un token JWT.
     */
    public boolean validateToken(String token) {
        // Appelle la vraie logique de validation du token
        return jwtService.isTokenValid(token);
    }
}