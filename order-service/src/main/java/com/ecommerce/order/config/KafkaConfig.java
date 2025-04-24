package com.ecommerce.order.config;

import com.ecommerce.order.event.OrderEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

@Configuration
public class KafkaConfig {

    @Autowired
    private ProducerFactory<String, OrderEvent> producerFactory;

    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerFactory.getConfigurationProperties()));
    }
}