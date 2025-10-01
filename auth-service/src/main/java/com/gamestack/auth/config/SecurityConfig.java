package com.gamestack.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Nécessaire pour ajouter le filtre

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final InternalTokenFilter internalTokenFilter; // Injection du nouveau filtre

    public SecurityConfig(InternalTokenFilter internalKeyFilter) {
        this.internalTokenFilter = internalKeyFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. Configurer les règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        // Seules les routes de connexion/inscription sont publiques
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // TOUTES LES AUTRES routes, y compris /validate, exigent une AUTHENTIFICATION
                        // Cette authentification sera fournie par le InternalKeyFilter
                        .anyRequest().authenticated()
                )

                // *** CRITIQUE : AJOUT DU FILTRE DE CLÉ STATIQUE ***
                // Ce filtre vérifiera X-Internal-Secret pour les routes internes comme /validate
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
