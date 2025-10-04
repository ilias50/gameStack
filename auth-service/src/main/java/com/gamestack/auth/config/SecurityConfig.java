package com.gamestack.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final InternalTokenFilter internalTokenFilter;
    private final UserDetailsService userDetailsService;

    // Injection du filtre personnalisé et du service utilisateur
    public SecurityConfig(InternalTokenFilter internalKeyFilter, UserDetailsService userDetailsService) {
        this.internalTokenFilter = internalKeyFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Désactiver CSRF, HTTP Basic et Form Login (API stateless/JWT)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 1. Configurer les règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques (inscription, connexion et validation de token)
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/validate").permitAll()

                        // TOUTES LES AUTRES routes exigent une AUTHENTIFICATION (via le filtre)
                        .anyRequest().authenticated()
                )

                // 2. Ajouter notre filtre (pour les routes sécurisées après le login)
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 3. Gestion de la politique de session (stateless pour JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    // Expose AuthenticationManager pour éviter la création automatique d'un utilisateur en mémoire
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}