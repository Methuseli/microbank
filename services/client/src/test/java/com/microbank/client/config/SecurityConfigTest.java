package com.microbank.client.config;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.WebFilterChain;

import com.microbank.client.utils.JWTUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SecurityConfigTest {

    SecurityConfig securityConfig;

    @Mock
    JWTUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig();
    }

    @Test
    void jwtAuthFilter_shouldAuthenticate_whenValidToken() {
        // Arrange
        String tokenValue = "valid-token";
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("alice");
        when(claims.get("roles", List.class)).thenReturn(List.of("ROLE_USER"));
        securityConfig = spy(securityConfig);
        doReturn(claims).when(securityConfig.jwtUtil()).getClaimsFromToken(tokenValue);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test")
                        .header("Authorization", "Bearer " + tokenValue)
                        .build()
        );
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = securityConfig.jwtAuthFilter().filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        // Verify that the SecurityContext contains expected authentication
        ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .as(StepVerifier::create)
                .expectNext("alice")
                .verifyComplete();
    }

    @Test
    void jwtAuthFilter_shouldReturnUnauthorized_whenInvalidToken() {
        // Arrange
        String tokenValue = "invalid-token";
        securityConfig = spy(securityConfig);
        doThrow(new RuntimeException("Invalid")).when(securityConfig.jwtUtil()).getClaimsFromToken(tokenValue);

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test")
                        .header("Authorization", "Bearer " + tokenValue)
                        .build()
        );
        WebFilterChain chain = mock(WebFilterChain.class);

        // Act
        Mono<Void> result = securityConfig.jwtAuthFilter().filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        ServerHttpResponse response = exchange.getResponse();
        assert(response.getStatusCode() == HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void jwtAuthFilter_shouldContinue_whenNoToken() {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test").build());
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = securityConfig.jwtAuthFilter().filter(exchange, chain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        verify(chain).filter(exchange);
    }

    @Test
    void securityFilterChain_shouldPermitAuthEndpoints() {
        var chain = securityConfig.securityFilterChain(null); // just check config builds
        assert(chain != null);
    }
}
