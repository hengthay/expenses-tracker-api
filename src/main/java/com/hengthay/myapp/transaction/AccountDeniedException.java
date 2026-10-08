package com.hengthay.myapp.transaction;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountDeniedException extends RuntimeException {
    public AccountDeniedException() {
        super("Access was denied!");
    }
}
