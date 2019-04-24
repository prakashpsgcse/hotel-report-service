package com.target.report.generator.utils;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerClient {
    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerClient.class);
    @Value("${kafka.broker.url}")
    private String brokerUrl;

    @Value("${kafka.acks}")
    private String acks;

    @Value("${kafka.key.deserializer}")
    private String keyDeserializer;

    @Value("${kafka.value.deserializer}")
    private String valueDeserializer;

    public String readKafkaValue(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        int pollIntervalMs = 100;
        int maxTotalPollTimeMs = 2000;
        int totalPollTimeMs = 0;
        String kafkaResponse = "";
        while (totalPollTimeMs < maxTotalPollTimeMs) {
            totalPollTimeMs += pollIntervalMs;
            ConsumerRecords<byte[], byte[]> records = consumer.poll(pollIntervalMs);
            if (records.count() > 0) {
                String response = new String(records.iterator().next().value());
                kafkaResponse = kafkaResponse + response;
            }
        }
        consumer.close();
        return kafkaResponse;

    }

}
