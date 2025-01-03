package org.faust.gateway.filter;

import org.faust.gateway.GrantedAuthoritiesExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestFilter implements GlobalFilter, Ordered {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    private JwtDecoder jwtDecoder() {
        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        return jwtDecoder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (headers == null || headers.isEmpty()) {
            return chain.filter(exchange);
        }
        for (String header : headers) {
            if (!header.startsWith("Bearer")) {
                continue;
            }
            final String jwtToken = header.substring(7);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Jwt jwt = jwtDecoder().decode(jwtToken);
                Map<String, Object> claims = jwt.getClaims();
                Converter<Jwt, Collection<GrantedAuthority>> authoritiesExtractor = new GrantedAuthoritiesExtractor();
                UUID userId = UUID.fromString((String) claims.get("sub"));
                String username = (String) claims.get("preferred_username");
                UUID tokenId = UUID.fromString((String) claims.get("jti"));
                System.out.println("ID = " + userId + "; name = " + username);

                ServerHttpRequest newRequest = request.mutate()
                        .header("GW_USER", username)
                        .header("GW_USER_ID", userId.toString())
                        .header("GW_TOKEN_ID", tokenId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "")
                        .build();
                chain.filter(exchange.mutate().request(newRequest).build());
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
