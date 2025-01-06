package com.bvcott.bubank.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import com.bvcott.bubank.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
        throws java.io.IOException, jakarta.servlet.ServletException {
            String authorizationHeader = request.getHeader("Authorization");

            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsername(token);

                filterChain.doFilter(request, response);
            }
    }
}
