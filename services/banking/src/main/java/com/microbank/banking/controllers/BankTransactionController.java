package com.microbank.banking.controllers;

import java.util.UUID;

import org.springframework.boot.autoconfigure.pulsar.PulsarProperties.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.banking.entity.BankTransaction;
import com.microbank.banking.services.BankTransactionService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/bank-transactions")
@AllArgsConstructor
public class BankTransactionController {

    private final BankTransactionService bankTransactionService;

    @GetMapping("/{accountId}/transactions")
    public Mono<ResponseEntity<Flux<BankTransaction>>> getTransactions(
            @PathVariable String accountId
    ) {
        Flux<BankTransaction> transactions = bankTransactionService.getTransactionsByAccountId(UUID.fromString(accountId));
        return Mono.just(ResponseEntity.ok().body(transactions));
    }
}
