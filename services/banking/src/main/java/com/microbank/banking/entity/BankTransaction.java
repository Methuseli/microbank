package com.microbank.banking.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Table("bank_transactions")
@Builder
public class BankTransaction {
    private UUID id;
    @NonNull
    private UUID accountId;
    private double amount;
    @NonNull
    private String transactionType;
    @NonNull
    private LocalDateTime timestamp;
    private String description;
}
