package com.maurodelcore.expensetracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates and reads the JWTs used to authenticate users. Tokens are signed with a
 * secret key that stays on the server. The key is generated when the application
 * starts, so every token becomes invalid after a restart.
 */
@Component
public class JwtUtils {

    /**
     * Secret key used to sign new tokens and to verify the signature of incoming ones.
     */
    private final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    /**
     * Generates a signed token for a user, valid for 10 hours.
     *
     * @param username the username stored as the subject of the token
     * @return the compact, signed JWT
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)  // The username is the subject of the token
                .issuedAt(new Date(System.currentTimeMillis()))  // Token creation time
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10-hour validity
                .signWith(SECRET_KEY)  // Sign the token with the secret key
                .compact();
    }

    /**
     * Reads the username from a token.
     *
     * @param token the JWT sent by the client
     * @return the username stored in the token's subject
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Verifies the token's signature and returns its claims. Throws an exception
     * if the signature is wrong or the token is malformed or expired.
     *
     * @param token the JWT sent by the client
     * @return the claims (subject, issue time, expiration, and so on)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)  // Use the secret key to verify the signature
                .build()
                .parseSignedClaims(token)  // Parse the signed token
                .getPayload();  // The claims: username, expiration time and others
    }

    /**
     * Checks whether the token's expiration time has passed.
     *
     * @param token the JWT sent by the client
     * @return {@code true} if the token has expired
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Checks that the token belongs to the given user and has not expired.
     *
     * @param token    the JWT sent by the client
     * @param username the username of the user loaded from the database
     * @return {@code true} if the token's username matches and the token is still valid
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

}