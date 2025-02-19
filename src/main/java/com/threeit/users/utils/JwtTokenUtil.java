package com.threeit.users.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    /**
     * Generates a JSON Web Token (JWT) for the specified email.
     *
     * @param email the email for which the token is to be generated
     * @return a signed JWT as a String
     */
    public String generateToken(String email) {
        // 60 Minute
        long EXPIRATION_TIME = 1000 * 60 * 60;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the provided JWT token by verifying its signature and claims.
     *
     * @param token the JWT token to be validated
     * @return true if the token is valid and properly signed, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts the email address from the provided JWT token.
     *
     * @param token the JWT token from which to extract the email
     * @return the email address contained within the token's subject field
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
