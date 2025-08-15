package com.microbank.client.services;

import com.microbank.client.entity.User;

import reactor.core.publisher.Mono;

public interface AuthService {
    public Mono<User> register(User user);

    public Mono<User> login(String email, String password);

    public Mono<Void> logout(String token);
}
