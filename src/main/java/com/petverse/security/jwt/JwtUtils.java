package com.petverse.security.jwt;
import com.petverse.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${backendapi.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    // DİKKAT: Bu değer Base64 olmalı (örn. openssl rand -base64 64 çıktısı)
    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecretBase64;

    private Key key;

    @PostConstruct
    public void init() {
        // Base64 decode → 32+ byte zorunlu
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", userDetails.getId().toString())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    // .setAllowedClockSkewSeconds(60) // istersen küçük saat kaymalarını tolere et
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("JWT malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            LOGGER.error("JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims empty: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
