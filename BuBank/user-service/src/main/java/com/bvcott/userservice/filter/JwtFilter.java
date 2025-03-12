package com.bvcott.userservice.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bvcott.userservice.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter implements ApplicationContextAware {

    private final JwtUtil jwtUtil;
    private ApplicationContext applicationContext;
    private final static Logger log = LoggerFactory.getLogger(JwtFilter.class);

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        // Log request details
        log.info("Incoming Request URI: {}", requestURI);
        log.info("Authorization Header: {}", authorizationHeader);

        // ✅ **Bypass JWT filter for H2 Console**
        if (requestURI.startsWith("/h2-console")) {
            log.info("Bypassing JWT authentication for H2 Console");
            filterChain.doFilter(request, response);
            return;
        }

        // Skip public endpoints
        if (requestURI.startsWith("/api/v1/auth/register") || requestURI.startsWith("/api/v1/auth/login")) {
            log.info("Public endpoint accessed: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // Validate Authorization header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for request: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 Unauthorized
            return;
        }

        String token = authorizationHeader.substring(7).trim(); // Remove "Bearer " prefix and trim
        log.info("Extracted Token: {}", token);

        try {
            // Extract username and validate token
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    log.info("Token is valid for user: {}", username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("Authentication set for user: {}", username);
                } else {
                    log.warn("Token validation failed for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error during JWT processing: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Return 401 Unauthorized for any error
            return;
        }

        filterChain.doFilter(request, response);
    }
}