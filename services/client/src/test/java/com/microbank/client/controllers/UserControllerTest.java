package com.microbank.client.controllers;

import com.microbank.client.config.SecurityConfig;
import com.microbank.client.dto.UserUpdateRequest;
import com.microbank.client.entity.User;
import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.services.UserService;
import com.microbank.client.utils.JWTUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    BlacklistedTokenRepository blacklistedTokenRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Test User");
        testUser.setPassword("hashed_password");
        testUser.setEmail("test@example.com");
    }

    @Test
    void getUserById_shouldReturnUser() {
        Mockito.when(userService.getUserById(testUser.getId())).thenReturn(Mono.just(testUser));

        webTestClient.mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/client/api/v1/users/{id}", testUser.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(testUser);
    }

    @Test
    void updateUser_shouldReturnUnauthorizedIfRoleOrBlacklistPresent() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setRole("ADMIN"); // triggers 401
        webTestClient.mutateWith(mockUser().roles("USER"))
                .patch()
                .uri("/client/api/v1/users/{id}", testUser.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Updated Name");
        Mockito.when(userService.updateUser(Mockito.any(UserUpdateRequest.class), Mockito.eq(testUser.getId())))
                .thenReturn(Mono.just(testUser));

        webTestClient
                .mutateWith(mockUser().roles("USER"))
                .patch()
                .uri("/client/api/v1/users/{id}", testUser.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(testUser);
    }

    @Test
    void deleteUser_shouldReturnNoContent() {
        Mockito.when(userService.deleteUser(testUser.getId())).thenReturn(Mono.empty());
        webTestClient
                .mutateWith(mockUser().roles("USER"))
                .delete()
                .uri("/client/api/v1/users/{id}", testUser.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getAllUsers_shouldReturnFluxOfUsers() {
        Mockito.when(userService.getAllUsers()).thenReturn(Flux.just(testUser));
        webTestClient
                .mutateWith(mockUser().roles("ADMIN"))
                .get()
                .uri("/client/api/v1/users/admin")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .contains(testUser);
    }

    // @Test
    // void getCurrentUser_shouldReturnUser() {
    //     String token = "jwt-token";
    //     Mockito.when(userService.getCurrentUser(token)).thenReturn(Mono.just(testUser));

    //     webTestClient
    //             .mutateWith(mockUser().roles("USER"))
    //             .get()
    //             .uri("/client/api/v1/users/current-user")
    //             .header("Authorization", "Bearer jwt-token") // <-- add this
    //             .exchange()
    //             .expectStatus().isOk()
    //             .expectBody(User.class)
    //             .isEqualTo(testUser);
    // }

}
