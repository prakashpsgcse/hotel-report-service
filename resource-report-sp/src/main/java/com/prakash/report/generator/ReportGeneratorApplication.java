package com.prakash.report.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.prakash.report.generator.stream.launcher.StreamsLauncher;

/*
 * Main class to start application and launch stream.
 */
@SpringBootApplication
@EnableMongoRepositories
public class ReportGeneratorApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportGeneratorApplication.class);

    @Autowired
    private StreamsLauncher kafkaStreamsLauncher;

    public static void main(String[] args) {
        LOGGER.info("Starting Reporting application");
        SpringApplication.run(ReportGeneratorApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        kafkaStreamsLauncher.launch();
    }

}
