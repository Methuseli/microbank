package com.microbank.banking.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table("bank_accounts")
public class BankAccount {
    private UUID id;
    private String accountNumber;
    @NonNull
    private UUID accountHolderId;
    private double balance;
    private LocalDateTime createdAt;
}
