package com.gamestack.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final InternalTokenFilter internalTokenFilter;

    // Injection du filtre personnalisé
    public SecurityConfig(InternalTokenFilter internalKeyFilter) {
        this.internalTokenFilter = internalKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Désactiver CSRF et HTTP Basic (inutiles pour une API stateless/JWT)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 1. Configurer les règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        // 🟢 EXCEPTION CRITIQUE : Seules les routes de connexion/inscription sont publiques
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // TOUTES LES AUTRES routes exigent une AUTHENTIFICATION (via le filtre)
                        .anyRequest().authenticated()
                )

                // 2. Ajouter notre filtre (pour les routes sécurisées après le login)
                // Ce filtre gère l'authentification des autres requêtes (ex: /validate).
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 3. Gestion de la politique de session (stateless pour JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}