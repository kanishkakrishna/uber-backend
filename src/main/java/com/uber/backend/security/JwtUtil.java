package com.uber.backend.security; // Apne project ka sahi package name yahan check kar lena

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Ye hamara secret password hai jisse token lock hoga (kam se kam 256-bit lamba hona chahiye)
    private static final String SECRET = "uber-clone-backend-very-long-secret-key-for-jwt-authentication";

    // Token ki validity (Yahan maine 10 ghante rakhi hai: 1000ms * 60s * 60m * 10h)
    private static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 1. Token me se Username nikalna
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Token ki Expiry Date nikalna
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 3. Naya Token Generate karna (Login ke baad)
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 4. Token Valid hai ya nahi ye check karna
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}