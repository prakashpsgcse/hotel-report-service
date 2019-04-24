package com.target.report.generator.configs;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
    @Value("${kafka.broker.url}")
    private String brokerUrl;

    @Value("${kafka.acks}")
    private String acks;

    @Value("${kafka.key.serializer}")
    private String keySerializer;

    @Value("${kafka.value.serializer}")
    private String valueSerializer;

    private Properties getKafkaProducerProps() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        kafkaProperties.put(ProducerConfig.ACKS_CONFIG, acks);
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        LOGGER.debug("Kafka properties {} ", kafkaProperties);
        return kafkaProperties;
    }

    @Bean
    public <K, V> Producer<K, V> getProducer() {

        Producer<K, V> kafkaProducer = new KafkaProducer<>(getKafkaProducerProps());
        LOGGER.debug("Kakfa producer created ");
        return kafkaProducer;
    }

}
