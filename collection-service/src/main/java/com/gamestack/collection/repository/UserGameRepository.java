package com.gamestack.collection.repository;

import com.gamestack.collection.model.Game;
import com.gamestack.collection.model.Platform;
import com.gamestack.collection.model.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Assurez-vous que cette importation est présente

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGameRepository extends JpaRepository<UserGame, Long> {

    /**
     * Récupère tous les jeux de la collection pour un utilisateur donné.
     * @param userId L'ID de l'utilisateur.
     * @return Liste des entités UserGame.
     */
    List<UserGame> findByUserId(Long userId);

    /**
     * Supprime une entrée de jeu spécifique pour un utilisateur.
     * Utilise la convention de nommage pour traverser les relations (game.id).
     * @param userId L'ID de l'utilisateur.
     * @param gameId L'ID du jeu.
     */
    @Modifying
    @Transactional
    void deleteByUserIdAndGameId(Long userId, Long gameId);

    /**
     * Vérifie si l'association utilisateur/jeu/plateforme existe déjà pour éviter les doublons.
     * Utilise Spring Data JPA pour rechercher par l'objet Game et l'objet Platform.
     * @param userId L'ID de l'utilisateur.
     * @param game L'entité Game.
     * @param platform L'entité Platform.
     * @return L'entité UserGame si elle existe.
     */
    Optional<UserGame> findByUserIdAndGameAndPlatform(Long userId, Game game, Platform platform);
}
