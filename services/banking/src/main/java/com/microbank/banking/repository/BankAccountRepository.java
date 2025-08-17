package com.microbank.banking.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microbank.banking.entity.BankAccount;

import reactor.core.publisher.Mono;

@Repository
public interface BankAccountRepository extends ReactiveCrudRepository<BankAccount, UUID>{
    Mono<BankAccount> findByAccountNumber(String accountNumber);

    Mono<BankAccount> findByAccountHolderId(UUID accountHolderId);

    Mono<Boolean> existsByAccountNumber(String accountNumber);

    Mono<Boolean> existsByAccountHolderId(UUID accountHolderId);

}
