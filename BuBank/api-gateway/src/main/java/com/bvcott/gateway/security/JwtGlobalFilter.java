package com.bvcott.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtGlobalFilter.class);

    @Autowired private JwtUtil jwtUtil;

    @Override public int getOrder() {
        // Run early in the filter chain
        return -1;
    }

    @Override public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String path = exchange.getRequest().getURI().getPath();

        // Allow public endpoints without token validation
        if (path.startsWith("/api/v1/auth/register") || path.startsWith("/api/v1/auth/login")) {
            log.info("Bypassing JWT validation for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JWT token not found in request headers");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7).trim();

        try{ 
            String username = jwtUtil.extractUsername(token);
            log.info("JWT token validated for user: {}", username);
            //Forward the validated username in a custom header
            exchange = exchange
                .mutate()
                .request(builder -> builder.header("X-Authenticated-User", username))
                .build();
        } catch(Exception e) {
            log.error("Token validation error: {}", e.getMessage(), e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
