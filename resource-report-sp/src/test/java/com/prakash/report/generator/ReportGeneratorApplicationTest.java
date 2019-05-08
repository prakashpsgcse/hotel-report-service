package com.prakash.report.generator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prakash.report.generator.dao.ResourceDAO;
import com.prakash.report.generator.domain.HotelResourceInfo;
import com.prakash.report.generator.utils.KafkaConsumerClient;
import com.prakash.report.generator.utils.SingleNodeKafkaCluster;

/**
 * Integration test case to check Stream Processor works or not .This
 * integration tests starts Embedded Kafka and zookeeper on port 9092,2181.Then
 * it sends same event (which will be trigger from resource-api ) to kafka .Then
 * checks in MongoDB
 * 
 * @author pprakash
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ReportGeneratorApplicationTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplicationTest.class);

    ObjectMapper mapper = new ObjectMapper();

    @Value("${kafka.report.topic}")
    private String kafkaTopic;

    @ClassRule
    public static final SingleNodeKafkaCluster kafkaCluster = new SingleNodeKafkaCluster();

    @Autowired
    private KafkaConsumerClient kafkaConsumerClient;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private Producer<byte[], byte[]> kafkaProducer;

    @BeforeClass
    public static void createTopic() {
        kafkaCluster.createTopic("reports");
    }

    /*
     * This test case sends sample event to Kafka and checks in mongoDb for
     * same.It will be saved if streamprocessor processed it properly
     */
    @Test
    public void publishEventTokafka() throws JsonParseException, JsonMappingException, IOException, InterruptedException {

        HotelResourceInfo expectedEvent = mapper.readValue(ReportGeneratorApplicationTest.class.getResourceAsStream("/SampleResource.json"),
                HotelResourceInfo.class);

        LOGGER.debug("sending resource event {} to kafka ", expectedEvent);
        String payLoad = mapper.writeValueAsString(expectedEvent);
        byte[] keyInBytes = "hot123".getBytes();
        byte[] valueInBytes = payLoad.getBytes();
        Future<RecordMetadata> response = kafkaProducer.send(new ProducerRecord(kafkaTopic, keyInBytes, valueInBytes));
        RecordMetadata record;
        try {
            record = response.get();
            LOGGER.debug("Published topic: {}, Partition: {}, Offset: {}", record.topic(), record.partition(), record.offset());
            LOGGER.debug("Resource event {} send to kafka ", expectedEvent);
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error occured in publishing  to  kafka {}", e);
        }
        Thread.sleep(10000);
        LOGGER.debug("Checking DB for any resource event ");
        HotelResourceInfo eventStored = resourceDAO.findAll().get(0);
        LOGGER.debug("Resource event {} from DB ", eventStored);
        Assert.assertTrue(eventStored.getHotelId().equals(expectedEvent.getHotelId()));
        Assert.assertTrue(eventStored.getHotelName().equals(expectedEvent.getHotelName()));
        Assert.assertTrue(eventStored.getTimeStamp().equals(expectedEvent.getTimeStamp()));
        Assert.assertTrue(eventStored.getResources().size() == 3);
    }
}
