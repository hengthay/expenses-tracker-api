package com.hengthay.myapp.account;

import com.hengthay.myapp.user.UserDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private String name;
    private String type;
    private BigDecimal balance;
    private Instant createdAt;
    private Instant updatedAt;
    private UserDto userDto;
}
