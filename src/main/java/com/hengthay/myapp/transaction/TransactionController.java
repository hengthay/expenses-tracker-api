package com.hengthay.myapp.transaction;

import com.hengthay.myapp.account.AccountRepository;
import com.hengthay.myapp.category.CategoryRepository;
import com.hengthay.myapp.auth.AuthService;
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

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(
            @PathVariable UUID id
            ) {
        var transaction = transactionService.getTransactionById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(transaction);
    }

    @GetMapping("/me")
    public ResponseEntity<List<TransactionDto>> getMyTransaction() {
        var transaction = transactionService.getMyTransaction();

        return ResponseEntity.status(HttpStatus.OK)
                .body(transaction);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDto>> getTransactionByAccount(
            @PathVariable UUID accountId
    ) {
        var transactions = transactionService.getTransactionByAccount(accountId);

        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            @RequestBody TransactionCreateRequest request
            ) {
        var transaction = transactionService.createTransaction(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable UUID id,
            @RequestBody TransactionUpdateRequest request
            ) {
        var transaction = transactionService.updateTransaction(id, request);

        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable UUID id
    ) {
        transactionService.deleteTransaction(id);

        return ResponseEntity.ok("Transaction deleted successfully!");
    }
}
