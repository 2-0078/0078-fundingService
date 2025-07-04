package com.pieceofcake.fundingservice.kafka.config;

import com.pieceofcake.fundingservice.kafka.producer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> productProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, FundingEvent> createFundingNotification() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FundingEvent> kafkaTemplate() {
        return new KafkaTemplate<>(createFundingNotification());
    }

    @Bean
    public ProducerFactory<String, RefundEvent> createRefundNotification() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, RefundEvent> refundKafkaTemplate() {
        return new KafkaTemplate<>(createRefundNotification());
    }

    @Bean
    public ProducerFactory<String, FundingRemainPieceEvent> createFundingRemainPiecesNotification() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, FundingRemainPieceEvent> fundingKafkaTemplate() {
        return new KafkaTemplate<>(createFundingRemainPiecesNotification());
    }

    @Bean
    public ProducerFactory<String, CompletedFundingEvent> createCompletedFundingNotification() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, CompletedFundingEvent> completedFundingKafkaTemplate() {
        return new KafkaTemplate<>(createCompletedFundingNotification());
    }

    @Bean
    public ProducerFactory<String, AlertEvent> createAlertNotification() {
        return new DefaultKafkaProducerFactory<>(productProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, AlertEvent> alertKafkaTemplate() {
        return new KafkaTemplate<>(createAlertNotification());
    }
}