package com.hengthay.myapp.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionUpdateRequest {
    private Long categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String notes;
}
