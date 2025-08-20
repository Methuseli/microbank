package com.microbank.client.repository;

import com.microbank.client.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

    @Mock
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

        // Mock the behavior
        when(userRepository.save(testUser)).thenReturn(Mono.just(testUser));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Mono.just(testUser));

        Mono<User> flow = userRepository.save(testUser)
                .then(userRepository.findByEmail("alice@example.com"));

        StepVerifier.create(flow)
                .expectNextMatches(user -> user.getName().equals("Alice")
                && user.getEmail().equals("alice@example.com"))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmpty_ifNotExists() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByEmail("nonexistent@example.com"))
                .verifyComplete();
    }
}
