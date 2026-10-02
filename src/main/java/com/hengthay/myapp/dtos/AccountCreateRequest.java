package com.hengthay.myapp.dtos;

import com.hengthay.myapp.controllers.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    private String name;
    private AccountType type;
    private BigDecimal balance;
}
