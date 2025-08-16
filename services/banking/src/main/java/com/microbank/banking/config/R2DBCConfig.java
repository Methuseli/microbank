package com.microbank.banking.config;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableTransactionManagement
public class R2DBCConfig {

    // @Value("${spring.r2dbc.username}")
    // private String username;

    // @Value("${spring.r2dbc.password}")
    // private String password;

    @Bean
    ConnectionFactoryInitializer initializer(
            ConnectionFactory connectionFactory
    ) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        ResourceDatabasePopulator resource = new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")
        );
        initializer.setDatabasePopulator(resource);
        return initializer;
    }

    @Bean
    ReactiveTransactionManager transactionManager(
            ConnectionFactory connectionFactory
    ) {
        return new R2dbcTransactionManager(connectionFactory);
    }

}

