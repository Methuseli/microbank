package com.microbank.client.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UserService {
    public Mono<User> getUserById(UUID id);

    public Mono<User> updateUser(UserUpdateRequest userUpdateRequest, UUID userId);

    public Mono<Void> deleteUser(UUID id);

    public Flux<User> getAllUsers();
}
