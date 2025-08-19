package com.microbank.client.controllers;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.client.dto.LoginRequest;
import com.microbank.client.dto.LogoutRequest;
import com.microbank.client.entity.User;
import com.microbank.client.services.AuthService;
import com.microbank.client.utils.JWTUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/api/v1/auth")
@AllArgsConstructor
@Slf4j
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
                            .secure(false)
                            .path("/")
                            .maxAge(Duration.ofHours(24))
                            .sameSite("Lax")
                            .build();
                    response.addCookie(cookie);
                    return ResponseEntity.ok(user);
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody LogoutRequest logoutRequest, ServerHttpResponse response) {
        return authService.logout(AUTH_TOKEN_COOKIE_NAME, logoutRequest.getUserId())
                .doOnSuccess(aVoid ->
                    response.addCookie(ResponseCookie.from(AUTH_TOKEN_COOKIE_NAME, "")
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .sameSite("Lax")
                            .maxAge(0)
                            .build())
                )
                .thenReturn(ResponseEntity.ok().build());
    }
}
