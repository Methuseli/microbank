package com.microbank.banking.services;

import java.util.UUID;

import com.microbank.banking.entity.BankTransaction;
import com.microbank.banking.repository.BankTransactionRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@AllArgsConstructor
public class BankTransactionServiceImpl implements BankTransactionService{
    private final BankTransactionRepository bankTransactionRepository;

    @Override
    public Flux<BankTransaction> getTransactionsByAccountId(UUID accountId) {
        return bankTransactionRepository.findByAccountId(accountId);
    }
    
}
