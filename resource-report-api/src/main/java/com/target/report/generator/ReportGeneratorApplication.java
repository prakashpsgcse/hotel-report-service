package com.target.report.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ReportGeneratorApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplication.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting hotel resource management service");
        SpringApplication.run(ReportGeneratorApplication.class, args);
    }
}
