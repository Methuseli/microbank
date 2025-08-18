package com.microbank.client.repository;

import com.microbank.client.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataR2dbcTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_shouldReturnUser_ifExists() {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .email("alice@example.com")
                .password("hashed")
                .role("USER")
                .build();

        Mono<User> flow = userRepository.save(testUser)
                .then(userRepository.findByEmail("alice@example.com"));

        StepVerifier.create(flow)
                .expectNextMatches(user -> user.getName().equals("Alice") &&
                        user.getEmail().equals("alice@example.com"))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmpty_ifNotExists() {
        StepVerifier.create(userRepository.findByEmail("nonexistent@example.com"))
                .verifyComplete();
    }
}
