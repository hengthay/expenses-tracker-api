package com.hengthay.myapp.auth;

import com.hengthay.myapp.user.Role;
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
    public Jwt getAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    // Get refresh token
    public Jwt getRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    // Generate token
    public Jwt generateToken(User user, long tokenExpiration) {
        // use Jwt to build and generate token from login user
        // and set jwt token information
        Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("name", user.getUsername())
                .add("email", user.getEmail())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    // To check if token valid
    public Jwt validateToken(String token) {
        try {
            // If getClaims succeeds, JJWT has already verified the signature
            // and confirmed the expiration date is strictly in the future.
            var claims = getClaims(token);

            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return null;
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
