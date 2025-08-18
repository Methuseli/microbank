package com.microbank.client.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.client.dto.LoginRequest;
import com.microbank.client.entity.User;
import com.microbank.client.services.AuthService;
import com.microbank.client.utils.JWTUtil;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@RequestBody User user) {
        return authService.register(user)
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(user));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .map(user -> {
                    // Generate JWT token and return in response body
                    String token = jwtUtil.generateToken(user);
                    return ResponseEntity.ok(token);
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody UUID userId, @org.springframework.web.bind.annotation.RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        String token = authHeader.substring(7);
        // Token-based logout: invalidate token server-side if implemented
        return authService.logout(token, userId)
                .thenReturn(ResponseEntity.ok().build());
    }
}
