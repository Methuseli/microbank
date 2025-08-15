package com.microbank.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.microbank.client.repository.BlacklistedTokenRepository;
import com.microbank.client.repository.UserRepository;
import com.microbank.client.services.AuthService;
import com.microbank.client.services.AuthServiceImpl;
import com.microbank.client.services.UserService;
import com.microbank.client.services.UserServiceImpl;
import com.microbank.client.utils.JWTUtil;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated())
                .build();
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    @Bean
    public AuthService authService() {
        return new AuthServiceImpl(userRepository, blacklistedTokenRepository, passwordEncoder());
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
