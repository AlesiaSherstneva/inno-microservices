package com.innowise.orderservice.config;

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
    @Value("${orders.events.topic}")
    private String ordersEventsTopic;

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
    NewTopic createOrdersEventsTopic() {
        return TopicBuilder.name(ordersEventsTopic)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}