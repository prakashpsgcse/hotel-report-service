package com.target.report.generator.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublishService implements PublishService {
    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublishService.class);

    @Autowired
    private Producer<byte[], byte[]> kafkaProducer;

    /**
     * sends key ,value to kafka
     */
    @Override
    public void publish(String key, String value, String topic) {
        LOGGER.debug("Sending key:{} and value:{} to topic:{}", key, value, topic);
        byte[] keyInBytes = key.getBytes();
        byte[] valueInBytes = value.getBytes();
        Future<RecordMetadata> response = kafkaProducer.send(new ProducerRecord(topic, keyInBytes, valueInBytes));
        RecordMetadata record;
        try {
            record = response.get();
            LOGGER.info("Published topic: {}, Partition: {}, Offset: {}", record.topic(), record.partition(), record.offset());
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occured in publishing  to  kafka {}", e);
            throw new KafkaException("Error occured in publishing  to  kafka", e);
        }

    }

    /**
     * Flushing any data that is in producer and closing the KafkaProducer
     * before shutdown
     * 
     * @throws Exception
     */
    @PreDestroy
    public void cleanUp() throws Exception {
        LOGGER.info("Flushing and closing kafka producer");
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
