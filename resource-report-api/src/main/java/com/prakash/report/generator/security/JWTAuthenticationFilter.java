package com.prakash.report.generator.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JWTAuthenticationFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);

    public JWTAuthenticationFilter() {
        LOGGER.debug("JWTAuthenticationFilter created");
    }

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {
        LOGGER.debug("JWTAuthenticationFilter doFilter verifying rquest JWT token using {} ", TokenGeneratorVerifier.class.getName());

        Authentication authentication = TokenGeneratorVerifier
                .verifyJWT((HttpServletRequest) request, (HttpServletResponse) response);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        LOGGER.debug("JWTAuthenticationFilter doFilter verified JWT .Authentication Object returned {}.Fowarding req to filterChain",
                authentication, filterChain);
        filterChain.doFilter(request, response);
    }
}
