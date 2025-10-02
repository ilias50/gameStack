package com.gamestack.collection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ❌ RETRAIT : Plus besoin des imports CORS (CorsConfigurationSource, UrlBasedCorsConfigurationSource, etc.)
// ❌ RETRAIT : Plus besoin de l'import static withDefaults
// import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final InternalTokenFilter internalTokenFilter;

    public SecurityConfig(InternalTokenFilter internalTokenFilter) {
        this.internalTokenFilter = internalTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // ❌ RETRAIT : Supprimer l'appel à la configuration CORS
                // .cors(withDefaults())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // *** CRITIQUE : PERMET L'ACCÈS À TOUS POUR BYPASSER LE FILTRE D'AUTORISATION ***
                .authorizeHttpRequests(auth -> auth
                        // La sécurité est assurée par InternalTokenFilter.
                        .requestMatchers("/api/collections/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Ajoutez toujours votre filtre en PREMIER pour la vérification du Token Interne
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}