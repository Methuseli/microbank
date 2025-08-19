package com.microbank.client.services;

import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microbank.client.entity.BlacklistedToken;
import com.microbank.client.entity.User;
import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaSender<String, String> kafkaSender;

    @Override
    public Mono<User> register(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.<User>error(new RuntimeException("User already exists")))
                .switchIfEmpty(
                        userRepository.save(
                                User.builder()
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .password(passwordEncoder.encode(user.getPassword()))
                                        .role("ADMIN")
                                        .build()
                        ).flatMap(savedUser -> {
                            // Create Kafka message after successful save
                            return sendUserCreatedEvent(savedUser.getId().toString())
                                    .thenReturn(savedUser);  // Return user after sending
                        })
                );
    }

    private Mono<Void> sendUserCreatedEvent(String userId) {
        var producerRecord = new ProducerRecord<String, String>(
                "user-created", // Topic name
                userId, // Key for partitioning
                userId // Value (user ID)
        );

        return kafkaSender.send(Mono.just(SenderRecord.create(producerRecord, userId)))
                .doOnError(e -> log.error("Failed to send user-created event for {}", userId, e))
                .doOnNext(r -> log.info("Sent user-created event for {}", userId))
                .then();  // Convert to Mono<Void>
    }

    @Override
    public Mono<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(retrievedUser -> {
                    if (passwordEncoder.matches(password, retrievedUser.getPassword())) {
                        return Mono.just(retrievedUser);
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });
    }

    @Override
    public Mono<Void> logout(String token, UUID userId) {
        return blacklistedTokenRepository.save(BlacklistedToken.builder().token(token).userId(userId).build())
                .then(Mono.empty());
    }
}
