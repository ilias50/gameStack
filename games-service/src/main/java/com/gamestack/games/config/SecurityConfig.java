package com.gamestack.games.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ❌ RETRAIT des imports CORS non utilisés
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import java.util.List;
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
                // ❌ RETRAIT : Suppression de l'appel à la configuration CORS pour éviter le double en-tête
                // .cors(withDefaults())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // *** CRITIQUE : PERMET L'ACCÈS À TOUS POUR BYPASSER LE FILTRE D'AUTORISATION ***
                .authorizeHttpRequests(auth -> auth
                        // La sécurité est assurée par InternalTokenFilter.
                        .requestMatchers("/api/games/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Ajoutez toujours votre filtre en PREMIER pour la vérification du Token Interne
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ❌ RETRAIT : Suppression complète du bean CorsConfigurationSource
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ... (Ancienne configuration CORS)
    }
    */
}
