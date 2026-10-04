package com.hengthay.myapp.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Jwt accessToken;
    private Jwt refreshToken;
}
