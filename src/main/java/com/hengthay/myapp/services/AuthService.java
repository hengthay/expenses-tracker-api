package com.hengthay.myapp.services;

import com.hengthay.myapp.entities.User;
import com.hengthay.myapp.repository.UserRepository;
import io.jsonwebtoken.impl.security.EdwardsCurve;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private UserRepository userRepository;

    public User getCurrentUser() {
        // get authentication user from filter we set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal(); // extract user id

        // find in database and return user
        return userRepository.findById(userId).orElse(null);
    }
}
