package org.example.server.jwt;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // Secret key for signing the JWT (should be stored securely in production)
    private static final String SECRET_KEY = "secret";

    // Expiration time for JWT (e.g., 1 hour)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour in milliseconds

    // Method to generate a JWT
    public static String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)  // Set the claims (e.g., username)
                .setSubject(username)  // Set the subject
                .setIssuedAt(new Date())  // Set the issue date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // Set expiration date
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Sign the token with the secret key
                .compact();
    }

    // Method to validate a JWT and retrieve the claims
    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)  // Use the secret key to validate the JWT
                .parseClaimsJws(token)  // Parse and validate the JWT
                .getBody();
    }

    // Method to extract the username from the JWT
    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Method to check if the JWT is expired
    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Method to validate the JWT token
    public static boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
