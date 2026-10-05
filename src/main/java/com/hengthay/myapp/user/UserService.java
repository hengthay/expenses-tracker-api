package com.hengthay.myapp.user;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        // Get as entity format
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            throw new UserNotFoundException();
        // turn back to dto format as object
        return userMapper.toDto(user);
    }

    public UserDto registerUser(RegisterUserDto request) {
        // map dtos to entity format
        var userEntity = userMapper.toEntity(request);
        // hashing password
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        // save to database
        userRepository.save(userEntity);

        return userMapper.toDto(userEntity);
    }

    public UserDto updateUser(Long id, RequestUserUpdate request) {
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            throw new UserNotFoundException();

        // update record from dto and target to current user that was found
        userMapper.update(request, user);
        // then save new record
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        var user = userRepository.findById(id).orElse(null);

        if(user == null)
            throw new UserNotFoundException();

        userRepository.delete(user);
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElseThrow();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
