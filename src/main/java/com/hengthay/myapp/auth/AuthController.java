package com.hengthay.myapp.auth;

import com.hengthay.myapp.user.UserDto;
import com.hengthay.myapp.user.UserMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
            ) {
        var loginResult = authService.login(request);
        // Store refresh token in cookie
        var cookie = new Cookie("refreshToken", loginResult.getRefreshToken().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);

        // Add cookie to response
        response.addCookie(cookie);
        return new JwtResponse(loginResult.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var accessToken = authService.refreshAccessToken(refreshToken);

        return new JwtResponse(accessToken.toString());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var user = authService.getCurrentUser();

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
