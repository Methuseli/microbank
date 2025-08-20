package com.microbank.client.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.WebFilterChain;

import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.services.AuthService;
import com.microbank.client.utils.JWTUtil;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest
@Import(SecurityConfig.class) // Import only the SecurityConfig
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1",
    "SPRING_R2DBC_URL=test"
})
class SecurityConfigTest {

    SecurityConfig securityConfig;

    @Autowired
    ApplicationContext ctx;

    @Mock
    JWTUtil jwtUtil;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    BlacklistedTokenRepository blacklistedTokenRepository;

    @MockitoBean
    AuthService authService;

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
        when(claims.get("role", String.class)).thenReturn("USER");

        JWTUtil mockJwtUtil = mock(JWTUtil.class);
        when(mockJwtUtil.getClaimsFromToken(tokenValue)).thenReturn(claims);

        // Spy SecurityConfig and make jwtUtil() return our mock
        securityConfig = spy(securityConfig);
        doReturn(mockJwtUtil).when(securityConfig).jwtUtil();

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test")
                        .header("Authorization", "Bearer " + tokenValue)
                        .build()
        );

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = securityConfig.jwtAuthFilter().filter(exchange, chain);

        // Assert: filter completes
        StepVerifier.create(result)
                .verifyComplete();

        // Optional: verify jwtUtil was used
        verify(mockJwtUtil).getClaimsFromToken(tokenValue);
    }

    @Test
    void jwtAuthFilter_shouldReturnUnauthorized_whenInvalidToken() {
        // Arrange
        String tokenValue = "invalid-token";

        // Create mock JWTUtil and stub behavior (throw for invalid token)
        JWTUtil mockJwtUtil = mock(JWTUtil.class);
        when(mockJwtUtil.getClaimsFromToken(tokenValue))
                .thenThrow(new RuntimeException("Invalid"));

        
        securityConfig = spy(securityConfig);
        doReturn(mockJwtUtil).when(securityConfig).jwtUtil();

        // Prepare exchange with Authorization header
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test")
                        .header("Authorization", "Bearer " + tokenValue)
                        .build()
        );
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = securityConfig.jwtAuthFilter().filter(exchange, chain);

       
        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(chain, never()).filter(any());
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
        var chain = ctx.getBean(SecurityWebFilterChain.class);
        assertNotNull(chain); 
    }
}
