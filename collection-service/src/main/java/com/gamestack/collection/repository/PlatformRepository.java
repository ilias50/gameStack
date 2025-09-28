package com.gamestack.collection.repository;

import com.gamestack.collection.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer l'accès aux données de la table statique 'platforms'.
 */
@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    // Aucune méthode personnalisée n'est nécessaire ici pour l'instant,
    // car JpaRepository fournit déjà findById(), findAll(), etc.
}
