package com.microbank.client.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microbank.client.entity.User;

import reactor.core.publisher.Mono;

@Service
public interface AuthService {
    public Mono<User> register(User user);

    public Mono<User> login(String email, String password);

    public Mono<Void> logout(String token, UUID userId);
}
