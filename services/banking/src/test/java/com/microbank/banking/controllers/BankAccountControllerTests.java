package com.microbank.banking.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import com.microbank.banking.dto.TransactionRequest;
import com.microbank.banking.entity.BankAccount;
import com.microbank.banking.services.BankAccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BankAccountControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountController controller;

    private BankAccount account;
    private UUID userId;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        account = BankAccount.builder()
                .id(accountId)
                .accountHolderId(userId)
                .balance(1000.0)
                .build();
    }

    @Test
    void createAccount_shouldReturnCreatedAccount() {
        when(bankAccountService.createAccount(any(BankAccount.class))).thenReturn(Mono.just(account));

        StepVerifier.create(controller.createAccount(account))
                .expectNextMatches(response
                        -> response.getStatusCode() == HttpStatus.CREATED
                && response.getBody().getId().equals(accountId)
                )
                .verifyComplete();

        verify(bankAccountService).createAccount(any(BankAccount.class));
    }

    @Test
    void createAccount_shouldReturnBadRequestIfEmpty() {
        when(bankAccountService.createAccount(any(BankAccount.class))).thenReturn(Mono.empty());

        StepVerifier.create(controller.createAccount(account))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void getAccountByAccountHolderId_shouldReturnAccount() {
        when(bankAccountService.getAccountByAccountHolderId(userId)).thenReturn(Mono.just(account));

        StepVerifier.create(controller.getAccountByAccountHolderId(userId))
                .expectNextMatches(response
                        -> response.getStatusCode() == HttpStatus.OK
                && response.getBody().getAccountHolderId().equals(userId)
                )
                .verifyComplete();
    }

    @Test
    void getAccountByAccountHolderId_shouldReturnNotFound() {
        when(bankAccountService.getAccountByAccountHolderId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(controller.getAccountByAccountHolderId(userId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        when(bankAccountService.getAccountById(accountId)).thenReturn(Mono.just(account));

        StepVerifier.create(controller.getAccountById(accountId))
                .expectNextMatches(response
                        -> response.getStatusCode() == HttpStatus.OK
                && response.getBody().getId().equals(accountId)
                )
                .verifyComplete();
    }

    @Test
    void getAccountById_shouldReturnNotFound() {
        when(bankAccountService.getAccountById(accountId)).thenReturn(Mono.empty());

        StepVerifier.create(controller.getAccountById(accountId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NOT_FOUND)
                .verifyComplete();
    }

    @Test
    void deleteAccount_shouldReturnNoContent() {
        when(bankAccountService.deleteAccount(accountId)).thenReturn(Mono.empty());

        StepVerifier.create(controller.deleteAccount(accountId))
                .expectNextMatches(response -> response.getStatusCode() == HttpStatus.NO_CONTENT)
                .verifyComplete();

        verify(bankAccountService).deleteAccount(accountId);
    }

    @Test
    void deposit_shouldReturnUpdatedAccount() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(500.0)
                .description("Deposit")
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .id(accountId)
                .accountHolderId(userId)
                .balance(1500.0)
                .build();

        when(bankAccountService.deposit(accountId, 500.0, "Deposit")).thenReturn(Mono.just(updatedAccount));

        StepVerifier.create(controller.deposit(accountId, request))
                .expectNextMatches(response
                        -> response.getStatusCode() == HttpStatus.OK
                && response.getBody().getBalance() == 1500.0
                )
                .verifyComplete();
    }

    @Test
    void withdraw_shouldReturnUpdatedAccount() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(200.0)
                .description("Withdraw")
                .build();

        BankAccount updatedAccount = BankAccount.builder()
                .id(accountId)
                .accountHolderId(userId)
                .balance(800.0)
                .build();

        when(bankAccountService.withdraw(accountId, 200.0, "Withdraw"))
                .thenReturn(Mono.just(updatedAccount));

        StepVerifier.create(controller.withdraw(accountId, request))
                .expectNextMatches(response
                        -> response.getStatusCode() == HttpStatus.OK
                && response.getBody().getBalance() == 800.0
                )
                .verifyComplete();
    }

}
