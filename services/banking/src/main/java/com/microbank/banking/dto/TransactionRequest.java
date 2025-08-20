package com.microbank.banking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionRequest {
    private double amount;
    private String description;
}