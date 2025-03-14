package com.bvcott.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange(exchanges -> exchanges
                // Allow preflight OPTIONS requests on all paths
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().permitAll()  // Adjust as needed
            );
        return http.build();
    }
}
