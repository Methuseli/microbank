package com.microbank.client.services;

import java.util.UUID;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.utils.UserMapper;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUser(UserUpdateRequest userUpdateRequest, UUID userId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    UserMapper.INSTANCE.mapUserToUserUpdateRequest(userUpdateRequest, user);
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
    
}
