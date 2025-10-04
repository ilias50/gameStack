package com.gamestack.auth.service;

import com.gamestack.auth.dto.AuthResponse;
import com.gamestack.auth.dto.LoginRequest;
import com.gamestack.auth.dto.RegisterRequest;
import com.gamestack.auth.exception.InvalidCredentialsException;
import com.gamestack.auth.exception.UserAlreadyExistsException;
import com.gamestack.auth.exception.UserNotFoundException;
import com.gamestack.auth.model.User;
import com.gamestack.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Classe de test pour le service d'authentification AuthService.
 * Utilise Mockito pour simuler les dépendances (UserRepository, PasswordEncoder, JwtService).
 * Maintenant configuré en mode LENIENT pour éviter les UnnecessaryStubbingException dues
 * aux stubs globaux dans setUp() non appelés dans les scénarios d'échec spécifiques.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Applique le mode LENIENT à toute la classe
class AuthServiceTest {

    // Simule les dépendances
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    // Injecte les mocks dans la classe à tester
    @InjectMocks
    private AuthService authService;

    private User testUserMock; // Utilisation d'un objet Mock de la vraie classe User
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    private final String RAW_PASSWORD = "testPassword123";
    private final String HASHED_PASSWORD = "$2a$10$hashedPassword";
    private final String GENERATED_TOKEN = "jwt.test.token";

    @BeforeEach
    void setUp() {
        // Initialisation des DTOs (Correction: Utiliser le constructeur par défaut et les setters)
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newUser");
        registerRequest.setPassword(RAW_PASSWORD);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword(RAW_PASSWORD);

        // Configuration du Mock User qui simule un utilisateur existant dans la DB
        testUserMock = mock(User.class);

        // Les stubs du User Mock qui sont utilisés par plusieurs tests
        // Suppression des appels 'lenient()' car le mode LENIENT est appliqué à la classe entière.
        when(testUserMock.getId()).thenReturn(1L);
        when(testUserMock.getUsername()).thenReturn("testuser");
        when(testUserMock.getPasswordHash()).thenReturn(HASHED_PASSWORD);
        when(testUserMock.getIsActive()).thenReturn(true);
        // Simuler les setters utilisés dans deactivateAccount
        doNothing().when(testUserMock).setIsActive(anyBoolean());

        // Configuration du comportement par défaut des mocks pour le succès.
        when(passwordEncoder.encode(anyString())).thenReturn(HASHED_PASSWORD);
        // RETRAIT DU STUB passwordEncoder.matches GLOBAL (il est ajouté localement dans login_Success)
        when(jwtService.generateToken(anyString(), anyLong())).thenReturn(GENERATED_TOKEN);
    }

    // --- Tests de la méthode register ---

