package com.hengthay.myapp.config;

import com.hengthay.myapp.auth.JwtAuthenticationFilter;
import com.hengthay.myapp.controllers.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // we use this method for encryption our password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // To authenticate user with Spring Security
    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) {
        return config.getAuthenticationManager();
    }
    // filter protected resource
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( c -> {
                            c.requestMatchers("/api/users/**").permitAll();
                            c.requestMatchers("/api/categories/**").permitAll();
                            c.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                            c.requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll();
                            c.requestMatchers(HttpMethod.GET, "/api/accounts").hasRole(Role.ADMIN.name()); // allow only admin
                            c.requestMatchers(HttpMethod.GET, "/api/transactions").permitAll();
                            c.anyRequest().authenticated();
                        }
                )
                // Check token before access to endpoints
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    c.accessDeniedHandler((((request, response, accessDeniedException) ->
                            response.setStatus(HttpStatus.FORBIDDEN.value())
                            )));
                });

        // Note: by default spring will treat when user tried to access protected
        // as 403 error code rather than 401
        // To handle it we can use exceptionHandling to make it correct.
        return http.build();
    }
}
