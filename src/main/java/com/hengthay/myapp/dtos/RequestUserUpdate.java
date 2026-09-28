package com.hengthay.myapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUserUpdate {
    private String name;
    private String email;
}
