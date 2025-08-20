package com.microbank.client.services;

import com.microbank.client.entity.User;
import com.microbank.client.entity.BlacklistedToken;
import com.microbank.client.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import com.microbank.client.repository.BlacklistedTokenRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.reactivestreams.Publisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@Slf4j
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaSender<String, String> kafkaSender;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("john@example.com")
                .password("rawPassword")
                .role("USER")
                .build();
    }

    @Test
    void register_shouldSaveNewUserAndSendKafkaEvent() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return Mono.just(u);
        });

        // Corrected KafkaSender mock
        when(kafkaSender.send(any(Publisher.class))).thenReturn(Flux.empty());

        StepVerifier.create(authService.register(testUser))
                .expectNextMatches(user
                        -> user.getEmail().equals("john@example.com")
                && user.getPassword().equals("encodedPassword")
                && user.getRole().equals("USER"))
                .verifyComplete();

        verify(userRepository).save(any(User.class));
        verify(kafkaSender).send(any(Publisher.class));
    }

    @Test
    void register_shouldErrorIfUserExists() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));
        log.info("User {} ", testUser);
        StepVerifier.create(authService.register(testUser))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("User already exists"))
                .verify();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnUserWhenCredentialsMatch() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("rawPassword", testUser.getPassword())).thenReturn(true);
        StepVerifier.create(authService.login("john@example.com", "rawPassword"))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void login_shouldErrorWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches("wrong", testUser.getPassword())).thenReturn(false);
        StepVerifier.create(authService.login("john@example.com", "wrong"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void login_shouldErrorWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Mono.empty());
        StepVerifier.create(authService.login("missing@example.com", "whatever"))
                .expectErrorMatches(e -> e instanceof RuntimeException &&
                        e.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void logout_shouldSaveBlacklistedToken() {
        UUID userId = UUID.randomUUID();
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class)))
                .thenReturn(Mono.just(BlacklistedToken.builder()
                        .token("token123")
                        .userId(userId)
                        .build()));
        StepVerifier.create(authService.logout("token123", userId))
                .verifyComplete();
        verify(blacklistedTokenRepository).save(any(BlacklistedToken.class));
    }
}
