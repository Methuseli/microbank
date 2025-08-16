package com.microbank.client.controllers;

import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private static final String AUTH_TOKEN_COOKIE_NAME = "auth_token";

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@RequestBody User user) {
        return authService.register(user)
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(user));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<User>> login(@RequestBody LoginRequest loginRequest, ServerHttpResponse response) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .map(user -> {
                    ResponseCookie cookie = ResponseCookie.from(AUTH_TOKEN_COOKIE_NAME, jwtUtil.generateToken(user))
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(Duration.ofHours(1))
                            .build();
                    response.addCookie(cookie);
                    return ResponseEntity.ok(user);
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody UUID userId, ServerHttpResponse response) {
        return authService.logout(AUTH_TOKEN_COOKIE_NAME, userId)
                .doOnSuccess(aVoid -> 
                    response.addCookie(ResponseCookie.from(AUTH_TOKEN_COOKIE_NAME, "")
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .maxAge(0)
                            .build())
                )
                .thenReturn(ResponseEntity.ok().build());
    }
}
