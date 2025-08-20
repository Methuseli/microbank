package com.microbank.client.controllers;

import com.microbank.client.config.SecurityConfig;
import com.microbank.client.dto.LoginRequest;
import com.microbank.client.dto.LogoutRequest;
import com.microbank.client.entity.User;
import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.services.AuthService;
import com.microbank.client.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(com.microbank.client.controllers.AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthService authService;

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
    void register_shouldReturnCreated() {
        // authService.register(...) is used for side-effect; controller returns created with provided user
        Mockito.when(authService.register(any(User.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/client/api/v1/auth/register")
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .isEqualTo(testUser);
    }

    @Test
    void login_shouldReturnTokenInBody_andNotUseCookie() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");

        Mockito.when(authService.login(eq(loginRequest.getEmail()), eq(loginRequest.getPassword())))
                .thenReturn(Mono.just(testUser));
        Mockito.when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

        webTestClient.post()
                .uri("/client/api/v1/auth/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                // token returned in response body (no cookies expected)
                .expectBody(String.class)
                .isEqualTo("jwt-token");
    }

    @Test
    void logout_shouldReturnUnauthorized_whenMissingAuthorizationHeader() {
        // Build a LogoutRequest payload
        LogoutRequest logoutRequest = new LogoutRequest(testUser.getId());

        webTestClient.post()
                .uri("/client/api/v1/auth/logout")
                .bodyValue(logoutRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void logout_shouldReturnOk_whenAuthorizationHeaderPresent() {
        // Prepare token and request payload
        String token = "jwt-token";
        LogoutRequest logoutRequest = new LogoutRequest(testUser.getId());

        io.jsonwebtoken.Claims claims = Mockito.mock(io.jsonwebtoken.Claims.class);
        when(claims.getSubject()).thenReturn(testUser.getEmail());          // subject used as username
        when(claims.get("role", String.class)).thenReturn("USER");         // role used to build authorities
        when(jwtUtil.getClaimsFromToken(token)).thenReturn(claims);

        // authService.logout expects (token, userId) per your controller signature
        Mockito.when(authService.logout(eq(token), eq(logoutRequest.getUserId()))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/client/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token)
                .bodyValue(logoutRequest)
                .exchange()
                .expectStatus().isOk();
    }
}
