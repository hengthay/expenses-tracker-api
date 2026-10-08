package com.hengthay.myapp.dtos;

import com.hengthay.myapp.account.AccountDto;
import com.hengthay.myapp.category.CategoryDto;
import com.hengthay.myapp.controllers.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionDto {
    private UUID id;
    private BigDecimal amount;
    private TransactionType type; // INCOME, EXPENSE
    private LocalDate transactionDate;
    private String notes;
    private AccountDto account;
    private CategoryDto category;
    private Instant createdAt;
    private Instant updatedAt;
}
