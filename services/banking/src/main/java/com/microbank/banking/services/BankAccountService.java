package com.microbank.banking.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microbank.banking.entity.BankAccount;

import reactor.core.publisher.Mono;

@Service
public interface BankAccountService {
    public Mono<BankAccount> createAccount(BankAccount account);

    public Mono<BankAccount> getAccountById(UUID id);

    public Mono<Void> deleteAccount(UUID id);

    public Mono<BankAccount> deposit(UUID id, double amount);

    public Mono<BankAccount> withdraw(UUID id, double amount);
}
