package com.hengthay.myapp.controllers;

import com.hengthay.myapp.dtos.TransactionCreateRequest;
import com.hengthay.myapp.dtos.TransactionDto;
import com.hengthay.myapp.dtos.TransactionUpdateRequest;
import com.hengthay.myapp.mappers.TransactionMapper;
import com.hengthay.myapp.repository.AccountRepository;
import com.hengthay.myapp.repository.CategoryRepository;
import com.hengthay.myapp.repository.TransactionRepository;
import com.hengthay.myapp.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(
            @PathVariable UUID id
            ) {
        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionMapper.toDto(transaction));
    }

    @GetMapping("/me")
    public ResponseEntity<TransactionDto> getMyTransaction() {
        var user = authService.getCurrentUser();

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var transaction = transactionRepository.findByUserId(user.getId()).orElse(null);

        if(transaction == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionMapper.toDto(transaction));
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @RequestBody TransactionCreateRequest request
            ) {
        var user = authService.getCurrentUser();

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // find account id
        var account = accountRepository.findById(request.getAccountId()).orElse(null);

        if(!account.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        // Category doesn't exist
        if(category == null) {
            return ResponseEntity.badRequest().build();
        }

        var transactionEntity = transactionMapper.toEntity(request);

        transactionEntity.setUser(user);
        transactionEntity.setAccount(account);
        transactionEntity.setCategory(category);

        var savedTransaction = transactionRepository.save(transactionEntity);

        // update account balance
        if(request.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionMapper.toDto(savedTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable UUID id,
            @RequestBody TransactionUpdateRequest request
            ) {
        var user = authService.getCurrentUser();

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            return ResponseEntity.notFound().build();

        // ensure the user owns this transaction
        if(!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        // get account from transaction
        var account = transaction.getAccount();
        // Revert the old transaction from the account balance
        if(transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        transaction.setCategory(category);
        transactionMapper.update(request, transaction);

        // Update new transaction
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        }

        accountRepository.save(account);
        var savedTransaction = transactionRepository.save(transaction);

        return ResponseEntity.ok(transactionMapper.toDto(savedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable UUID id
    ) {
        var user = authService.getCurrentUser();

        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var transaction = transactionRepository.findById(id).orElse(null);

        if(transaction == null)
            return ResponseEntity.notFound().build();

        // ensure the user owns this transaction
        if(!transaction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // get account from transaction
        var account = transaction.getAccount();
        // Revert the old transaction from the account balance
        if(transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }
        accountRepository.save(account);
        transactionRepository.delete(transaction);

        return ResponseEntity.ok("Transaction deleted successfully!");
    }
}
