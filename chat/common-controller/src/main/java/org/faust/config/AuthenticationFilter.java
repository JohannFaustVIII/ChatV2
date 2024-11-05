package org.faust.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    private JwtDecoder jwtDecoder() {
        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        return jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty()) {
            filterChain.doFilter(request, response);
        }
        if (!header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
        }
        final String jwtToken = header.substring(7);
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Jwt jwt = jwtDecoder().decode(jwtToken);
            Map<String, Object> claims = jwt.getClaims();
            Converter<Jwt, Collection<GrantedAuthority>> authoritiesExtractor = new GrantedAuthoritiesExtractor();
            AuthUser user = new AuthUser(
                    (String) claims.get("preferred_username"),
                    UUID.fromString((String) claims.get("sub")),
                    authoritiesExtractor.convert(jwt)
            );
            SecurityContextHolder.getContext().setAuthentication(user);
        }
        filterChain.doFilter(request, response);;
    }
}
