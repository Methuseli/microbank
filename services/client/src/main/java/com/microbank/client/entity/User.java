package com.microbank.client.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("users")
public class User {
    @Id
    private UUID id;
    @NonNull
    private String name;

    @NonNull
    private String password;
    @NonNull
    private String email;

    // TODO: Consider using an enum for roles or a separate Role entity
    private String role;

    private LocalDateTime createdAt;

    private Boolean blacklisted;
}
