package com.gamestack.collection.config;

import com.gamestack.collection.service.InternalJwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(InternalTokenFilter.class);

    @Autowired
    private InternalJwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HeaderConstants.AUTHORIZATION);

        // 1. VÉRIFICATION DU TOKEN (SÉCURITÉ ASSURÉE ICI)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Le token interne est absent, la requête ne vient pas de la Gateway ou est invalide.
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

        // Création d'un objet Authentication simple
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                Collections.emptyList(),
                authorities
        );

        // Assurez-vous que le contexte est établi avant de continuer la chaîne
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Log de confirmation que l'authentification est réussie
        log.debug("Contexte de sécurité établi pour: {}", SecurityContextHolder.getContext().getAuthentication().getName());

        // 3. CONTINUER LA CHAÎNE (MAINTENANT QUE LA ROUTE EST public DANS LA CONFIG DE SÉCURITÉ)
        filterChain.doFilter(request, response);
    }
}
