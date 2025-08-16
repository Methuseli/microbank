package com.microbank.banking.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.banking.entity.BankTransaction;
import com.microbank.banking.services.BankTransactionService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/v1/bank-transactions")
@AllArgsConstructor
public class BankTransactionController {
    private final BankTransactionService bankTransactionService;

    @GetMapping("/{accountId}/transactions")
    public Flux<ResponseEntity<BankTransaction>> getTransactionsByAccountId(@PathVariable UUID accountId) {
        return bankTransactionService.getTransactionsByAccountId(accountId)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}