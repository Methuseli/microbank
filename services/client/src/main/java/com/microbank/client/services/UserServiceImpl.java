package com.microbank.client.services;

import java.util.UUID;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.utils.JWTUtil;
import com.microbank.client.utils.UserMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUser(UserUpdateRequest userUpdateRequest, UUID userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    log.info("Before mapping: {}", user);
                    log.info("User update data: {}", userUpdateRequest.getBlacklisted());
                    UserMapper.INSTANCE.mapUserFromUserUpdateRequest(userUpdateRequest, user);
                    log.info("After mapping: {}", user);
                    return userRepository.save(user);
                });
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> getCurrentUser(String token) {
        log.info("Getting current user from token: {}", token);
        log.info("Subject: {}", jwtUtil.getClaimsFromToken(token).getSubject());
        return userRepository.findById(UUID.fromString(jwtUtil.getClaimsFromToken(token).getSubject()));
    }
    
}
