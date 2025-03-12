package com.bvcott.userservice.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bvcott.userservice.filter.JwtFilter;
import com.bvcott.userservice.util.JwtUtil;

@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Allow H2 console frames
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/accounts").authenticated()
                        .requestMatchers("/api/v1/accounts/**").authenticated()
                        .requestMatchers("/api/v1/transactions").authenticated()
                        .requestMatchers("/api/v1/transactions/**").authenticated()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/account-requests").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating password encoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Lazy
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil);
    }
}