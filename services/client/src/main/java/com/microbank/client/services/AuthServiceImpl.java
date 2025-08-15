package com.microbank.client.services;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.microbank.client.entity.BlacklistedToken;
import com.microbank.client.entity.User;
import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final PasswordEncoder passwordEncoder;

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
                                        .role("USER")
                                        .build()
                        )
                );
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
    public Mono<Void> logout(String token) {
        return blacklistedTokenRepository.save(BlacklistedToken.builder().token(token).build())
                .then(Mono.empty());
    }
}
