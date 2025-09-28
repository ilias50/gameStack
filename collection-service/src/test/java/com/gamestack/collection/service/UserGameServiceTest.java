package com.gamestack.collection.service;

import com.gamestack.collection.dto.UserGameResponseDto;
import com.gamestack.collection.model.Game;
import com.gamestack.collection.model.UserGame;
import com.gamestack.collection.repository.UserGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// ... imports

// 🚨 Vous devrez créer une classe Game simple dans votre dossier de tests si elle n'est pas accessible
// (ou simplement utiliser l'entité Game réelle si elle a un constructeur par défaut et des setters)
class FakeGame {
    private Long id;
    private String title;
    // ... autres champs nécessaires au DTO

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... Ajoutez les getters/setters nécessaires pour que le Service fonctionne
}
// Assurez-vous que l'entité Game réelle soit bien utilisée dans le code final.


@ExtendWith(MockitoExtension.class)
class UserGameServiceTest {

    @Mock
    private UserGameRepository userGameRepository;

    @InjectMocks
    private UserGameService userGameService;

    // ... (Test Case 1 reste identique)

    // ----------------------------------------------------------------------
    // Test Case 2: Quand l'utilisateur a des jeux (CORRIGÉ POUR LA RELATION)
    // ----------------------------------------------------------------------
    @Test
    void shouldReturnMappedDtosWhenUserHasGames() {
        Long testUserId = 2L;

        // 1. Créer le faux objet Game qui serait récupéré par la jointure
        Game fakeGame1 = new Game(); // Supposons un constructeur par défaut
        fakeGame1.setId(59184L);
        fakeGame1.setTitle("Kingdom Hearts"); // Le titre que l'on veut tester
        // ... setImagePath etc.

        // 2. Créer l'entité UserGame et l'associer au Game
        UserGame fakeUserGame1 = new UserGame();
        fakeUserGame1.setId(1L);
        fakeUserGame1.setUserId(testUserId);
        fakeUserGame1.setGame(fakeGame1); // ⬅️ LIAISON CRITIQUE
        fakeUserGame1.setActive(true);

        // --- Deuxième jeu ---
        Game fakeGame2 = new Game();
        fakeGame2.setId(3498L);
        fakeGame2.setTitle("GTA V");

        UserGame fakeUserGame2 = new UserGame();
        fakeUserGame2.setId(2L);
        fakeUserGame2.setUserId(testUserId);
        fakeUserGame2.setGame(fakeGame2); // ⬅️ LIAISON CRITIQUE
        fakeUserGame2.setActive(true);

        List<UserGame> fakeEntities = Arrays.asList(fakeUserGame1, fakeUserGame2);

        // 3. Stubbing: Le Repository renvoie ces faux objets
        when(userGameRepository.findByUserId(testUserId)).thenReturn(fakeEntities);

        // Exécution
        List<UserGameResponseDto> result = userGameService.getUserGames(testUserId);

        // 4. Vérification (Assert)
        assertEquals(2, result.size());
        assertEquals("Kingdom Hearts", result.get(0).getTitle());

        verify(userGameRepository, times(1)).findByUserId(testUserId);
    }

    // Dans UserGameServiceTest.java
    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        Long testUserId = 3L;

        // Préparation : Configurer le mock pour lancer une RuntimeException
        when(userGameRepository.findByUserId(testUserId)).thenThrow(new RuntimeException("DB access error"));

        // Vérification : S'assurer que la méthode du service lance la même exception
        assertThrows(RuntimeException.class, () -> {
            userGameService.getUserGames(testUserId);
        });

        // S'assurer que le repository a été appelé
        verify(userGameRepository, times(1)).findByUserId(testUserId);
    }
}