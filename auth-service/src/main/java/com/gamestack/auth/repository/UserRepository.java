package com.gamestack.auth.repository;

import com.gamestack.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur. Utilisé pour la connexion.
     * @param username Le nom d'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il existe.
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie si un nom d'utilisateur existe déjà. Utilisé pour l'inscription.
     * @param username Le nom d'utilisateur à vérifier.
     * @return true si l'utilisateur existe, false sinon.
     */
    boolean existsByUsername(String username);
}