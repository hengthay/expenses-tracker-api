package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.AccountDto;
import com.hengthay.myapp.mappers.AccountMapper;
import com.hengthay.myapp.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @RequestMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            return ResponseEntity.notFound().build();

        var accountDto = accountMapper.toDto(account);

        return ResponseEntity.ok(accountDto);
    }

//    @PostMapping
//    public ResponseEntity<AccountDto> registerAccount() {
//
//    }
}
