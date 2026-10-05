package com.hengthay.myapp.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {
    @NotNull
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String username;

    @Email
    @Size(max = 255, message = "Email is required")
    private String email;

    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters long.")
    private String password;
}
