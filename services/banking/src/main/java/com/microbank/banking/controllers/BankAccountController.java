package com.microbank.banking.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.banking.dto.TransactionRequest;
import com.microbank.banking.entity.BankAccount;
import com.microbank.banking.services.BankAccountService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bank-accounts")
@AllArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping("")
    public Mono<ResponseEntity<BankAccount>> createAccount(@RequestBody BankAccount account) {
        return bankAccountService.createAccount(account)
                .map(createdAccount -> ResponseEntity.status(HttpStatus.CREATED).body(createdAccount))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<BankAccount>> getAccountByAccountHolderId(@PathVariable UUID userId) {
        return bankAccountService.getAccountByAccountHolderId(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public Mono<ResponseEntity<BankAccount>> getAccountById(@PathVariable UUID id) {
        return bankAccountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable UUID id) {
        return bankAccountService.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deposit")
    public Mono<ResponseEntity<BankAccount>> deposit(@PathVariable UUID id, @RequestBody TransactionRequest transactionRequest) {
        return bankAccountService.deposit(id, transactionRequest.getAmount(), transactionRequest.getDescription())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/withdraw")
    public Mono<ResponseEntity<BankAccount>> withdraw(@PathVariable UUID id, @RequestBody TransactionRequest transactionRequest) {
        return bankAccountService.withdraw(id, transactionRequest.getAmount(), transactionRequest.getDescription())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
