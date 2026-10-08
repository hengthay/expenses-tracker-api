package com.hengthay.myapp.account;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    private String name;
    private AccountType type;
    private BigDecimal balance;
}
