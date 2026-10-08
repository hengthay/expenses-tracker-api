package com.hengthay.myapp.account;

public class UserAccountNotFoundException extends RuntimeException {
    public UserAccountNotFoundException() {
        super("Account not found!");
    }
}
