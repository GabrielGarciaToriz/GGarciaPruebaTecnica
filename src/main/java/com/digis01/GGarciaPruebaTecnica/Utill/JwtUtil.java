package com.digis01.GGarciaPruebaTecnica.Utill;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String secret;
    @Value("${app.jwt.expiration-ms}")
    private long expiration;
    private SecretKey accessoLlave;
    
    @PostConstruct
    public void init(){
        accessoLlave=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generarToeken(String rfc){
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(rfc)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(accessoLlave)
                .compact();
    }
    public String extraerRFC(String toekn){
        return parseClaims(toekn).getSubject();
    }
    
    public boolean esValido(String token){
        try {
            Date expiration  = parseClaims(token).getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    
    
    private Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(accessoLlave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
