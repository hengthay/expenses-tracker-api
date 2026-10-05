package com.hengthay.myapp.auth;

import com.hengthay.myapp.entities.User;
import com.hengthay.myapp.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public User getCurrentUser() {
        // get authentication user from filter we set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal(); // extract user id

        // find in database and return user
        return userRepository.findById(userId).orElse(null);
    }

    public LoginResponse login(LoginRequest request) {
        // To authenticate user with authenticationManager
        // that will check user information and hashed password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();

        var accessToken = jwtService.getAccessToken(user);
        var refreshToken = jwtService.getRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    public Jwt refreshAccessToken(String refreshToken) {
        Jwt jwt = jwtService.validateToken(refreshToken);
        // If token expired
        if (jwt == null || jwt.isExpired()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        // Get user from token
        var userId = jwt.getUserId();
        // Find user in database if not exist throw error
        var user = userRepository.findById(userId).orElseThrow();

        return jwtService.getAccessToken(user);
    }
}
