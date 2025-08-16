package com.microbank.banking.services;

import java.util.UUID;

import com.microbank.banking.entity.BankAccount;
import com.microbank.banking.entity.BankTransaction;
import com.microbank.banking.repository.BankAccountRepository;
import com.microbank.banking.repository.BankTransactionRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionRepository bankTransactionRepository;

    @Override
    public Mono<BankAccount> createAccount(BankAccount account) {
        return bankAccountRepository.findByAccountHolderId(account.getAccountHolderId())
            .flatMap(existingAccount -> Mono.<BankAccount>error(new RuntimeException("Account already exists"))
            )
            .switchIfEmpty(bankAccountRepository.save(account));
    }

    @Override
    public Mono<BankAccount> getAccountById(UUID id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public Mono<Void> deleteAccount(UUID id) {
        return bankAccountRepository.deleteById(id);
    }

    @Override
    public Mono<BankAccount> deposit(UUID id, double amount) {
        return bankAccountRepository.findById(id)
            .flatMap(account -> {
                account.setBalance(account.getBalance() + amount);
                bankTransactionRepository.save(
                    BankTransaction.builder()
                        .accountId(id)
                        .amount(amount)
                        .transactionType("DEPOSIT")
                        .build()
                ).subscribe(); // Save transaction asynchronously
                return bankAccountRepository.save(account);
            });
    }

    @Override
    public Mono<BankAccount> withdraw(UUID id, double amount) {
        return bankAccountRepository.findById(id)
            .flatMap(account -> {
                if (account.getBalance() >= amount) {
                    account.setBalance(account.getBalance() - amount);
                    bankTransactionRepository.save(
                        BankTransaction.builder()
                            .accountId(id)
                            .amount(amount)
                            .transactionType("WITHDRAWAL")
                            .build()
                    ).subscribe(); // Save transaction asynchronously
                    return bankAccountRepository.save(account);
                } else {
                    return Mono.error(new RuntimeException("Insufficient funds"));
                }
            });
    }
    
}
