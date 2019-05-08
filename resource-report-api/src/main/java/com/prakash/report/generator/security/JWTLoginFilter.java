package com.prakash.report.generator.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);

    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        LOGGER.debug("JWTLoginFilter created with AuthenticationManager {} ", authManager.getClass().getName());
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {
        LOGGER.debug("JWTLoginFilter attempting Authentication with Authendication Manager {}",
                getAuthenticationManager().getClass().getName());
        HotelUserCredential creds = new ObjectMapper().readValue(req.getInputStream(), HotelUserCredential.class);
        Authentication authenticate = getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), Collections.emptyList()));
        LOGGER.debug(
                "JWTLoginFilter attempting Authentication with Authendication successful .Setting Authentication Object into Security Context {}",
                authenticate);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        LOGGER.debug("JWTLoginFilter successfulAuthentication called for Authentication Object {} .Generation JWT Token ", auth);
        TokenGeneratorVerifier.generateJWT(res, auth);
        LOGGER.debug("JWTLoginFilter Token Generated anf forwarding");
        chain.doFilter(req, res);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}