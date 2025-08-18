package com.microbank.client.services;

import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .email("alice@example.com")
                .password("secret")
                .role("USER")
                .build();
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.getUserById(testUser.getId()))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void updateUser_shouldUpdateFields() {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("Updated Alice");

        when(userRepository.findById(testUser.getId())).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(userService.updateUser(req, testUser.getId()))
                .expectNextMatches(user -> user.getName().equals("Updated Alice"))
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_shouldComplete() {
        when(userRepository.deleteById(testUser.getId())).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(testUser.getId()))
                .verifyComplete();

        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void getAllUsers_shouldReturnFluxOfUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));

        StepVerifier.create(userService.getAllUsers())
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getCurrentUser_shouldReturnUserFromToken() {
        UUID id = testUser.getId();
        when(jwtUtil.getClaimsFromToken("valid-token"))
                .thenReturn(io.jsonwebtoken.Jwts.claims().setSubject(id.toString()));
        when(userRepository.findById(id)).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.getCurrentUser("valid-token"))
                .expectNext(testUser)
                .verifyComplete();
    }
}
