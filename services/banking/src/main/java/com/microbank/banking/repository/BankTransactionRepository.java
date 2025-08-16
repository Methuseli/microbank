package com.microbank.banking.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microbank.banking.entity.BankTransaction;

import reactor.core.publisher.Flux;

@Repository
public interface BankTransactionRepository extends ReactiveCrudRepository<BankTransaction, UUID>{
    Flux<BankTransaction> findByAccountId(UUID accountId);
}
