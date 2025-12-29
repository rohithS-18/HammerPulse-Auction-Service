package com.hammperpulse.auction.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JWTService {
    @Value("${jwt.key}")
    private String secretKey;

    public String generateToken(){
        Map<String,Object> claims =new HashMap<>();
        List<String> roles = List.of("ROLE_INTERNAL");
        claims.put("roles",roles);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject("auction-service")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 60L*60*24*30*1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload() ;
    }

    public List extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public  boolean isSignatureValid(String jwt) {
        try {
            if (jwt == null || secretKey == null) {
                return false;
            }
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                return false; // Invalid JWT format
            }
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            // Recompute the signature
            String data = header + "." + payload;
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] computedHash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // Base64 URL encode without padding
            String computedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(computedHash);
            // Compare signatures in constant time to prevent timing attacks
            return constantTimeEquals(signature, computedSignature);
        } catch (Exception e) {
            return false; // Any error means invalid signature
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
