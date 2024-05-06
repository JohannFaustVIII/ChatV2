package org.faust.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter implements WebFilter {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final List<String> authorizationHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authorizationHeaders == null || authorizationHeaders.isEmpty()) {
            return chain.filter(exchange);
        }
        for (String header : authorizationHeaders) {
            if (!header.startsWith("Bearer")) {
                continue;
            }
            final String jwtToken = header.substring(7);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Jwt jwt = jwtDecoder().decode(jwtToken);
                Map<String, Object> claims = jwt.getClaims();
                Converter<Jwt, Collection<GrantedAuthority>> authoritiesExtractor = new GrantedAuthoritiesExtractor();
                UserDetails userDetails = new User((String) claims.get("preferred_username"), "", authoritiesExtractor.convert(jwt));
                AuthUser user = new AuthUser(userDetails);
                return chain.filter(exchange).contextWrite(c -> ReactiveSecurityContextHolder.withAuthentication(user));
            }
        }
        return chain.filter(exchange);
    }

    private JwtDecoder jwtDecoder() {
        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        return jwtDecoder;
    }


}
