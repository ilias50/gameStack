package com.gamestack.auth.config;


import com.gamestack.auth.service.InternalJwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    @Autowired
    private InternalJwtUtil jwtUtil;

    // 🟢 AJOUT CRITIQUE : Cette méthode définit les chemins que le filtre DOIT ignorer.
    // Cela permet aux routes .permitAll() de fonctionner sans être bloquées par le filtre.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        final String path = request.getRequestURI();

        // Assurez-vous que ces chemins correspondent exactement à ceux définis dans SecurityConfig !
        // J'utilise 'contains' pour la robustesse, mais 'equals' est plus précis si vous n'utilisez pas de chemins dynamiques.

    // Nous excluons /register, /login et /validate (ces endpoints utilisent le token CLIENT)
    return path.contains("/api/auth/register") || path.contains("/api/auth/login") || path.contains("/api/auth/validate");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. VÉRIFICATION DU TOKEN (SÉCURITÉ ASSURÉE ICI)
        // Ce bloc est maintenant seulement exécuté si shouldNotFilter retourne false.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Le token interne est absent et la route n'est pas publique. Blocage.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            return;
        }

        final String internalToken = authHeader.substring(7);
        Claims claims = jwtUtil.validateInternalToken(internalToken);

        if (claims == null) {
            // Token invalide ou signature incorrecte/expirée
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            return;
        }

        // 2. AUTHENTIFICATION SPRING SECURITY (POUR LES BÉNÉFICES DU CONTEXTE)
        String username = claims.getSubject();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                Collections.emptyList(),
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("--- Games Service : Contexte de sécurité établi pour: " + SecurityContextHolder.getContext().getAuthentication().getName());

        // 3. CONTINUER LA CHAÎNE
        filterChain.doFilter(request, response);
    }
}