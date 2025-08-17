package com.microbank.banking.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microbank.banking.entity.BankAccount;

import reactor.core.publisher.Mono;

@Repository
public interface BankAccountRepository extends ReactiveCrudRepository<BankAccount, UUID>{
    Mono<BankAccount> findByAccountNumber(String accountNumber);

    Mono<BankAccount> findByAccountHolderId(UUID accountHolderId);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
           "FROM bank_account WHERE account_number = :accountNumber")
    Mono<Boolean> existsByAccountNumber(String accountNumber);

    Mono<Boolean> existsByAccountHolderId(UUID accountHolderId);

}
