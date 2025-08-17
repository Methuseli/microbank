package com.microbank.client.controllers;

import java.util.UUID;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;
import com.microbank.client.services.UserService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Mono<ResponseEntity<User>> getMethodName(@PathVariable UUID userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getBlacklisted() != null || userUpdateRequest.getRole() != null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        return userService.updateUser(userUpdateRequest, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/admin/{userId}")
    public Mono<ResponseEntity<User>> blacklistUser(@PathVariable UUID userId, @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(userUpdateRequest, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable UUID userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.notFound().<Void>build());
    }

    @GetMapping("/admin")
    public Flux<ResponseEntity<User>> getAllUsers() {
        return userService.getAllUsers()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/current-user")
    public Mono<ResponseEntity<User>> getCurrentUser(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst("auth_token");
        String token = cookie.getValue();
        return userService.getCurrentUser(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
