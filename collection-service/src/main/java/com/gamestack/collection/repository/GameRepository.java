package com.gamestack.collection.repository;

import com.gamestack.collection.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    /**
     * Recherche un jeu par son identifiant d'API.
     * @param apiId L'identifiant de l'API.
     * @return Un Optional contenant le Game si trouvé.
     */
    Optional<Game> findByApiId(long apiId);
}
