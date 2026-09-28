package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.ChangePasswordRequest;
import com.hengthay.myapp.dtos.RegisterUserDto;
import com.hengthay.myapp.dtos.RequestUserUpdate;
import com.hengthay.myapp.dtos.UserDto;
import com.hengthay.myapp.mappers.UserMapper;
import com.hengthay.myapp.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @RequestMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserDto request
            ) {
        var userEntity = userMapper.toEntity(request);
        // hashing password
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        var userDto = userMapper.toDto(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody RequestUserUpdate request
            ) {
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            return ResponseEntity.notFound().build();

        userMapper.update(request, user);

        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            return ResponseEntity.notFound().build();

        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public void changePassword(Long id, ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElseThrow();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
