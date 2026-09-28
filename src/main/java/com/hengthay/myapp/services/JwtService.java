package com.hengthay.myapp.services;

import com.hengthay.myapp.config.JwtConfig;
import com.hengthay.myapp.controllers.Role;
import com.hengthay.myapp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    // Get access token
    public String getAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    // Get refresh token
    public String getRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    // Generate token
    public String generateToken(User user, long tokenExpiration) {
        // use Jwt to build and generate token from login user
        // and set jwt token information
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getUsername())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    // To check if token valid
    public boolean validateToken(String token) {
        try {
            // If getClaims succeeds, JJWT has already verified the signature
            // and confirmed the expiration date is strictly in the future.
            getClaims(token);

            return true;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // To check and parse token
    private Claims getClaims(String token) {
        // verify with secret key and check token if matching
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract user id from token
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    // Extract user role from token
    public Role getUserRoleFromToken(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }
}
