package com.hengthay.myapp.account;

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
    private final AccountService accountService;

    @GetMapping
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable UUID id) {
        var account = accountService.getAccountById(id);

        return ResponseEntity.ok(account);
    }

    @GetMapping("/me")
    public ResponseEntity<AccountDto> getMyAccount() {
        var userAccount = accountService.getMyAccount();

        return ResponseEntity.ok(userAccount);
    }

    @PostMapping
    public ResponseEntity<AccountDto> registerAccount(
            @RequestBody AccountCreateRequest request
            ) {
        var account = accountService.registerAccount(request);

        // FIXED: Return 201 CREATED for new resources instead of 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable UUID id,
            @RequestBody RequestAccountUpdate request
            ) {
        var account = accountService.updateAccount(id, request);

        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{id}/delete-account")
    public ResponseEntity<String> deleteAccount(
            @PathVariable UUID id
    ) {
        accountService.deleteAccount(id);

        return ResponseEntity.status(HttpStatus.OK).body("Account deleted successfully!");
    }
}
