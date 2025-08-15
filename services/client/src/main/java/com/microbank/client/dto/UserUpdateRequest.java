package com.microbank.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String role;
    private Boolean blacklisted;
}
