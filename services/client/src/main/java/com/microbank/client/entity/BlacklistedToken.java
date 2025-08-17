package com.microbank.client.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    private UUID id;
    @NonNull
    private String token;
    private UUID userId;
    private LocalDateTime createdAt;
}