    @Test
    void register_Success() {
        // GIVEN: L'utilisateur n'existe pas
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        // GIVEN: On simule l'objet User retourné après la sauvegarde avec l'ID généré (2L)
        User savedUserResult = mock(User.class);
        when(savedUserResult.getId()).thenReturn(2L);
        when(savedUserResult.getUsername()).thenReturn(registerRequest.getUsername());

        // AND: Le repository retourne cet objet lors de la sauvegarde d'un User
        when(userRepository.save(any(User.class))).thenReturn(savedUserResult);

        // WHEN
        AuthResponse response = authService.register(registerRequest);

        // THEN
        assertNotNull(response);
        assertEquals(2L, response.getUserId());
        assertEquals("newUser", response.getUsername());
        assertEquals(GENERATED_TOKEN, response.getToken());

        // Vérifie que l'encodage et la sauvegarde ont été appelés
        verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        // GIVEN: L'utilisateur existe déjà
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // WHEN / THEN
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest),
                "Doit lever UserAlreadyExistsException si le nom d'utilisateur est déjà pris.");

        // Vérifie qu'aucune sauvegarde n'a été tentée
        verify(userRepository, never()).save(any(User.class));
    }

    // --- Tests de la méthode login ---

    @Test
    void login_Success() {
        // GIVEN: L'utilisateur existe et est actif
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserMock));
        // AJOUT du stub matches LOCALEMENT
        when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(HASHED_PASSWORD))).thenReturn(true);

        // WHEN
        AuthResponse response = authService.login(loginRequest);

        // THEN
        assertNotNull(response);
        assertEquals(testUserMock.getId(), response.getUserId());
        assertEquals(GENERATED_TOKEN, response.getToken());
        verify(passwordEncoder, times(1)).matches(RAW_PASSWORD, HASHED_PASSWORD);
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // GIVEN: L'utilisateur n'est pas trouvé
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(UserNotFoundException.class, () -> authService.login(loginRequest),
                "Doit lever UserNotFoundException si le nom d'utilisateur n'existe pas.");
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // GIVEN: L'utilisateur est trouvé
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testUserMock));
        // MAIS: Le mot de passe ne correspond pas
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // WHEN / THEN
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest),
                "Doit lever InvalidCredentialsException si le mot de passe est incorrect.");
    }

    @Test
    void login_InactiveUser_ThrowsException() {
        // GIVEN: L'utilisateur est trouvé mais inactif
        User inactiveUserMock = mock(User.class);

        // Stubs locaux pour l'utilisateur inactif.
        // Seul getIsActive() est nécessaire pour déclencher l'exception InvalidCredentialsException
        // Le mode LENIENT appliqué à la classe gère les autres stubs inutiles.
        when(inactiveUserMock.getIsActive()).thenReturn(false); // <--- Inactif

        LoginRequest inactiveLogin = new LoginRequest();
        inactiveLogin.setUsername("inactive");
        inactiveLogin.setPassword(RAW_PASSWORD);

        when(userRepository.findByUsername("inactive")).thenReturn(Optional.of(inactiveUserMock));

        // WHEN / THEN
        assertThrows(InvalidCredentialsException.class, () -> authService.login(inactiveLogin),
                "Doit lever InvalidCredentialsException si le compte est inactif.");
    }

    // --- Tests de la méthode deactivateAccount ---

    @Test
    void deactivateAccount_Success() {
        // GIVEN: L'utilisateur est trouvé
        when(userRepository.findById(testUserMock.getId())).thenReturn(Optional.of(testUserMock));

        // WHEN
        authService.deactivateAccount(testUserMock.getId());

        // THEN
        // On vérifie que la méthode setIsActive(false) a été appelée sur l'objet mock
        verify(testUserMock, times(1)).setIsActive(false);
        // Vérifie que la mise à jour (sauvegarde) a été effectuée
        verify(userRepository, times(1)).save(testUserMock);
    }

    @Test
    void deactivateAccount_UserNotFound_ThrowsException() {
        // GIVEN: L'utilisateur n'est pas trouvé
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(UserNotFoundException.class, () -> authService.deactivateAccount(99L),
                "Doit lever UserNotFoundException si l'ID n'existe pas.");
    }

    // --- Tests de la méthode validateToken ---

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // GIVEN: Le service JWT confirme que le token est valide
        when(jwtService.isTokenValid(GENERATED_TOKEN)).thenReturn(true);

        // WHEN
        boolean isValid = authService.validateToken(GENERATED_TOKEN);

        // THEN
        assertTrue(isValid);
        verify(jwtService, times(1)).isTokenValid(GENERATED_TOKEN);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // GIVEN: Le service JWT confirme que le token est invalide
        when(jwtService.isTokenValid(anyString())).thenReturn(false);

        // WHEN
        boolean isValid = authService.validateToken("bad.token.signature");

        // THEN
        assertFalse(isValid);
        verify(jwtService, times(1)).isTokenValid("bad.token.signature");
    }

    // --- Test de la méthode logout (simulée) ---

    @Test
    void logout_DoesNotThrow() {
        // La méthode logout n'a qu'une logique de log simulée, on vérifie juste qu'elle ne lève pas d'exception
        assertDoesNotThrow(() -> authService.logout(GENERATED_TOKEN));
    }
}
