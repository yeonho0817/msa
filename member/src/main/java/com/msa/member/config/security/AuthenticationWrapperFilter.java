package com.msa.member.config.security;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthenticationFilter Exception handling 용 class
 */
public class AuthenticationWrapperFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;

    public AuthenticationWrapperFilter(HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            doFilter(request, response, filterChain);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
