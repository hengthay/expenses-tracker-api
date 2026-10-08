package com.hengthay.myapp.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TransactionCreateRequest {
    private UUID accountId;
    private Long categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String notes;
}
