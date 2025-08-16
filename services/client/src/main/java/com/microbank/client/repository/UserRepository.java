package com.microbank.client.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microbank.client.entity.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends  ReactiveCrudRepository<User, UUID>{
    Mono<User> findByEmail(String email);
}
