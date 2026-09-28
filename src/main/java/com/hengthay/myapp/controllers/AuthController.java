package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.LoginRequest;
import com.hengthay.myapp.dtos.LoginResponse;
import com.hengthay.myapp.repository.UserRepository;
import com.hengthay.myapp.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
            ) {
        // To authenticate user with authenticationManager
        // that will check user information and hashed password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail()).orElse(null);

        if(user == null)
            return ResponseEntity.notFound().build();

        var accessToken = jwtService.getAccessToken(user);
        var refreshToken = jwtService.getRefreshToken(user);

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    // Handle unauthorize if invalid information
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
