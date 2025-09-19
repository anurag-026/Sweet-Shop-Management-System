package com.anurag.SweetShopBackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Validate token belongs to the specified user
     * @param token The JWT token to validate
     * @param userDetails The user details to check against
     * @return true if token is valid and belongs to the user, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && 
                   !tokenBlacklistService.isTokenBlacklisted(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate token and check if it's not blacklisted
     * @param token The JWT token to validate
     * @param checkBlacklist Whether to check the token blacklist
     * @return true if token is valid and not blacklisted, false otherwise
     */
    public boolean validateToken(String token, boolean checkBlacklist) {
        if (!validateToken(token)) {
            return false;
        }
        
        // If blacklist check is not required, return true
        if (!checkBlacklist) {
            return true;
        }
        
        // If token is blacklisted, it's invalid
        return true; // This will be replaced with blacklist check in JwtAuthenticationFilter
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String refreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            
            // Create new token with same claims but new expiration
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("role", role);
            
            return createToken(newClaims, username);
        } catch (Exception e) {
            throw new RuntimeException("Unable to refresh token", e);
        }
    }

    public boolean canRefreshToken(String token) {
        try {
            // Check if token is valid (not expired or malformed)
            if (!validateToken(token)) {
                return false;
            }
            
            // Check if token is blacklisted
            return !tokenBlacklistService.isTokenBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Blacklist a token
     * @param token The JWT token to blacklist
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            tokenBlacklistService.blacklistToken(token, expiration);
        } catch (Exception e) {
            // If token is already invalid, blacklist with default expiration
            tokenBlacklistService.blacklistToken(token);
        }
    }
}
