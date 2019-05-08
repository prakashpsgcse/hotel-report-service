package com.prakash.report.generator.service;

/*
 * This interface should be implemented to publish events from api .Now it supports KafkaPublishService implementation .
 * Implement this interface for other publish-subscribe platform like Kinisis and use @ConditionalOnProperty to load correct config
 */
public interface PublishService {

    public void publish(String key, String value, String topic);

}
