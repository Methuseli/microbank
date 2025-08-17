package com.microbank.banking.services;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.microbank.banking.entity.BankAccount;
import com.microbank.banking.entity.BankTransaction;
import com.microbank.banking.repository.BankAccountRepository;
import com.microbank.banking.repository.BankTransactionRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final KafkaReceiver<String, String> kafkaReceiver;
    private Disposable consumerDisposable;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository,
            BankTransactionRepository bankTransactionRepository,
            KafkaReceiver<String, String> kafkaReceiver) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.kafkaReceiver = kafkaReceiver;
    }

    private static final int ACCOUNT_NUMBER_LENGTH = 10; // 10-digit account number
    private static final int MAX_GENERATION_ATTEMPTS = 5;

    @PostConstruct
    public void startConsuming() {
        consumerDisposable = kafkaReceiver.receive()
                .doOnNext(record -> log.info("Received message: key={}, value={}", record.key(), record.value()))
                .concatMap(this::processRecord)
                .subscribe();
    }

    private Mono<Void> processRecord(ReceiverRecord<String, String> record) {
        try {
            UUID userId = UUID.fromString(record.value()); // validate
            return createAccountForUser(userId)
                    .then(commitOffset(record.receiverOffset()));
        } catch (IllegalArgumentException e) {
            // Value wasn't a UUID
            log.error("Invalid UUID in record: {}", record.value(), e);
            return commitOffset(record.receiverOffset()); // skip bad record
        }
    }

    private Mono<Void> commitOffset(ReceiverOffset offset) {
        return offset.commit()
                .doOnSuccess(v -> log.debug("Committed offset {}", offset))
                .doOnError(e -> log.warn("Offset commit failed", e));
    }

    @PreDestroy
    public void stopConsuming() {
        if (consumerDisposable != null && !consumerDisposable.isDisposed()) {
            consumerDisposable.dispose();
            log.info("Kafka consumer stopped");
        }
    }

    private Mono<BankAccount> createAccountForUser(UUID userId) {
        BankAccount account = BankAccount.builder()
                .balance(0.0)
                .accountHolderId(userId)
                .build();
        return bankAccountRepository.existsByAccountHolderId(userId)
                .flatMap(exists -> exists
                ? Mono.error(new RuntimeException("Account already exists for this user"))
                : generateUniqueAccountNumber()
                        .doOnNext(account::setAccountNumber)
                        .then(bankAccountRepository.save(account))
                );
    }

    @Override
    public Mono<BankAccount> createAccount(BankAccount account) {
        return bankAccountRepository.existsByAccountHolderId(account.getAccountHolderId())
                .flatMap(exists -> exists
                ? Mono.error(new RuntimeException("Account already exists for this user"))
                : generateUniqueAccountNumber()
                        .doOnNext(account::setAccountNumber)
                        .then(bankAccountRepository.save(account))
                );
    }

    @Override
    public Mono<BankAccount> getAccountById(UUID id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public Mono<BankAccount> getAccountByAccountHolderId(UUID userId) {
        return bankAccountRepository.findByAccountHolderId(userId);
    }

    @Override
    public Mono<Void> deleteAccount(UUID id) {
        return bankAccountRepository.deleteById(id);
    }

    @Override
    public Mono<BankAccount> deposit(UUID id, double amount, String description) {
        return bankAccountRepository.findById(id)
                .flatMap(account -> {
                    account.setBalance(account.getBalance() + amount);

                    BankTransaction transaction = BankTransaction.builder()
                            .accountId(id)
                            .amount(amount)
                            .transactionType("deposit")
                            .description(description)
                            .build();

                    return bankTransactionRepository.save(transaction)
                            .then(bankAccountRepository.save(account));
                });
    }

    @Override
    public Mono<BankAccount> withdraw(UUID id, double amount, String description) {
        return bankAccountRepository.findById(id)
                .flatMap(account -> {
                    if (account.getBalance() < amount) {
                        return Mono.error(new RuntimeException("Insufficient funds"));
                    }
                    account.setBalance(account.getBalance() - amount);

                    BankTransaction transaction = BankTransaction.builder()
                            .accountId(id)
                            .amount(amount)
                            .transactionType("withdrawal")
                            .description(description)
                            .build();
                    
                    return bankTransactionRepository.save(transaction)
                            .then(bankAccountRepository.save(account));
                });
    }

    private Mono<String> generateUniqueAccountNumber() {
        return Mono.fromCallable(this::generateRandomAccountNumber)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(candidate
                        -> bankAccountRepository.existsByAccountNumber(candidate)
                        .flatMap(exists
                                -> exists ? Mono.empty() : Mono.just(candidate)
                        ))
                .retryWhen(Retry.fixedDelay(MAX_GENERATION_ATTEMPTS, Duration.ofMillis(100)))
                .switchIfEmpty(Mono.defer(()
                        -> Mono.error(new RuntimeException("Failed to generate unique account number after "
                        + MAX_GENERATION_ATTEMPTS + " attempts")))
                );
    }

    private String generateRandomAccountNumber() {
        long min = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH - 1);
        long max = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH) - 1;
        long randomNumber = ThreadLocalRandom.current().nextLong(min, max + 1);
        return String.valueOf(randomNumber);
    }

}
