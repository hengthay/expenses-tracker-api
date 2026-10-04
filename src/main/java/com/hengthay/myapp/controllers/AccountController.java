package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.AccountCreateRequest;
import com.hengthay.myapp.dtos.AccountDto;
import com.hengthay.myapp.dtos.RequestAccountUpdate;
import com.hengthay.myapp.mappers.AccountMapper;
import com.hengthay.myapp.repository.AccountRepository;
import com.hengthay.myapp.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final AuthService authService;

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            return ResponseEntity.notFound().build();

        var accountDto = accountMapper.toDto(account);

        return ResponseEntity.ok(accountDto);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDto> getMyAccount() {
        var user = authService.getCurrentUser();

        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        var account = accountRepository.findByUserId(user.getId()).orElse(null);

        if(account == null)
            return ResponseEntity.notFound().build();

        var accountDto = accountMapper.toDto(account);

        return ResponseEntity.ok(accountDto);
    }

    @PostMapping
    public ResponseEntity<AccountDto> registerAccount(
            @RequestBody AccountCreateRequest request
            ) {
        var user = authService.getCurrentUser();

        if(user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        var accountEntity = accountMapper.toEntity(request);
        accountEntity.setUser(user);

        var savedAccount = accountRepository.save(accountEntity);

        // FIXED: Return 201 CREATED for new resources instead of 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(accountMapper.toDto(savedAccount));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable UUID id,
            @RequestBody RequestAccountUpdate request
            ) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            return ResponseEntity.notFound().build();

        var user = authService.getCurrentUser();
        // to check only owner of this account can be update information
        if(user == null || !account.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        accountMapper.update(request, account);

        var savedAccount = accountRepository.save(account);

        return ResponseEntity.ok(accountMapper.toDto(savedAccount));
    }

    @DeleteMapping("/{id}/delete-account")
    public ResponseEntity<String> deleteAccount(
            @PathVariable UUID id
    ) {
        var account = accountRepository.getAccountById(id).orElse(null);

        if(account == null)
            return ResponseEntity.notFound().build();

        var user = authService.getCurrentUser();
        // to check only owner of this account can be update information
        if(user == null || !account.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(!account.getTransactions().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete account because it contains active transactions. Delete the transactions first.");
        }

        accountRepository.delete(account);

        return ResponseEntity.status(HttpStatus.OK).body("Account deleted successfully!");
    }
}
