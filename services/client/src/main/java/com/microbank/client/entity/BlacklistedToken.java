package com.microbank.client.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    private UUID id;
    private String token;

}