package com.microbank.client.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microbank.client.entity.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends ReactiveCrudRepository<BlacklistedToken, UUID> {

}
