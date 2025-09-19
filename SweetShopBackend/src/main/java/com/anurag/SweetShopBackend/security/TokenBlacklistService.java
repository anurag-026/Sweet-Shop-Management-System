package com.anurag.SweetShopBackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage blacklisted JWT tokens
 * This prevents the use of tokens after logout
 */
@Service
public class TokenBlacklistService {

    // Map to store blacklisted tokens with their expiration time
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * Blacklist a token
     * @param token The JWT token to blacklist
     * @param expiration The token expiration date
     */
    public void blacklistToken(String token, Date expiration) {
        blacklistedTokens.put(token, expiration);
    }
    
    /**
     * Blacklist a token
     * @param token The JWT token to blacklist
     */
    public void blacklistToken(String token) {
        // If no expiration is provided, set it to 24 hours from now
        Date expiration = new Date(System.currentTimeMillis() + 86400000);
        blacklistedTokens.put(token, expiration);
    }
    
    /**
     * Check if a token is blacklisted
     * @param token The JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * Clean up expired tokens from the blacklist
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}
