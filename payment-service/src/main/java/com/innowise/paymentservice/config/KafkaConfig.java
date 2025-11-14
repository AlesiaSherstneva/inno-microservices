package com.innowise.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Value("${payments.events.topic}")
    private String paymentsEventsTopic;

    @Value("${kafka.topic.properties.partitions}")
    private Integer partitions;

    @Value("${kafka.topic.properties.replication-factor}")
    private Integer replicationFactor;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(
            @Autowired(required = false) ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    NewTopic createProductsEventTopic() {
        return TopicBuilder.name(paymentsEventsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}