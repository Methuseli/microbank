package com.microbank.banking.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microbank.banking.entity.BankTransaction;

import reactor.core.publisher.Flux;

@Service
public interface BankTransactionService {
    Flux<BankTransaction> getTransactionsByAccountId(UUID accountId);
}
