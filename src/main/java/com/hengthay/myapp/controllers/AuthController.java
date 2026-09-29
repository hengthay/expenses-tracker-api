package com.hengthay.myapp.controllers;

import com.hengthay.myapp.config.JwtConfig;
import com.hengthay.myapp.dtos.LoginRequest;
import com.hengthay.myapp.dtos.LoginResponse;
import com.hengthay.myapp.dtos.UserDto;
import com.hengthay.myapp.mappers.UserMapper;
import com.hengthay.myapp.repository.UserRepository;
import com.hengthay.myapp.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
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
        // Store refresh token in cookie
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);

        // Add cookie to response
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public String refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        System.out.println(refreshToken);
        var isTokenValid = jwtService.validateToken(refreshToken);
        // If token expired
        if (!isTokenValid) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        // Get user from token
        var userId = jwtService.getUserIdFromToken(refreshToken);
        // Find user in database if not exist throw error
        var user = userRepository.findById(userId).orElseThrow();

        return jwtService.getAccessToken(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        // get authentication user from filter we set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal(); // extract user id

        var user = userRepository.findById(userId).orElse(null); // find in database

        if(user == null)
            return ResponseEntity.notFound().build();

        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    // Handle unauthorize if invalid information
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
