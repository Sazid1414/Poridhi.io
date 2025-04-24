package com.ecommerce.product.config;

import com.ecommerce.product.event.ProductEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

@Configuration
public class KafkaConfig {

    @Autowired
    private ProducerFactory<String, ProductEvent> producerFactory;

    @Bean
    public KafkaTemplate<String, ProductEvent> kafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerFactory.getConfigurationProperties()));
    }
}