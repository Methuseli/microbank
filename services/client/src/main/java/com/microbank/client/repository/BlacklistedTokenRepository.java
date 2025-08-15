package com.microbank.client.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.microbank.client.entity.BlacklistedToken;

public interface BlacklistedTokenRepository extends ReactiveCrudRepository<BlacklistedToken, UUID> {

}
