package com.bvcott.discoveryserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class EurekaSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity
            .csrf(csrf -> csrf.disable())
            // Permit all requests to Eureka endpoints
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/eureka/**").permitAll()
                    .anyRequest().authenticated()
            )
            // Use HTTP Basic for any remaining endpoints
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}