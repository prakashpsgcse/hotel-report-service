package com.target.report.generator.security;

import static java.util.Collections.emptyList;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.target.report.generator.domain.HotelUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Thid class generates JWT after successful authentication and verify the token
 * 
 * @author pprakash
 *
 */
@Service
public class TokenGeneratorVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenGeneratorVerifier.class);

    public static void generateJWT(HttpServletResponse res, Authentication auth) {
        LOGGER.debug("TokenAuthenticationService creating token for user: {} ", auth.getName());
        HotelUser hotelUser = ((HotelUserPrincipal) auth.getPrincipal()).getHotelUser();
        String JWT = Jwts.builder()
                .setSubject(hotelUser.getHotelId())
                .claim("scope", hotelUser.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + JWTConstants.JWT_TTL))
                .signWith(JWTConstants.SIGN_ALGORITHM, JWTConstants.SECRET_KEY)
                .compact();
        // TODO send it payload if required
        res.addHeader(HttpHeaders.AUTHORIZATION, JWTConstants.TOKEN_PREFIX + " " + JWT);
    }

    static Authentication verifyJWT(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug("TokenGeneratorVerifier verifying token");
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        String user = null;
        String scope = null;
        if (token != null) {
            try {
                user = Jwts.parser()
                        .setSigningKey(JWTConstants.SECRET_KEY)
                        .parseClaimsJws(token.replace(JWTConstants.TOKEN_PREFIX, ""))
                        .getBody()
                        .getSubject();
                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(JWTConstants.SECRET_KEY)
                        .parseClaimsJws(token.replace(JWTConstants.TOKEN_PREFIX, ""));
                scope = claims.getBody().get("scope").toString();
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                throw new BadCredentialsException(HttpStatus.UNAUTHORIZED.name());
            }
            HotelUser user1 = new HotelUser();
            user1.setHotelId(user);
            // TODO use in ROLE based auth
            user1.setRole(scope);
            HotelUserPrincipal principal = new HotelUserPrincipal();
            principal.setHotelUser(user1);
            return new UsernamePasswordAuthenticationToken(principal, null, emptyList());
        }
        else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }
    }
}