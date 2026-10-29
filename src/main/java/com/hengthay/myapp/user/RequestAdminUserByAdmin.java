package com.hengthay.myapp.user;

import lombok.Data;

@Data
public class RequestAdminUserByAdmin {
    private String username;
    private String email;
    private Role role;
}
