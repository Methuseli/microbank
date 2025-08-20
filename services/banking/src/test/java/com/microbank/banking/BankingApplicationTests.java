package com.microbank.banking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.microbank.banking.repository.BankAccountRepository;
import com.microbank.banking.repository.BankTransactionRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1",
    "spring.r2dbc.username=sa",
    "spring.r2dbc.password=",
    "SPRING_R2DBC_URL=testdb", // Resolve the placeholder
    "spring.sql.init.mode=never",
    "spring.kafka.bootstrap-servers=localhost:9092", // Provide a Kafka bootstrap server
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration", // Optionally exclude Kafka if not needed for this test
    "SPRING_JWT_SECRET=jwt_secret"
})
@Testcontainers
class BankingApplicationTests {

    @Container
    static final PostgreSQLContainer<?> POSTGRES
            = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    // Map container runtime values into Spring properties for R2DBC
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        String r2dbcUrl = String.format("r2dbc:postgresql://%s:%d/%s",
                POSTGRES.getHost(),
                POSTGRES.getFirstMappedPort(),
                POSTGRES.getDatabaseName());

        registry.add("spring.r2dbc.url", () -> r2dbcUrl);
        registry.add("spring.r2dbc.username", POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES::getPassword);

        // Ensure SQL init runs against Postgres if you rely on schema/data SQL files
        registry.add("spring.sql.init.mode", () -> "always");
        // Optionally disable Flyway if it conflicts with your sql scripts:
        // registry.add("spring.flyway.enabled", () -> "false");
    }

	@MockitoBean
	BankAccountRepository bankAccountRepository;

	@MockitoBean
	BankTransactionRepository bankTransactionRepository;

    @Test
    void contextLoads() {
    }

}
