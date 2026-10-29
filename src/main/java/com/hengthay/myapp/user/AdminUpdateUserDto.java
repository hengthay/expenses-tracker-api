package com.hengthay.myapp.user;

import lombok.Data;

@Data
public class AdminUpdateUserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
