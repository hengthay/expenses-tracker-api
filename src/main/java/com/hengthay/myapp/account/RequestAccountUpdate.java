package com.hengthay.myapp.account;

import lombok.Data;

@Data
public class RequestAccountUpdate {
    private String name;
    private AccountType type;
}
