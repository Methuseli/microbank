package com.microbank.banking.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microbank.banking.repository.BankAccountRepository;
import com.microbank.banking.repository.BankTransactionRepository;
import com.microbank.banking.services.BankAccountService;
import com.microbank.banking.services.BankAccountServiceImpl;
import com.microbank.banking.services.BankTransactionService;
import com.microbank.banking.services.BankTransactionServiceImpl;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaConfig {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ReceiverOptions<String, String> receiverOptions() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "banking-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return ReceiverOptions.create(props);
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver() {
        return KafkaReceiver.create(receiverOptions().subscription(Collections.singleton("user-created")));
    }

    @Bean
    public BankAccountService bankAccountService() {
        return new BankAccountServiceImpl(bankAccountRepository, bankTransactionRepository, kafkaReceiver());
    }

    @Bean
    public BankTransactionService bankTransactionService() {
        return new BankTransactionServiceImpl(bankTransactionRepository);
    }
}
