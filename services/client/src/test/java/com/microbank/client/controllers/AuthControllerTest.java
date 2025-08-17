// package com.microbank.client.controllers;

// import com.microbank.client.dto.LoginRequest;
// import com.microbank.client.entity.User;
// import com.microbank.client.services.AuthService;
// import com.microbank.client.utils.JWTUtil;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
// import org.springframework.http.HttpStatus;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.reactive.server.WebTestClient;
// import reactor.core.publisher.Mono;

// import java.time.Duration;
// import java.util.UUID;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;

// @WebFluxTest(AuthController.class)
// class AuthControllerTest {

//     @Autowired
//     private WebTestClient webTestClient;

//     @MockitoBean
//     private AuthService authService;

//     @MockitoBean
//     private JWTUtil jwtUtil;

//     private User testUser;

//     @BeforeEach
//     void setup() {
//         testUser = new User();
//         testUser.setId(UUID.randomUUID());
//         testUser.setEmail("test@example.com");
//     }

//     @Test
//     void register_shouldReturnCreated() {
//         Mockito.when(authService.register(any(User.class))).thenReturn(Mono.just(testUser));

//         webTestClient.post()
//                 .uri("/api/v1/auth/register")
//                 .bodyValue(testUser)
//                 .exchange()
//                 .expectStatus().isCreated()
//                 .expectBody(User.class)
//                 .isEqualTo(testUser);
//     }

//     @Test
//     void login_shouldReturnUserAndSetCookie() {
//         LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
//         Mockito.when(authService.login(eq(loginRequest.getEmail()), eq(loginRequest.getPassword())))
//                 .thenReturn(Mono.just(testUser));
//         Mockito.when(jwtUtil.generateToken(testUser)).thenReturn("jwt-token");

//         webTestClient.post()
//                 .uri("/api/v1/auth/login")
//                 .bodyValue(loginRequest)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectCookie().valueEquals("auth_token", "jwt-token")
//                 .expectBody(User.class)
//                 .isEqualTo(testUser);
//     }

//     @Test
//     void logout_shouldClearCookie() {
//         UUID userId = testUser.getId();
//         Mockito.when(authService.logout("auth_token", userId)).thenReturn(Mono.empty());

//         webTestClient.post()
//                 .uri("/api/v1/auth/logout")
//                 .bodyValue(userId)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectCookie().maxAge("auth_token", Duration.ZERO);
//     }
// }
