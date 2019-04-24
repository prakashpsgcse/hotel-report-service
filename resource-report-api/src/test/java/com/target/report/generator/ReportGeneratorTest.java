package com.target.report.generator;

import java.io.IOException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.target.report.generator.dao.HotelUserDAO;
import com.target.report.generator.dao.ResourceDAO;
import com.target.report.generator.domain.HotelResourceInfo;
import com.target.report.generator.domain.HotelUser;
import com.target.report.generator.domain.ResourceReport;
import com.target.report.generator.security.HotelUserCredential;
import com.target.report.generator.utils.KafkaConsumerClient;
import com.target.report.generator.utils.SingleNodeKafkaCluster;

/**
 * Integration testcase for report api .This starts Embedded kafka ,Zookeeper
 * ,mongo first . Testcase sends sample event to kafka and checks kafka for the
 * same event .Testcase saved sample data into Mongo and using api it it checks
 * it received report
 * 
 * @author pprakash
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ReportGeneratorTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorTest.class);

    public static final ObjectMapper mapper = new ObjectMapper();

    @Value("${kafka.report.topic}")
    private String kafkaTopic;

    @ClassRule
    public static final SingleNodeKafkaCluster kafkaCluster = new SingleNodeKafkaCluster();
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private KafkaConsumerClient kafkaConsumerClient;

    @LocalServerPort
    private int port;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private HotelUserDAO hotelUserDAO;

    private String jwtToken;

    /*
     * Testcase sends sample event to kafka and checks kafka for the same event
     */
    @Test
    public void testPublishResourceToKafka() throws JsonParseException, JsonMappingException, IOException {

        hotelUserDAO.deleteAll();
        HotelResourceInfo event = mapper.readValue(ReportGeneratorTest.class.getResourceAsStream("/SampleResource.json"),
                HotelResourceInfo.class);
        String jwt = addHotelUserAndGenerateJWT();
        String payLoad = mapper.writeValueAsString(event);
        LOGGER.debug("Publishing  resource {}  ", payLoad);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<String>(payLoad, headers);
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/v1.0/hotel/resource/hotel123",
                HttpMethod.POST, entity, String.class);
        String publishResponse = response.getBody();
        LOGGER.debug("Response for publishing  resource {}  ", publishResponse);
        Assert.assertTrue("Resource report not published", response.getStatusCode().is2xxSuccessful());

        String eventFromKafka = kafkaConsumerClient.readKafkaValue("reports");
        LOGGER.debug("Event from Kafka {}  ", eventFromKafka);
        HotelResourceInfo eventPublished = mapper.readValue(eventFromKafka, HotelResourceInfo.class);
        Assert.assertTrue("Hotel id not matched ", eventPublished.getHotelId().equals(event.getHotelId()));
        Assert.assertTrue("Hotel id not matched ", eventPublished.getHotelName().equals(event.getHotelName()));
        Assert.assertTrue("Incorrect resources received ", eventPublished.getResources().size() == 3);

    }

    /*
     * Testcase save sample data into Mongo and using api it it checks it
     * received report
     */
    @Test
    public void testReportGeneration() throws JsonParseException, JsonMappingException, IOException {
        hotelUserDAO.deleteAll();
        String jwt = addHotelUserAndGenerateJWT();
        HotelResourceInfo event = mapper.readValue(ReportGeneratorTest.class.getResourceAsStream("/SampleResource.json"),
                HotelResourceInfo.class);
        resourceDAO.save(event);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> response = testRestTemplate.exchange(
                "http://localhost:" + port + "/v1.0/hotel/report/hotel123?startTimeStamp=1555784697000&endTimeStamp=1555784698000",
                HttpMethod.GET, entity, String.class);
        LOGGER.debug("Received report {}  ", response.getBody());
        Assert.assertTrue("Reports not received", response.getStatusCode().is2xxSuccessful());
        ResourceReport report = mapper.readValue(response.getBody(), ResourceReport.class);
        Assert.assertTrue("Reports received for incorrect hotel id", report.getHotelId().equals(event.getHotelId()));
        Assert.assertTrue("Reports received for incorrect resource inf0", report.getResources().size() == 3);

    }

    @Test
    public void testInvalidPayload() throws JsonParseException, JsonMappingException, IOException {
        hotelUserDAO.deleteAll();
        String jwt = addHotelUserAndGenerateJWT();
        HotelResourceInfo event = mapper.readValue(ReportGeneratorTest.class.getResourceAsStream("/InvalidResource.json"),
                HotelResourceInfo.class);

        String payLoad = mapper.writeValueAsString(event);
        LOGGER.debug("Publishing  resource {}  ", payLoad);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<String>(payLoad, headers);
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/v1.0/hotel/resource/abc123",
                HttpMethod.POST, entity, String.class);
        String publishResponse = response.getBody();
        LOGGER.debug("Response for publishing  resource {}  ", publishResponse);
        Assert.assertTrue("Resource report not published", response.getStatusCode().is4xxClientError());

        Assert.assertTrue(publishResponse.contains("HotelName cannot be empty"));
        Assert.assertTrue(publishResponse.contains("Date cannot be empty"));

    }

    public String addHotelUserAndGenerateJWT() throws JsonProcessingException {
        HotelUser user = new HotelUser();
        user.setHotelId("hotel123");
        user.setPassword("password");
        user.setRole("user");
        user.setUsername("admin");
        hotelUserDAO.save(user);
        LOGGER.debug("Creating test user {} for login ", user);
        HotelUserCredential loginUser = new HotelUserCredential();
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());

        // generate JWT
        String payLoad = mapper.writeValueAsString(loginUser);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<String>(payLoad, headers);
        ResponseEntity<String> response = testRestTemplate.exchange("http://localhost:" + port + "/login",
                HttpMethod.POST, entity, String.class);
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        String auth = response.getHeaders().get("Authorization").get(0);
        jwtToken = auth.replace("Bearer", "");
        LOGGER.debug("JWT {} for user  ", jwtToken);
        return jwtToken;
    }
}
